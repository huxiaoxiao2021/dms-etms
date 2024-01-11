package com.jd.bluedragon.distribution.spotcheck.handler;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.SpotCheckServiceProxy;
import com.jd.bluedragon.core.base.WaybillTraceManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.send.domain.dto.SendDetailDto;
import com.jd.bluedragon.distribution.send.service.SendDetailService;
import com.jd.bluedragon.distribution.spotcheck.domain.*;
import com.jd.bluedragon.distribution.spotcheck.enums.ExcessStatusEnum;
import com.jd.bluedragon.distribution.spotcheck.enums.SpotCheckRecordTypeEnum;
import com.jd.bluedragon.distribution.spotcheck.enums.SpotCheckSourceFromEnum;
import com.jd.bluedragon.distribution.spotcheck.enums.SpotCheckStatusEnum;
import com.jd.bluedragon.distribution.spotcheck.service.SpotCheckDealService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.report.domain.spotcheck.WeightVolumeSpotCheckDto;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


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
        // 泡重比校验
        if(weightVolumeRatioCheck(spotCheckContext, result)){
            return;
        }
        //有打木架服务不支持人工抽检
        if(!isSupportSpotCheck(spotCheckContext, result)){
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
        // first pack pre deal
        if(CollectionUtils.isEmpty(spotCheckContext.getSpotCheckRecords())){
            firstPackPreDeal(spotCheckContext);
        }
        // last pack pre deal
        if(checkIsGather(spotCheckContext)){
            lastPackPreDeal(spotCheckContext);
        }
        // common deal
        packCommonDeal(spotCheckContext);
    }

    private void firstPackPreDeal(SpotCheckContext spotCheckContext) {
        // 初始化总记录
        spotCheckServiceProxy.insertOrUpdateProxyReform(initSummaryDto(spotCheckContext));
    }

    private void packCommonDeal(SpotCheckContext spotCheckContext) {
        // 包裹明细数据落库
        spotCheckServiceProxy.insertOrUpdateProxyReform(assembleDetailReform(spotCheckContext));
        // 设置包裹维度缓存
        setSpotCheckPackCache(spotCheckContext.getPackageCode(), spotCheckContext.getReviewSiteCode());
        // 抽检全程跟踪
        // spotCheckDealService.sendWaybillTrace(spotCheckContext);
    }

    private void lastPackPreDeal(SpotCheckContext spotCheckContext) {
        spotCheckContext.setIsGatherTogether(true);
        // 1、汇总复核数据
        summaryReviewWeightVolume(spotCheckContext);
        // 获取核对数据
        spotCheckDealService.assembleContrastData(spotCheckContext);
        // 2、设置已抽检缓存
        setSpotCheckCache(spotCheckContext.getWaybillCode(), spotCheckContext.getExcessStatus());
        // 3、更新总记录
        WeightVolumeSpotCheckDto summaryDto = assembleSummaryReform(spotCheckContext);
        spotCheckServiceProxy.insertOrUpdateProxyReform(summaryDto);
        // 4、下发超标数据
        if(Objects.equals(spotCheckContext.getExcessStatus(), ExcessStatusEnum.EXCESS_ENUM_YES.getCode())){
            // 异步处理dws一单多件图片下发AI逻辑
            dwsIssueDealProducer.sendOnFailPersistent(spotCheckContext.getWaybillCode(), JsonHelper.toJson(summaryDto));
        }
    }

    private boolean checkIsGather(SpotCheckContext spotCheckContext) {
        List<String> packRecord = spotCheckContext.getSpotCheckRecords()
                .stream()
                .filter(item -> Objects.equals(item.getRecordType(), SpotCheckRecordTypeEnum.DETAIL_RECORD.getCode())
                        && Objects.equals(item.getReviewSiteCode(), spotCheckContext.getReviewSiteCode()))
                .map(WeightVolumeSpotCheckDto::getPackageCode)
                .collect(Collectors.toList());
        return CollectionUtils.isNotEmpty(packRecord) && packRecord.size() + 1 == spotCheckContext.getPackNum();
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
        if(spotCheckDealService.checkIsHasSpotCheck(spotCheckContext)){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, SpotCheckConstants.SPOT_CHECK_HAS_SPOT_CHECK);
            return;
        }
        // 当前场地已抽检包裹
        List<String> spotCheckPackList = spotCheckContext.getSpotCheckRecords()
                .stream()
                .filter(item -> Objects.equals(item.getReviewSiteCode(), spotCheckContext.getReviewSiteCode())
                        && Objects.equals(item.getRecordType(), SpotCheckRecordTypeEnum.DETAIL_RECORD.getCode()))
                .map(WeightVolumeSpotCheckDto::getPackageCode)
                .collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(spotCheckPackList) && spotCheckPackList.contains(spotCheckContext.getPackageCode())){
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
        if(CollectionUtils.isNotEmpty(CollectionUtils.subtract(sendList, spotCheckPackList))){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, SpotCheckConstants.SPOT_CHECK_PACK_SPOT_SEND_NOT_CHECK);
        }
    }

}
