package com.jd.bluedragon.distribution.spotcheck.handler;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.spotcheck.domain.*;
import com.jd.bluedragon.distribution.spotcheck.enums.ExcessStatusEnum;
import com.jd.bluedragon.distribution.spotcheck.enums.SpotCheckSourceFromEnum;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.MathUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 超标标准选择
 *
 * @author hujiping
 * @date 2021/8/23 2:59 下午
 */
@Service
public class AbstractExcessStandardHandler {

    @Value("${spotCheck.firstSumSide:70}")
    public double sumSideLimit;

    @Value("${spotCheck.firstVolume:12700}")
    public double volumeLimit;

    @Qualifier("excessStandardWeightVolumeHandler")
    @Autowired
    private IExcessStandardHandler excessStandardWeightVolumeHandler;

    @Qualifier("excessStandardWeightHandler")
    @Autowired
    private IExcessStandardHandler excessStandardWeightHandler;

    @Qualifier("excessStandardSideHandler")
    @Autowired
    private IExcessStandardHandler excessStandardSideHandler;

    @Qualifier("excessStandardVolumeHandler")
    @Autowired
    private IExcessStandardHandler excessStandardVolumeHandler;

    @Autowired
    private UccPropertyConfiguration uccPropertyConfiguration;

    /**
     * 超标标准选择
     *  0、特殊场景处理
     *  1、SpotCheckSourceFromEnum.B_SPOT_CHECK_SOURCE 走重量体积标准
     *  2、SpotCheckSourceFromEnum.C_SPOT_CHECK_SOURCE 单独判断
     *      1)、人工抽检 同 dws一单多件
     *      2)、客户端抽检
     *          a、复核重量 > 复合体积重量, 则执行重量标准
     *          b、复核重量 < 复合体积重量 && 周长 < 70, 则执行重量标准
     *          c、复核重量 < 复合体积重量 && 周长 > 70, 则执行边长标准
     *      3)、dws抽检
     *          （1）、一单一件 同 客户端抽检
     *          （2）、一单多件
     *                  a、复核重量 > 复合体积重量, 则执行重量标准
     *                  b、复核重量 < 复合体积重量 && 复核体积 < 12700, 则执行重量标准
     *                  c、复核重量 < 复合体积重量 && 复核体积 > 12700, 则执行体积标准
     * @param spotCheckContext
     * @return
     */
    public InvokeResult<CheckExcessResult> checkIsExcess(SpotCheckContext spotCheckContext) {
        InvokeResult<CheckExcessResult> result = new InvokeResult<CheckExcessResult>();
        CheckExcessResult checkExcessResult = specialTreatment(spotCheckContext);
        if(checkExcessResult != null){
            result.setData(checkExcessResult);
            return result;
        }
        CheckExcessRequest checkExcessRequest = buildCheckExcessRequest(spotCheckContext);
        if(SpotCheckSourceFromEnum.B_SPOT_CHECK_SOURCE.contains(checkExcessRequest.getSpotCheckSourceFrom())){
            return excessStandardWeightVolumeHandler.checkIsExcess(checkExcessRequest);
        }
        // 客户端抽检
        if(Objects.equals(checkExcessRequest.getSpotCheckSourceFrom(), SpotCheckSourceFromEnum.SPOT_CHECK_CLIENT_PLATE.getName())){
            return executeStandardA(checkExcessRequest);
        }
        // dws抽检
        if(Objects.equals(checkExcessRequest.getSpotCheckSourceFrom(), SpotCheckSourceFromEnum.SPOT_CHECK_DWS.getName())){
            if(Objects.equals(checkExcessRequest.getIsMorePack(), false)){
                return executeStandardA(checkExcessRequest);
            }
            return executeStandardB(checkExcessRequest);
        }
        if(Objects.equals(checkExcessRequest.getSpotCheckSourceFrom(), SpotCheckSourceFromEnum.SPOT_CHECK_ARTIFICIAL.getName())){
            return executeStandardB(checkExcessRequest);
        }
        result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, "非法来源!");
        return result;
    }

    private InvokeResult<CheckExcessResult> executeStandardA(CheckExcessRequest checkExcessRequest) {
        if(checkExcessRequest.getReviewWeight() > checkExcessRequest.getReviewVolumeWeight()){
            return excessStandardWeightHandler.checkIsExcess(checkExcessRequest);
        }
        double sumSide = checkExcessRequest.getReviewLength() + checkExcessRequest.getReviewWidth() + checkExcessRequest.getReviewHeight();
        if(sumSide < sumSideLimit){
            return excessStandardWeightHandler.checkIsExcess(checkExcessRequest);
        }else {
            return excessStandardSideHandler.checkIsExcess(checkExcessRequest);
        }
    }

    private InvokeResult<CheckExcessResult> executeStandardB(CheckExcessRequest checkExcessRequest) {
        if(checkExcessRequest.getReviewWeight() > checkExcessRequest.getReviewVolumeWeight()){
            return excessStandardWeightHandler.checkIsExcess(checkExcessRequest);
        }
        if(checkExcessRequest.getReviewVolume() < volumeLimit){
            return excessStandardWeightHandler.checkIsExcess(checkExcessRequest);
        }
        return excessStandardVolumeHandler.checkIsExcess(checkExcessRequest);
    }

    /**
     * 构建校验超标入参
     *
     * @param spotCheckContext
     * @return
     */
    private CheckExcessRequest buildCheckExcessRequest(SpotCheckContext spotCheckContext) {
        CheckExcessRequest checkExcessRequest = new CheckExcessRequest();
        checkExcessRequest.setSpotCheckSourceFrom(spotCheckContext.getSpotCheckSourceFrom());
        checkExcessRequest.setIsMorePack(spotCheckContext.getIsMultiPack());
        SpotCheckReviewDetail spotCheckReviewDetail = spotCheckContext.getSpotCheckReviewDetail();
        double reviewWeight = spotCheckReviewDetail.getReviewTotalWeight();
        double reviewVolume = spotCheckReviewDetail.getReviewTotalVolume();
        double reviewVolumeWeight = MathUtils.keepScale(reviewVolume / spotCheckContext.getVolumeRate(), 3);
        double reviewLarge = Math.max(reviewWeight, reviewVolumeWeight);
        checkExcessRequest.setReviewWeight(reviewWeight);
        checkExcessRequest.setReviewVolume(reviewVolume);
        checkExcessRequest.setReviewLength(spotCheckReviewDetail.getReviewLength() == null ? Constants.DOUBLE_ZERO : spotCheckReviewDetail.getReviewLength());
        checkExcessRequest.setReviewWidth(spotCheckReviewDetail.getReviewWidth() == null ? Constants.DOUBLE_ZERO : spotCheckReviewDetail.getReviewWidth());
        checkExcessRequest.setReviewHeight(spotCheckReviewDetail.getReviewHeight() == null ? Constants.DOUBLE_ZERO : spotCheckReviewDetail.getReviewHeight());
        checkExcessRequest.setReviewVolumeWeight(reviewVolumeWeight);
        checkExcessRequest.setReviewLarge(reviewLarge);
        SpotCheckContrastDetail spotCheckContrastDetail = spotCheckContext.getSpotCheckContrastDetail();
        Double contrastWeight = spotCheckContrastDetail.getContrastWeight() == null ? Constants.DOUBLE_ZERO : spotCheckContrastDetail.getContrastWeight();
        Double contrastVolume = spotCheckContrastDetail.getContrastVolume() == null ? Constants.DOUBLE_ZERO : spotCheckContrastDetail.getContrastVolume();
        Double contrastLarge = spotCheckContrastDetail.getContrastLarge() == null ? Constants.DOUBLE_ZERO : spotCheckContrastDetail.getContrastLarge();
        checkExcessRequest.setContrastWeight(contrastWeight);
        checkExcessRequest.setContrastVolume(contrastVolume);
        checkExcessRequest.setContrastLarge(contrastLarge);
        checkExcessRequest.setLargeDiff(Math.abs(reviewLarge - contrastLarge));
        return checkExcessRequest;
    }

    /**
     * 特殊场景处理
     *  1、C网入口单独处理：
     *      1）、生鲜产品 && 复核较大值 < 核对较大值 则不超标
     *      2）、复核较大值 <= 1.5 && 核对较大值 <= 1.5 则不超标
     *  2、B网入口待补充：
     *
     * @param spotCheckContext
     */
    private CheckExcessResult specialTreatment(SpotCheckContext spotCheckContext) {
        CheckExcessResult checkExcessResult = new CheckExcessResult();
        // C网特殊场景处理
        if(SpotCheckSourceFromEnum.C_SPOT_CHECK_SOURCE.contains(spotCheckContext.getSpotCheckSourceFrom())){
            SpotCheckReviewDetail spotCheckReviewDetail = spotCheckContext.getSpotCheckReviewDetail();
            SpotCheckContrastDetail spotCheckContrastDetail = spotCheckContext.getSpotCheckContrastDetail();
            // 复核总重量、复核总体积
            double reviewWeight = spotCheckReviewDetail.getReviewTotalWeight();
            double reviewVolume = spotCheckReviewDetail.getReviewTotalVolume();
            double reviewVolumeWeight = MathUtils.keepScale(reviewVolume / spotCheckContext.getVolumeRate(), 3);
            // 复核较大值
            double reviewLarge = Math.max(reviewWeight, reviewVolumeWeight);
            // 核对较大值
            double checkMore = spotCheckContrastDetail.getContrastLarge() == null ? Constants.DOUBLE_ZERO : spotCheckContrastDetail.getContrastLarge();
            if(BusinessUtil.isFresh(spotCheckContext.getWaybill().getWaybillSign()) && reviewLarge < checkMore){
                checkExcessResult.setExcessCode(com.jd.ql.dms.report.domain.Enum.IsExcessEnum.EXCESS_ENUM_NO.getCode());
                checkExcessResult.setExcessReason(Constants.EMPTY_FILL);
                return checkExcessResult;
            }
            // 复核较大值 <= 1.5 && 核对较大值 <= 1.5
            double spotCheckNoExcessLimit = uccPropertyConfiguration.getSpotCheckNoExcessLimit();
            if(reviewLarge <= spotCheckNoExcessLimit && checkMore <= spotCheckNoExcessLimit){
                checkExcessResult.setExcessCode(ExcessStatusEnum.EXCESS_ENUM_NO.getCode());
                checkExcessResult.setExcessReason(Constants.EMPTY_FILL);
            }
        }
        return null;
    }
}
