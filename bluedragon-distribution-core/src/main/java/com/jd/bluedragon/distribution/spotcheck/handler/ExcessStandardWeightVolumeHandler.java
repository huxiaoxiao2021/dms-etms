package com.jd.bluedragon.distribution.spotcheck.handler;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.spotcheck.domain.CheckExcessRequest;
import com.jd.bluedragon.distribution.spotcheck.domain.CheckExcessResult;
import com.jd.bluedragon.distribution.spotcheck.enums.ExcessStatusEnum;
import com.jd.bluedragon.dms.utils.MathUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 超标标准处理器
 *  以重量和体积为标准
 *
 * @author hujiping
 * @date 2021/8/23 3:01 下午
 */
@Service("excessStandardWeightVolumeHandler")
public class ExcessStandardWeightVolumeHandler implements IExcessStandardHandler {

    @Value("${spotCheck.bWeight:30}")
    public double bWeight;
    @Value("${spotCheck.bFirstWeightStage:1}")
    public double bFirstWeightStage;
    @Value("${spotCheck.bSecondWeightStage:0.04}")
    public double bSecondWeightStage;
    @Value("${spotCheck.bVolume:100000}")
    public double bVolume;
    @Value("${spotCheck.bFirstVolumeStage:100000}")
    public double bFirstVolumeStage;
    @Value("${spotCheck.bSecondVolumeStage:0.1}")
    public double bSecondVolumeStage;


    /**
     * 1、重量超标校验：
     *      录入重量<30kg,允许误差值(+-)1kg(含)
     *      录入重量>=30kg,允许误差值(+-)4%(含)
     * 2、体积超标校验：
     *      录入体积<0.1方,允许误差值(+-)0.1方(含)
     *      录入体积>=0.1方,允许误差值(+-)10%(含)
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
        Double reviewVolume = checkExcessRequest.getReviewVolume();
        Double contrastWeight = checkExcessRequest.getContrastWeight();
        Double contrastVolume = checkExcessRequest.getContrastVolume();
        // 超标原因
        String excessReasonTemplate = "重量体积标准:录入重量%s在%s至%s之间并且【重量差异:%s】超过标准值%s";
        double diffWeight = MathUtils.keepScale(Math.abs(reviewWeight - contrastWeight), 2);

        if(reviewWeight < bWeight && diffWeight > bFirstWeightStage){
            checkExcessResult.setExcessCode(ExcessStatusEnum.EXCESS_ENUM_YES.getCode());
            checkExcessResult.setExcessReason(String.format(excessReasonTemplate, reviewWeight, Constants.NUMBER_ZERO, bWeight, diffWeight, bFirstWeightStage));
            checkExcessResult.setExcessStandard(String.valueOf(bFirstWeightStage));
            return result;
        }
        if(reviewWeight >= bWeight && diffWeight > bSecondWeightStage * reviewWeight){
            checkExcessResult.setExcessCode(ExcessStatusEnum.EXCESS_ENUM_YES.getCode());
            checkExcessResult.setExcessReason(String.format(excessReasonTemplate, reviewWeight, bWeight, "∞", diffWeight, bSecondWeightStage));
            checkExcessResult.setExcessStandard(String.valueOf(bSecondWeightStage));
            return result;
        }
        excessReasonTemplate = "重量体积标准:录入体积%s在%s至%s之间并且【体积差异:%s】超过标准值%s";
        double diffVolume = MathUtils.keepScale(Math.abs(reviewVolume - contrastVolume), 2);
        if(reviewVolume < bVolume && diffVolume > bFirstVolumeStage){
            checkExcessResult.setExcessCode(ExcessStatusEnum.EXCESS_ENUM_YES.getCode());
            checkExcessResult.setExcessReason(String.format(excessReasonTemplate, reviewVolume, Constants.NUMBER_ZERO, bVolume, diffVolume, bFirstVolumeStage));
            checkExcessResult.setExcessStandard(String.valueOf(bFirstVolumeStage));
            return result;
        }
        if(reviewVolume >= bVolume && diffVolume > bSecondVolumeStage * reviewVolume){
            checkExcessResult.setExcessCode(ExcessStatusEnum.EXCESS_ENUM_YES.getCode());
            checkExcessResult.setExcessReason(String.format(excessReasonTemplate, reviewVolume, bVolume, "∞", diffVolume, bSecondVolumeStage));
            checkExcessResult.setExcessStandard(String.valueOf(bSecondVolumeStage));
            return result;
        }
        return result;
    }
}
