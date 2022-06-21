package com.jd.bluedragon.distribution.spotcheck.handler;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.SpotCheckQueryManager;
import com.jd.bluedragon.core.base.SpotCheckServiceProxy;
import com.jd.bluedragon.core.base.WaybillTraceManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.send.domain.dto.SendDetailDto;
import com.jd.bluedragon.distribution.send.service.SendDetailService;
import com.jd.bluedragon.distribution.spotcheck.domain.*;
import com.jd.bluedragon.distribution.spotcheck.enums.*;
import com.jd.bluedragon.distribution.spotcheck.service.SpotCheckDealService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.report.domain.spotcheck.SpotCheckQueryCondition;
import com.jd.ql.dms.report.domain.spotcheck.WeightVolumeSpotCheckDto;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;


/**
 * dws抽检
 *
 * @author hujiping
 * @date 2021/8/10 10:52 上午
 */
@Service("dwsSpotCheckHandler")
public class DwsSpotCheckHandler extends AbstractSpotCheckHandler {

    @Autowired
    private SpotCheckDealService spotCheckDealService;

    @Autowired
    private WaybillTraceManager waybillTraceManager;

    @Autowired
    private SendDetailService sendDetailService;

    @Autowired
    private SpotCheckQueryManager spotCheckQueryManager;

    @Autowired
    private SpotCheckServiceProxy spotCheckServiceProxy;

    @Autowired
    @Qualifier("dwsIssueDealProducer")
    private DefaultJMQProducer dwsIssueDealProducer;

    @Override
    protected InvokeResult<SpotCheckResult> checkIsExcessReform(SpotCheckContext spotCheckContext) {
        if(!spotCheckContext.getIsMultiPack()){
            return super.checkIsExcessReform(spotCheckContext);
        }
        InvokeResult<SpotCheckResult> result = new InvokeResult<SpotCheckResult>();
        SpotCheckResult spotCheckDto = new SpotCheckResult();
        spotCheckDto.setExcessStatus(ExcessStatusEnum.EXCESS_ENUM_COMPUTE.getCode());
        spotCheckDto.setIsMultiPack(spotCheckContext.getIsMultiPack());
        result.setData(spotCheckDto);
        if(spotCheckDealService.gatherTogether(spotCheckContext)){
            return spotCheckDealService.checkIsExcessReform(spotCheckContext);
        }
        return result;
    }

