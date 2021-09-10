package com.jd.bluedragon.distribution.spotcheck.handler;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.spotcheck.domain.*;
import com.jd.bluedragon.distribution.spotcheck.enums.ExcessStatusEnum;
import com.jd.bluedragon.distribution.spotcheck.enums.SpotCheckBusinessTypeEnum;
import com.jd.bluedragon.distribution.spotcheck.service.SpotCheckDealService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.MathUtils;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.etms.waybill.domain.Waybill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    protected void spotCheck(SpotCheckContext spotCheckContext, InvokeResult<Boolean> result) {
        Waybill waybill = spotCheckContext.getWaybill();
        String waybillCode = spotCheckContext.getWaybillCode();
        String packageCode = spotCheckContext.getPackageCode();
        Integer siteCode = spotCheckContext.getReviewSiteCode();
        if(!WaybillUtil.isPackageCode(packageCode)){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, SpotCheckConstants.SPOT_CHECK_ONLY_SUPPORT_MORE_PACK);
            return;
        }
        // 纯配外单校验
        if(!BusinessUtil.isCInternet(waybill.getWaybillSign())){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, SpotCheckConstants.SPOT_CHECK_ONLY_SUPPORT_C);
            return;
        }
        if(!BusinessUtil.isPurematch(waybill.getWaybillSign())){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, SpotCheckConstants.SPOT_CHECK_ONLY_SUPPORT_PURE_MATCH);
            return;
        }
        // 是否已发货校验
        if(spotCheckDealService.checkIsHasSend(packageCode, siteCode)){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, SpotCheckConstants.SPOT_CHECK_MUST_BEFORE_SEND);
            return;
        }
        // 是否已抽检
        if(spotCheckDealService.checkIsHasSpotCheck(waybillCode)){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, SpotCheckConstants.SPOT_CHECK_HAS_SPOT_CHECK);
        }
    }

    /**
     * 超标校验
     *  1、获取复核数据、核对数据
     *      1）、一单多件
     *          a、未集齐返回'待集齐计算'
     *          b、集齐则汇总复核数据
     *          c、核对数据运单获取
     *  2、超标判断
     * @param spotCheckContext
     * @return
     */
    @Override
    protected InvokeResult<CheckExcessResult> checkIsExcessB(SpotCheckContext spotCheckContext) {
        InvokeResult<CheckExcessResult> result = new InvokeResult<CheckExcessResult>();
        if(!spotCheckDealService.gatherTogether(spotCheckContext)){
            CheckExcessResult excessData = new CheckExcessResult();
            excessData.setExcessCode(ExcessStatusEnum.EXCESS_ENUM_COMPUTE.getCode());
            result.setData(excessData);
            return result;
        }
        summaryReviewWeightVolume(spotCheckContext);
        obtainContrast(spotCheckContext);
        return abstractExcessStandardHandler.checkIsExcess(spotCheckContext);
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

    @Override
    protected void obtainContrast(SpotCheckContext spotCheckContext) {
        // B网从运单获取
        if(Objects.equals(spotCheckContext.getSpotCheckBusinessType(), SpotCheckBusinessTypeEnum.SPOT_CHECK_TYPE_B.getCode())){
            spotCheckDealService.assembleContrastDataFromWaybillFlow(spotCheckContext);
            return;
        }
        // C网:
        // 一单一件从计费获取后无从运单获取
        // 一单从计费获取
        if(spotCheckContext.getIsMultiPack()){
            spotCheckDealService.assembleContrastDataFromWaybillFlow(spotCheckContext);
            return;
        }
        SpotCheckContrastDetail spotCheckContrastDetail = spotCheckContext.getSpotCheckContrastDetail();
        if(spotCheckContrastDetail.getContrastWeight() == null
                || Objects.equals(spotCheckContrastDetail.getContrastWeight(), Constants.DOUBLE_ZERO)){
            spotCheckDealService.assembleContrastDataFromWaybillFlow(spotCheckContext);
        }
    }

    /**
     * 超标后续逻辑
     *  1、抽检数据落es
     *  2、抽检全程跟踪
     *  3、上传称重流水
     *  4、记录抽检日志
     * @param spotCheckContext
     */
    @Override
    protected void dealAfterCheckSuc(SpotCheckContext spotCheckContext) {
        if(spotCheckContext.getIsMultiPack()){
            multiPackDeal(spotCheckContext);
        }else {
            onePackDeal(spotCheckContext);
        }
    }
}
