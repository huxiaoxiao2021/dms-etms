package com.jd.bluedragon.distribution.weightAndVolumeCheck.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.dto.CheckExcessParam;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.dto.StandardDto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static java.text.NumberFormat.getPercentInstance;

/**
 * C网纯配外单，标准A处理
 * @author fanggang7
 * @time 2021-06-16 15:28:11 周三
 */
@Service("weightAndVolumeCPureTypeCheckAForDwsHandler")
public class WeightAndVolumeCPureTypeCheckAForDwsHandler extends AbstractCheckStandardHandler {

    /**
     * 校验是否超标
     * @param checkExcessParam 参数
     * @return 超标判定结果
     */
    @Override
    public StandardDto checkExcess(CheckExcessParam checkExcessParam) {
        StandardDto standardDto = new StandardDto();
        standardDto.setExcessFlag(this.isExcess(checkExcessParam, standardDto));
        standardDto.setHitMessage(this.getStandardVal(checkExcessParam.getSumLWH().doubleValue(), checkExcessParam.getCheckMoreBigValue()));
        return standardDto;
    }

    /**
     * 校验标准A：
     * ①分拣【较大值】1.5公斤以下但是计费系统【唯一值】计费较大值在1.5公斤以上，误差标准正负0.5 kg（含）
     * ②分拣【较大值】1.5公斤至20公斤（含）， 误差标准正负0.5 kg（含）
     * ③分拣【较大值】20公斤至50公斤（含）， 误差标准正负1 kg（含）
     * ④分拣【较大值】50公斤以上，误差标准为重量的正负2%（含）
     * @return false:未超标  true:超标
     */
    private boolean isExcess(CheckExcessParam checkExcessParam, StandardDto standardDto) {
        Double moreBigValue = checkExcessParam.getMoreBigValue();
        Double checkMoreBigValue = checkExcessParam.getCheckMoreBigValue();
        Double differenceValue = checkExcessParam.getDifferenceValue();
        Double reviewWeight = checkExcessParam.getReviewWeight();

        //重量阀值
        Double firstWeight = Double.parseDouble(dwsPureStandardAFirstWeight);
        double secondWeight = Double.parseDouble(dwsPureStandardASecondWeight);
        double thirdWeight = Double.parseDouble(dwsPureStandardAThirdWeight);

        //重量误差标准值
        Double firstStage = Double.parseDouble(dwsPureStandardBFirstWeightDeviation);
        Double secondStage = Double.parseDouble(dwsPureStandardBSecondWeightDeviation);
        Double thirdStage = Double.parseDouble(dwsPureStandardBThirdWeightDeviation);
        Double firthStage = Double.parseDouble(dwsPureStandardBFourthWeightDeviation);

        String excessReasonTemplate = "分拣较大值%s在%s公斤至%s公斤之间并且误差%s超过标准值%s";

        StringBuilder stringBuilder = new StringBuilder();
        if (firstWeight <= moreBigValue && checkMoreBigValue > firstWeight) {
            if (differenceValue > firstStage) {
                stringBuilder.append(firstStage);
                standardDto.setHitMessage(stringBuilder.toString());
                standardDto.setExcessReason(String.format(excessReasonTemplate, moreBigValue, Constants.DOUBLE_ZERO, firstWeight, differenceValue, firstStage));
                return true;
            }
            return false;
        }

        if (firstWeight < moreBigValue && moreBigValue <= secondWeight) {
            if (differenceValue > secondStage) {
                stringBuilder.append(secondStage);
                standardDto.setHitMessage(stringBuilder.toString());
                standardDto.setExcessReason(String.format(excessReasonTemplate, moreBigValue, firstWeight, secondWeight, differenceValue, firstStage));
                return true;
            }
            return false;
        }

        if (secondWeight < moreBigValue && moreBigValue <= thirdWeight) {
            if (differenceValue > thirdStage) {
                stringBuilder.append(thirdStage);
                standardDto.setHitMessage(stringBuilder.toString());
                standardDto.setExcessReason(String.format(excessReasonTemplate, moreBigValue, secondWeight, thirdWeight, differenceValue, thirdStage));
                return true;
            }
            return false;
        }

        if (moreBigValue > thirdWeight) {
            if (differenceValue > keeTwoDecimals(reviewWeight * firthStage)) {
                stringBuilder.append(getPercentInstance().format(firthStage));
                standardDto.setHitMessage(stringBuilder.toString());
                standardDto.setExcessReason(String.format(excessReasonTemplate, moreBigValue, thirdWeight, "∞", differenceValue, "重量2%"));
                return true;
            }
            return false;
        }
        return false;
    }

    private String getStandardVal(Double moreBigValue, Double checkMoreBigValue) {
        StringBuilder stringBuilder = new StringBuilder();
        //重量阀值
        Double firstWeight = Double.parseDouble(dwsPureStandardAFirstWeight);
        double secondWeight = Double.parseDouble(dwsPureStandardASecondWeight);
        double thirdWeight = Double.parseDouble(dwsPureStandardAThirdWeight);

        //重量误差标准值
        Double firstStage = Double.parseDouble(dwsPureStandardBFirstWeightDeviation);
        Double secondStage = Double.parseDouble(dwsPureStandardBSecondWeightDeviation);
        Double thirdStage = Double.parseDouble(dwsPureStandardBThirdWeightDeviation);
        Double firthStage = Double.parseDouble(dwsPureStandardBFourthWeightDeviation);

        if(moreBigValue > firstWeight && checkMoreBigValue <= firstWeight){
            return stringBuilder.append(firstStage).toString();
        }
        if (firstWeight < moreBigValue && moreBigValue <= secondWeight) {
            return stringBuilder.append(secondStage).toString();
        }
        if (secondWeight < moreBigValue && moreBigValue <= thirdWeight) {
            return stringBuilder.append(thirdStage).toString();
        }
        if (moreBigValue > thirdWeight) {
            return stringBuilder.append(getPercentInstance().format(firthStage)).toString();
        }
        return stringBuilder.toString();
    }
}
    