    @Override
    protected void reformCheck(SpotCheckContext spotCheckContext, InvokeResult<Boolean> result) {
        if(!spotCheckContext.getIsMultiPack()){
            super.reformCheck(spotCheckContext, result);
            return;
        }
        String packageCode = spotCheckContext.getPackageCode();
        String waybillCode = WaybillUtil.getWaybillCode(packageCode);
        // 纯配外单校验
        if(!BusinessUtil.isPurematch(spotCheckContext.getWaybill().getWaybillSign())){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, SpotCheckConstants.SPOT_CHECK_ONLY_SUPPORT_PURE_MATCH);
            return;
        }
        // 泡重比校验
        if(Objects.equals(spotCheckContext.getSpotCheckBusinessType(), SpotCheckBusinessTypeEnum.SPOT_CHECK_TYPE_B.getCode())
                && weightVolumeRatioCheck(spotCheckContext, result)){
            return;
        }
        // 是否妥投
        if(waybillTraceManager.isWaybillFinished(waybillCode)){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, SpotCheckConstants.SPOT_CHECK_FORBID_FINISHED_PACK);
            return;
        }
        // 发货抽检校验
        reformSendSpotCheck(spotCheckContext, result);
    }

    @Override
    protected void afterCheckDealReform(SpotCheckDto spotCheckDto, SpotCheckContext spotCheckContext, InvokeResult<Boolean> result) {
        if(!spotCheckContext.getIsMultiPack()){
            super.afterCheckDealReform(spotCheckDto, spotCheckContext, result);
            return;
        }
        if(StringUtils.isEmpty(spotCheckDealService.spotCheckPackSetStr(spotCheckContext.getWaybillCode(), spotCheckContext.getReviewSiteCode()))){
            spotCheckServiceProxy.insertOrUpdateProxyReform(initSummaryDto(spotCheckContext));
        }
        // 集齐后
        if(spotCheckDealService.gatherTogether(spotCheckContext)){
            spotCheckContext.setIsGatherTogether(true);
            // 1、汇总复核数据
            summaryReviewWeightVolume(spotCheckContext);
            // 获取核对数据
            spotCheckDealService.assembleContrastData(spotCheckContext);
            // 设置已抽检缓存
            setSpotCheckCache(spotCheckContext.getWaybillCode(), spotCheckContext.getExcessStatus());
            // 2、更新总记录
            WeightVolumeSpotCheckDto summaryDto = assembleSummaryReform(spotCheckContext);
            spotCheckServiceProxy.insertOrUpdateProxyReform(summaryDto);
            // 3、下发超标数据
            if(Objects.equals(spotCheckContext.getExcessStatus(), ExcessStatusEnum.EXCESS_ENUM_YES.getCode())){
                if(spotCheckDealService.isExecuteDwsAIDistinguish(spotCheckContext.getReviewSiteCode())){
                    // 发消息来单独处理dws的一单多件的下发逻辑
                    dwsIssueDealProducer.sendOnFailPersistent(spotCheckContext.getWaybillCode(), JsonHelper.toJson(summaryDto));
                }else {
                    spotCheckDealService.executeIssue(summaryDto);
                }
            }
        }
        // 包裹明细数据落库
        spotCheckServiceProxy.insertOrUpdateProxyReform(assembleDetailReform(spotCheckContext));
        // 设置包裹维度缓存
        setSpotCheckPackCache(spotCheckContext.getPackageCode(), spotCheckContext.getReviewSiteCode());
        // 抽检全程跟踪
        spotCheckDealService.sendWaybillTrace(spotCheckContext);
    }

    private WeightVolumeSpotCheckDto initSummaryDto(SpotCheckContext spotCheckContext) {
        // 初始化总记录
        WeightVolumeSpotCheckDto initSummaryDto = new WeightVolumeSpotCheckDto();
        initSummaryDto.setReviewDate(System.currentTimeMillis());
        initSummaryDto.setWaybillCode(spotCheckContext.getWaybillCode());
        initSummaryDto.setPackageCode(spotCheckContext.getWaybillCode());
        initSummaryDto.setReviewSiteCode(spotCheckContext.getReviewSiteCode());
        BaseStaffSiteOrgDto reviewSite = spotCheckContext.getReviewSite();
        initSummaryDto.setSiteTypeName(reviewSite == null
                ? null : Objects.equals(reviewSite.getSiteType(), Constants.DMS_SITE_TYPE) ? Constants.CONSTANT_NUMBER_ONE : Constants.NUMBER_ZERO);
        initSummaryDto.setProductTypeName(spotCheckContext.getProductTypeName());
        initSummaryDto.setMerchantCode(spotCheckContext.getMerchantCode());
        initSummaryDto.setMerchantName(spotCheckContext.getMerchantName());
        initSummaryDto.setIsTrustMerchant(spotCheckContext.getIsTrustMerchant() ? Constants.CONSTANT_NUMBER_ONE : Constants.NUMBER_ZERO);
        initSummaryDto.setReviewSource(SpotCheckSourceFromEnum.analysisCodeFromName(spotCheckContext.getSpotCheckSourceFrom()));
        SpotCheckReviewDetail spotCheckReviewDetail = spotCheckContext.getSpotCheckReviewDetail();
        initSummaryDto.setReviewOrgCode(spotCheckReviewDetail.getReviewOrgId());
        initSummaryDto.setReviewOrgName(spotCheckReviewDetail.getReviewOrgName());
        initSummaryDto.setReviewSiteCode(spotCheckReviewDetail.getReviewSiteCode());
        initSummaryDto.setReviewSiteName(spotCheckReviewDetail.getReviewSiteName());
        initSummaryDto.setReviewUserErp(spotCheckReviewDetail.getReviewUserErp());
        initSummaryDto.setReviewUserName(spotCheckReviewDetail.getReviewUserName());
        initSummaryDto.setDimensionType(spotCheckContext.getSpotCheckDimensionType());
        initSummaryDto.setIsMultiPack(spotCheckContext.getIsMultiPack() ? Constants.CONSTANT_NUMBER_ONE : Constants.NUMBER_ZERO);
        initSummaryDto.setBusinessType(spotCheckContext.getSpotCheckBusinessType());
        initSummaryDto.setIsExcess(ExcessStatusEnum.EXCESS_ENUM_COMPUTE.getCode());
        initSummaryDto.setIsGatherTogether(Constants.NUMBER_ZERO);
        initSummaryDto.setSpotCheckStatus(SpotCheckStatusEnum.SPOT_CHECK_STATUS_DOING.getCode());
        initSummaryDto.setRecordType(SpotCheckRecordTypeEnum.SUMMARY_RECORD.getCode());
        initSummaryDto.setMachineCode(spotCheckReviewDetail.getMachineCode());
        initSummaryDto.setYn(Constants.CONSTANT_NUMBER_ONE);
        return initSummaryDto;
    }

    protected WeightVolumeSpotCheckDto assembleDetailReform(SpotCheckContext spotCheckContext) {
        WeightVolumeSpotCheckDto detailDto = new WeightVolumeSpotCheckDto();
        // 复核数据
        SpotCheckReviewDetail spotCheckReviewDetail = spotCheckContext.getSpotCheckReviewDetail();
        detailDto.setReviewSource(SpotCheckSourceFromEnum.analysisCodeFromName(spotCheckContext.getSpotCheckSourceFrom()));
        detailDto.setReviewOrgCode(spotCheckReviewDetail.getReviewOrgId());
        detailDto.setReviewOrgName(spotCheckReviewDetail.getReviewOrgName());
        detailDto.setReviewSiteCode(spotCheckReviewDetail.getReviewSiteCode());
        detailDto.setReviewSiteName(spotCheckReviewDetail.getReviewSiteName());
        detailDto.setReviewUserErp(spotCheckReviewDetail.getReviewUserErp());
        detailDto.setReviewUserName(spotCheckReviewDetail.getReviewUserName());
        detailDto.setReviewWeight(spotCheckReviewDetail.getReviewWeight());
        detailDto.setReviewLength(spotCheckReviewDetail.getReviewLength());
        detailDto.setReviewWidth(spotCheckReviewDetail.getReviewWidth());
        detailDto.setReviewHeight(spotCheckReviewDetail.getReviewHeight());
        detailDto.setReviewVolume(spotCheckReviewDetail.getReviewVolume());
        detailDto.setReviewLWH(spotCheckReviewDetail.getReviewLWH());
        detailDto.setMachineCode(spotCheckReviewDetail.getMachineCode());
        // 通用数据
        detailDto.setReviewDate(System.currentTimeMillis());
        detailDto.setWaybillCode(spotCheckContext.getWaybillCode());
        detailDto.setPackageCode(spotCheckContext.getPackageCode());
        detailDto.setMerchantCode(spotCheckContext.getMerchantCode());
        detailDto.setMerchantName(spotCheckContext.getMerchantName());
        detailDto.setIsTrustMerchant(spotCheckContext.getIsTrustMerchant() ? Constants.CONSTANT_NUMBER_ONE : Constants.NUMBER_ZERO);
        detailDto.setProductTypeName(spotCheckContext.getProductTypeName());
        BaseStaffSiteOrgDto reviewSite = spotCheckContext.getReviewSite();
        detailDto.setSiteTypeName(reviewSite == null
                ? null : Objects.equals(reviewSite.getSiteType(), Constants.DMS_SITE_TYPE) ? Constants.CONSTANT_NUMBER_ONE : Constants.NUMBER_ZERO);
        detailDto.setIsMultiPack(spotCheckContext.getIsMultiPack() ? Constants.CONSTANT_NUMBER_ONE : Constants.NUMBER_ZERO);
        // 是否有图片判断
        String packPicCache = spotCheckDealService.getSpotCheckPackUrlFromCache(spotCheckContext.getPackageCode(), spotCheckContext.getReviewSiteCode());
        detailDto.setIsHasPicture(StringUtils.isEmpty(packPicCache) ? Constants.NUMBER_ZERO : Constants.CONSTANT_NUMBER_ONE);
        detailDto.setPictureAddress(packPicCache);
        detailDto.setBusinessType(spotCheckContext.getSpotCheckBusinessType());
        detailDto.setDimensionType(spotCheckContext.getSpotCheckDimensionType());
        detailDto.setRecordType(SpotCheckRecordTypeEnum.DETAIL_RECORD.getCode());
        detailDto.setIsExcess(ExcessStatusEnum.EXCESS_ENUM_COMPUTE.getCode());
        detailDto.setYn(Constants.CONSTANT_NUMBER_ONE);
        return detailDto;
    }

    private void reformSendSpotCheck(SpotCheckContext spotCheckContext, InvokeResult<Boolean> result) {
        if(spotCheckDealService.checkIsHasSpotCheck(spotCheckContext.getWaybillCode())){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, SpotCheckConstants.SPOT_CHECK_HAS_SPOT_CHECK);
            return;
        }
        SpotCheckQueryCondition condition = new SpotCheckQueryCondition();
        condition.setWaybillCode(spotCheckContext.getWaybillCode());
        condition.setReviewSiteCode(spotCheckContext.getReviewSiteCode());
        List<String> spotCheckList = spotCheckQueryManager.getSpotCheckPackByCondition(condition);
        if(CollectionUtils.isNotEmpty(spotCheckList) && spotCheckList.contains(spotCheckContext.getPackageCode())){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, SpotCheckConstants.SPOT_CHECK_HAS_SPOT_CHECK);
            return;
        }
        SendDetailDto params = new SendDetailDto();
        params.setWaybillCode(spotCheckContext.getWaybillCode());
        params.setCreateSiteCode(spotCheckContext.getReviewSiteCode());
        params.setIsCancel(Constants.NUMBER_ZERO);
        params.setStatus(Constants.CONSTANT_NUMBER_ONE);
        List<String> sendList = sendDetailService.queryPackageByWaybillCode(params);
        if(CollectionUtils.isEmpty(sendList)){
            return;
        }
        // 存在已发未抽检的包裹，则禁止抽检
        if(CollectionUtils.isNotEmpty(CollectionUtils.subtract(sendList, spotCheckList))){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, SpotCheckConstants.SPOT_CHECK_PACK_SPOT_SEND_NOT_CHECK);
        }
    }

}
