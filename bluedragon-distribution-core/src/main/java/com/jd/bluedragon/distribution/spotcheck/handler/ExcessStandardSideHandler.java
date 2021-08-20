package com.jd.bluedragon.distribution.spotcheck.handler;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.spotcheck.domain.CheckExcessRequest;
import com.jd.bluedragon.distribution.spotcheck.domain.CheckExcessResult;
import com.jd.bluedragon.distribution.spotcheck.enums.ExcessStatusEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 超标标准处理器
 *  以边长为标准
 *
 * @author hujiping
 * @date 2021/8/23 3:01 下午
 */
@Service("excessStandardSideHandler")
public class ExcessStandardSideHandler implements IExcessStandardHandler {

    @Value("${spotCheck.firstSumSide:70}")
    public double firstSumSide;
    @Value("${spotCheck.secondSumSide:100}")
    public double secondSumSide;
    @Value("${spotCheck.thirdSumSide:120}")
    public double thirdSumSide;
    @Value("${spotCheck.fourSumSide:200}")
    public double fourSumSide;

    @Value("${spotCheck.firstSumSideStage:0.8}")
    public double firstSumSideStage;
    @Value("${spotCheck.secondSumSideStage:1}")
    public double secondSumSideStage;
    @Value("${spotCheck.thirdSumSideStage:1.5}")
    public double thirdSumSideStage;
    @Value("${spotCheck.fourSumSideStage:2}")
    public double fourSumSideStage;

    /**
     * 边长超标标准
     *  1. 70cm=<三边之和<100cm，误差标准正负0.8 kg（含）
     *  2. 100cm=<三边之和<120cm，误差标准正负1 kg（含）
     *  3. 120cm=<三边之和<200cm，误差标准正负1.5 kg（含）
     *  4. 三边之和>200cm，误差标准正负2kg（含）
     * @param checkExcessRequest
     * @return
     */
    @Override
    public InvokeResult<CheckExcessResult> checkIsExcess(CheckExcessRequest checkExcessRequest) {
        InvokeResult<CheckExcessResult> result = new InvokeResult<CheckExcessResult>();
        CheckExcessResult checkExcessResult = new CheckExcessResult();
        checkExcessResult.setExcessCode(ExcessStatusEnum.EXCESS_ENUM_NO.getCode());
        result.setData(checkExcessResult);
        // 较大值差异
        double largeDiff = checkExcessRequest.getLargeDiff();
        double sumSide = checkExcessRequest.getReviewLength() + checkExcessRequest.getReviewWidth() + checkExcessRequest.getReviewHeight();
        // 超标原因
        String excessReasonTemplate = "三边之和在%scm和%scm之间并且误差%s超过误差标准值%skg";
        if(sumSide >= firstSumSide && sumSide < secondSumSide){
            if(largeDiff > firstSumSideStage){
                checkExcessResult.setExcessCode(ExcessStatusEnum.EXCESS_ENUM_YES.getCode());
                checkExcessResult.setExcessReason(String.format(excessReasonTemplate, sumSide, firstSumSide, secondSumSide, largeDiff, firstSumSideStage));
                checkExcessResult.setExcessStandard(String.valueOf(firstSumSideStage));
                return result;
            }
            return result;
        }
        if(sumSide >= secondSumSide && sumSide < thirdSumSide){
            if(largeDiff > secondSumSideStage){
                checkExcessResult.setExcessCode(ExcessStatusEnum.EXCESS_ENUM_YES.getCode());
                checkExcessResult.setExcessReason(String.format(excessReasonTemplate, sumSide, secondSumSide, thirdSumSide, largeDiff, secondSumSideStage));
                checkExcessResult.setExcessStandard(String.valueOf(secondSumSideStage));
                return result;
            }
            return result;
        }
        if(sumSide >= thirdSumSide && sumSide < fourSumSide){
            if(largeDiff > thirdSumSideStage){
                checkExcessResult.setExcessCode(ExcessStatusEnum.EXCESS_ENUM_YES.getCode());
                checkExcessResult.setExcessReason(String.format(excessReasonTemplate, sumSide, thirdSumSide, fourSumSide, largeDiff, thirdSumSideStage));
                checkExcessResult.setExcessStandard(String.valueOf(thirdSumSideStage));
                return result;
            }
            return result;
        }
        if(sumSide >= fourSumSide){
            if(largeDiff > fourSumSideStage){
                checkExcessResult.setExcessCode(ExcessStatusEnum.EXCESS_ENUM_YES.getCode());
                checkExcessResult.setExcessReason(String.format(excessReasonTemplate, sumSide, fourSumSide, "∞", largeDiff, fourSumSideStage));
                checkExcessResult.setExcessStandard(String.valueOf(fourSumSideStage));
                return result;
            }
            return result;
        }
        return result;
    }
}
