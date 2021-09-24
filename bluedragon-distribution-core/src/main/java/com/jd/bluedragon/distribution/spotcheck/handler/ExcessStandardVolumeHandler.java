package com.jd.bluedragon.distribution.spotcheck.handler;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.spotcheck.domain.CheckExcessRequest;
import com.jd.bluedragon.distribution.spotcheck.domain.CheckExcessResult;
import com.jd.bluedragon.distribution.spotcheck.enums.ExcessStatusEnum;
import com.jd.bluedragon.dms.utils.MathUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 超标标准处理器
 *  以体积为标准
 *
 * @author hujiping
 * @date 2021/8/23 3:01 下午
 */
@Service("excessStandardVolumeHandler")
public class ExcessStandardVolumeHandler implements IExcessStandardHandler {

    @Value("${spotCheck.firstVolume:12700}")
    public double firstVolume;
    @Value("${spotCheck.secondVolume:37037}")
    public double secondVolume;
    @Value("${spotCheck.thirdVolume:64000}")
    public double thirdVolume;
    @Value("${spotCheck.fourVolume:296252}")
    public double fourVolume;

    @Value("${spotCheck.firstVolumeStage:800000}")
    public double firstVolumeStage;
    @Value("${spotCheck.secondVolumeStage:1000000}")
    public double secondVolumeStage;
    @Value("${spotCheck.thirdVolumeStage:1500000}")
    public double thirdVolumeStage;
    @Value("${spotCheck.fourVolumeStage:2000000}")
    public double fourVolumeStage;

    /**
     * 体积超标标准
     *  1. 12700 =< 体积 < 37037，误差标准正负0.8 kg（含）
     *  2. 37037 =< 体积 < 64000，误差标准正负1 kg（含）
     *  3. 64000 =< 体积 < 296252，误差标准正负1.5 kg（含）
     *  4. 296252 =< 体积，误差标准正负2kg（含）
     * @param checkExcessRequest
     * @return
     */
    @Override
    public InvokeResult<CheckExcessResult> checkIsExcess(CheckExcessRequest checkExcessRequest) {
        InvokeResult<CheckExcessResult> result = new InvokeResult<CheckExcessResult>();
        CheckExcessResult checkExcessResult = new CheckExcessResult();
        checkExcessResult.setExcessCode(ExcessStatusEnum.EXCESS_ENUM_NO.getCode());
        result.setData(checkExcessResult);
        double reviewVolume = checkExcessRequest.getReviewVolume();
        double contrastVolume = checkExcessRequest.getContrastVolume();
        // 体积误差
        double diffVolume = MathUtils.keepScale(Math.abs(reviewVolume - contrastVolume), 3);
        // 超标原因
        String excessReasonTemplate = "体积%s在%s和%s之间并且误差%s超过误差标准值%s";
        if(reviewVolume >= firstVolume && reviewVolume < secondVolume){
            if(diffVolume > firstVolumeStage){
                checkExcessResult.setExcessCode(ExcessStatusEnum.EXCESS_ENUM_YES.getCode());
                checkExcessResult.setExcessReason(String.format(excessReasonTemplate, reviewVolume, firstVolume, secondVolume, diffVolume, firstVolumeStage));
                checkExcessResult.setExcessStandard(String.valueOf(firstVolumeStage));
                return result;
            }
            return result;
        }
        if(reviewVolume >= secondVolume && reviewVolume < thirdVolume){
            if(diffVolume > secondVolumeStage){
                checkExcessResult.setExcessCode(ExcessStatusEnum.EXCESS_ENUM_YES.getCode());
                checkExcessResult.setExcessReason(String.format(excessReasonTemplate, reviewVolume, secondVolume, thirdVolume, diffVolume, secondVolumeStage));
                checkExcessResult.setExcessStandard(String.valueOf(secondVolumeStage));
                return result;
            }
            return result;
        }
        if(reviewVolume >= thirdVolume && reviewVolume < fourVolume){
            if(diffVolume > thirdVolumeStage){
                checkExcessResult.setExcessCode(ExcessStatusEnum.EXCESS_ENUM_YES.getCode());
                checkExcessResult.setExcessReason(String.format(excessReasonTemplate, reviewVolume, thirdVolume, fourVolume, diffVolume, thirdVolumeStage));
                checkExcessResult.setExcessStandard(String.valueOf(thirdVolumeStage));
                return result;
            }
            return result;
        }
        if(reviewVolume >= fourVolume){
            if(diffVolume > fourVolumeStage){
                checkExcessResult.setExcessCode(ExcessStatusEnum.EXCESS_ENUM_YES.getCode());
                checkExcessResult.setExcessReason(String.format(excessReasonTemplate, reviewVolume, fourVolume, "∞", diffVolume, thirdVolumeStage));
                checkExcessResult.setExcessStandard(String.valueOf(fourVolumeStage));
                return result;
            }
            return result;
        }
        return result;
    }
}
