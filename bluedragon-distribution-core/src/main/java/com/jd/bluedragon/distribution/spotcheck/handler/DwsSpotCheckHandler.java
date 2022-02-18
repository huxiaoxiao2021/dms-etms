package com.jd.bluedragon.distribution.spotcheck.handler;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.ReportExternalManager;
import com.jd.bluedragon.core.base.SpotCheckQueryManager;
import com.jd.bluedragon.core.base.SpotCheckServiceProxy;
import com.jd.bluedragon.core.base.WaybillTraceManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.send.domain.dto.SendDetailDto;
import com.jd.bluedragon.distribution.send.service.SendDetailService;
import com.jd.bluedragon.distribution.spotcheck.domain.*;
import com.jd.bluedragon.distribution.spotcheck.enums.*;
import com.jd.bluedragon.distribution.spotcheck.service.SpotCheckDealService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.report.domain.WeightVolumeCollectDto;
import com.jd.ql.dms.report.domain.spotcheck.SpotCheckQueryCondition;
import com.jd.ql.dms.report.domain.spotcheck.WeightVolumeSpotCheckDto;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
    private AbstractExcessStandardHandler abstractExcessStandardHandler;

    @Autowired
    private WaybillTraceManager waybillTraceManager;

    @Autowired
    private SendDetailService sendDetailService;

    @Autowired
    private SpotCheckQueryManager spotCheckQueryManager;

    @Autowired
    private ReportExternalManager reportExternalManager;

    @Autowired
    private SpotCheckServiceProxy spotCheckServiceProxy;

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
    protected void spotCheck(SpotCheckContext spotCheckContext, InvokeResult<Boolean> result) {
        if(!WaybillUtil.isPackageCode(spotCheckContext.getPackageCode())){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, SpotCheckConstants.SPOT_CHECK_ONLY_SUPPORT_MORE_PACK);
            return;
        }
        super.spotCheck(spotCheckContext, result);
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
            // 初始化老的抽检数据
            initOldDto(spotCheckContext);
        }
        // 集齐后
        if(spotCheckDealService.gatherTogether(spotCheckContext)){
            spotCheckContext.setIsGatherTogether(true);
            // 0、设置超标缓存
            setSpotCheckCache(spotCheckContext.getWaybillCode(), spotCheckContext.getExcessStatus());
            // 1、汇总复核数据
            summaryReviewWeightVolume(spotCheckContext);
            // 获取核对数据
            spotCheckDealService.assembleContrastData(spotCheckContext);
            // 2、更新总记录
            WeightVolumeSpotCheckDto summaryDto = assembleSummaryReform(spotCheckContext);
            spotCheckServiceProxy.insertOrUpdateProxyReform(summaryDto);
            // 3、下发超标数据
            spotCheckDealService.spotCheckIssue(summaryDto);
        }
        // 双写老的抽检数据
        assembledSummaryAndDetailOldDto(spotCheckContext);
        // 包裹明细数据落库
        spotCheckServiceProxy.insertOrUpdateProxyReform(assembleDetailReform(spotCheckContext));
        // 设置包裹维度缓存
        setSpotCheckPackCache(spotCheckContext.getPackageCode(), spotCheckContext.getReviewSiteCode());
        // 抽检全程跟踪
        spotCheckDealService.sendWaybillTrace(spotCheckContext);
        // 记录抽检操作日志
        spotCheckDealService.recordSpotCheckLog(spotCheckContext);
    }

    private void assembledSummaryAndDetailOldDto(SpotCheckContext spotCheckContext) {
        // 包裹维度明细数据
        assembledDetailOldDto(spotCheckContext);
        if(spotCheckContext.getIsGatherTogether()){
            // 汇总的总记录数据
            assembledSummaryOldDto(spotCheckContext);
        }
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
        initSummaryDto.setIsIssueDownstream(Constants.NUMBER_ZERO);
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

    protected void initOldDto(SpotCheckContext spotCheckContext) {
        WeightVolumeCollectDto initOldDto = new WeightVolumeCollectDto();
        initOldDto.setRecordType(SpotCheckRecordTypeEnum.SUMMARY_RECORD.getCode());
        initOldDto.setIsWaybillSpotCheck(spotCheckContext.getSpotCheckDimensionType());
        initOldDto.setFromSource(spotCheckContext.getSpotCheckSourceFrom());
        initOldDto.setSpotCheckType(spotCheckContext.getSpotCheckBusinessType());
        initOldDto.setMultiplePackage(spotCheckContext.getIsMultiPack() ? Constants.CONSTANT_NUMBER_ONE : Constants.NUMBER_ZERO);
        initOldDto.setReviewDate(spotCheckContext.getOperateTime());
        initOldDto.setWaybillCode(spotCheckContext.getWaybillCode());
        initOldDto.setPackageCode(spotCheckContext.getWaybillCode());
        initOldDto.setIsTrustBusi(spotCheckContext.getIsTrustMerchant() ? Constants.CONSTANT_NUMBER_ONE : Constants.NUMBER_ZERO);
        initOldDto.setMerchantCode(spotCheckContext.getMerchantCode());
        initOldDto.setBusiCode(spotCheckContext.getMerchantId());
        initOldDto.setBusiName(spotCheckContext.getMerchantName());
        initOldDto.setProductTypeCode(spotCheckContext.getProductTypeCode());
        initOldDto.setProductTypeName(spotCheckContext.getProductTypeName());
        BaseStaffSiteOrgDto reviewSite = spotCheckContext.getReviewSite();
        initOldDto.setReviewSubType(reviewSite == null
                ? null : Objects.equals(reviewSite.getSiteType(), Constants.DMS_SITE_TYPE) ? Constants.CONSTANT_NUMBER_ONE : Constants.NUMBER_ZERO);
        initOldDto.setVolumeRate(spotCheckContext.getVolumeRate());
        SpotCheckReviewDetail spotCheckReviewDetail = spotCheckContext.getSpotCheckReviewDetail();
        initOldDto.setReviewOrgCode(spotCheckReviewDetail.getReviewOrgId());
        initOldDto.setReviewOrgName(spotCheckReviewDetail.getReviewOrgName());
        initOldDto.setReviewSiteCode(spotCheckReviewDetail.getReviewSiteCode());
        initOldDto.setReviewSiteName(spotCheckReviewDetail.getReviewSiteName());
        initOldDto.setReviewErp(spotCheckReviewDetail.getReviewUserErp());
        initOldDto.setReviewWeight(spotCheckReviewDetail.getReviewWeight());
        initOldDto.setReviewVolume(spotCheckReviewDetail.getReviewVolume());
        initOldDto.setReviewVolumeWeight(spotCheckReviewDetail.getReviewVolumeWeight());
        initOldDto.setMoreBigWeight(spotCheckReviewDetail.getReviewLarge());
        initOldDto.setVolumeRate(spotCheckContext.getVolumeRate());
        initOldDto.setFullCollect(Constants.NUMBER_ZERO);
        initOldDto.setIsExcess(ExcessStatusEnum.EXCESS_ENUM_COMPUTE.getCode());
        spotCheckServiceProxy.insertOrUpdateProxyPrevious(initOldDto);
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

    /**
     * 超标校验
     *  1、获取复核数据、核对数据
     *       a、未集齐返回'待集齐计算'
     *       b、集齐则汇总复核数据
     *       c、核对数据运单获取
     *  2、超标判断
     * @param spotCheckContext
     * @return
     */
    @Override
    protected InvokeResult<CheckExcessResult> checkIsExcessB(SpotCheckContext spotCheckContext) {
        return checkIsExcessC(spotCheckContext);
    }

    /**
     * 超标校验
     *  1、获取复核数据、核对数据
     *      1）、一单多件
     *          a、未集齐返回'待集齐计算'
     *          b、集齐则汇总复核数据
     *          c、核对数据从计费获取
     *      2）、一单一件
     *          a、核对数据从计费获取
     *          b、核对数据先计费获取，计费重量为0或空则从运单称重流水获取
     *  2、超标判断
     * @param spotCheckContext
     * @return
     */
    @Override
    protected InvokeResult<CheckExcessResult> checkIsExcessC(SpotCheckContext spotCheckContext) {
        InvokeResult<CheckExcessResult> result = new InvokeResult<CheckExcessResult>();
        if(spotCheckContext.getIsMultiPack()){
            // 一单多件
            if(!spotCheckDealService.gatherTogether(spotCheckContext)){
                CheckExcessResult excessData = new CheckExcessResult();
                excessData.setExcessCode(ExcessStatusEnum.EXCESS_ENUM_COMPUTE.getCode());
                result.setData(excessData);
                return result;
            }
            summaryReviewWeightVolume(spotCheckContext);
            obtainContrast(spotCheckContext);
        }else {
            // 一单一件
            obtainContrast(spotCheckContext);
        }
        return abstractExcessStandardHandler.checkIsExcess(spotCheckContext);
    }

    /**
     * 超标后续逻辑
     *  1、抽检数据落es
     *  2、抽检全程跟踪
     *  4、记录抽检日志
     * @param spotCheckContext
     */
    @Override
    protected void dealAfterCheckSuc(SpotCheckContext spotCheckContext) {
        if(spotCheckContext.getIsMultiPack()){
            multiDataDeal(spotCheckContext);
        }else {
            onceDataDeal(spotCheckContext);
        }
    }
}
