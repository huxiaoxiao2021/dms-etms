package com.jd.bluedragon.distribution.spotcheck.handler;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.spotcheck.domain.CheckExcessRequest;
import com.jd.bluedragon.distribution.spotcheck.domain.CheckExcessResult;
import com.jd.bluedragon.distribution.spotcheck.enums.ExcessStatusEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 超标标准处理器
 *  以重量为标准
 *
 * @author hujiping
 * @date 2021/8/23 3:01 下午
 */
@Service("excessStandardWeightHandler")
public class ExcessStandardWeightHandler implements IExcessStandardHandler {

    @Value("${spotCheck.firstWeight:1.5}")
    public double firstWeight;
    @Value("${spotCheck.secondWeight:20.0}")
    public double secondWeight;
    @Value("${spotCheck.thirdWeight:50.0}")
    public double thirdWeight;
    @Value("${spotCheck.firstWeightStage:0.5}")
    public double firstWeightStage;
    @Value("${spotCheck.secondWeightStage:1.0}")
    public double secondWeightStage;
    @Value("${spotCheck.thirdWeightStage:0.02}")
    public double thirdWeightStage;

    /**
     * 重量超标标准
     *    1. 分拣【较大值】<= 1.5 && 核对较大值 <= 1.5，不论误差多少均判断为正常
     *    1.分拣【较大值】<=1 && 核对较大值 > 1，误差标准正负0.5 kg（含）
     *    2. 分拣【较大值】1公斤至20公斤（含),误差标准正负0.5 kg（含）
     *    3. 分拣【较大值】20公斤至50公斤（含),误差标准正负1 kg（含）
     *    4. 分拣【较大值】50公斤以上，误差标准为重量的正负2%（含）
     * @param checkExcessRequest
     * @return
     */
    @Override
    public InvokeResult<CheckExcessResult> checkIsExcess(CheckExcessRequest checkExcessRequest) {
        InvokeResult<CheckExcessResult> result = new InvokeResult<CheckExcessResult>();
        CheckExcessResult checkExcessResult = new CheckExcessResult();
        checkExcessResult.setExcessCode(ExcessStatusEnum.EXCESS_ENUM_NO.getCode());
        result.setData(checkExcessResult);
        Double reviewWeight = checkExcessRequest.getReviewWeight();
        // 复核较大值
        Double reviewLarge = checkExcessRequest.getReviewLarge();
        // 核对较大值
        Double contrastLarge = checkExcessRequest.getContrastLarge();
        // 较大值差异
        double largeDiff = checkExcessRequest.getLargeDiff();
        // 超标原因
        String excessReasonTemplate = "分拣较大值%s在%s公斤至%s公斤之间并且误差%s超过标准值%s";
        if(reviewLarge <= firstWeight && contrastLarge > firstWeight){
            if(largeDiff > firstWeightStage){
                checkExcessResult.setExcessCode(ExcessStatusEnum.EXCESS_ENUM_YES.getCode());
                checkExcessResult.setExcessReason(String.format(excessReasonTemplate, reviewLarge, Constants.NUMBER_ZERO, firstWeight, largeDiff, firstWeightStage));
                checkExcessResult.setExcessStandard(String.valueOf(firstWeightStage));
                return result;
            }
            return result;
        }
        if(reviewLarge > firstWeight && reviewLarge <= secondWeight){
            if(largeDiff > firstWeightStage){
                checkExcessResult.setExcessCode(ExcessStatusEnum.EXCESS_ENUM_YES.getCode());
                checkExcessResult.setExcessReason(String.format(excessReasonTemplate, reviewLarge, firstWeight, secondWeight, largeDiff, firstWeightStage));
                checkExcessResult.setExcessStandard(String.valueOf(firstWeightStage));
                return result;
            }
            return result;
        }
        if(reviewLarge > secondWeight && reviewLarge <= thirdWeight){
            if(largeDiff > secondWeightStage){
                checkExcessResult.setExcessCode(ExcessStatusEnum.EXCESS_ENUM_YES.getCode());
                checkExcessResult.setExcessReason(String.format(excessReasonTemplate, reviewLarge, secondWeight, thirdWeight, largeDiff, secondWeightStage));
                checkExcessResult.setExcessStandard(String.valueOf(secondWeightStage));
                return result;
            }
            return result;
        }
        if(reviewLarge > thirdWeight){
            if(largeDiff > reviewWeight * thirdWeightStage){
                checkExcessResult.setExcessCode(ExcessStatusEnum.EXCESS_ENUM_YES.getCode());
                checkExcessResult.setExcessReason(String.format(excessReasonTemplate, reviewLarge, thirdWeight, "∞", largeDiff, thirdWeightStage));
                checkExcessResult.setExcessStandard(String.valueOf(thirdWeightStage));
                return result;
            }
            return result;
        }
        return result;
    }
}
