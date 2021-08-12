package com.jd.bluedragon.distribution.weightAndVolumeCheck.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.dto.CheckExcessParam;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.dto.StandardDto;
import org.springframework.stereotype.Service;

import static java.text.NumberFormat.getPercentInstance;

/**
 * C网纯配外单，标准B处理
 * @author fanggang7
 * @time 2021-06-16 15:28:11 周三
 */
@Service("weightAndVolumeCPureTypeCheckBForDwsHandler")
public class WeightAndVolumeCPureTypeCheckBForDwsHandler extends AbstractCheckStandardHandler{

    /**
     * 校验标准B：
     *  1. 12700立方厘米=<体积<37037立方厘米时，误差标准正负0.8 kg（含）
     *  2. 37037立方厘米=<体积<64000立方厘米时，误差标准正负1 kg（含）
     *  3. 64000立方厘米=<体积<296252立方厘米时，误差标准正负1.5 kg（含）
     *  4. 体积>296252立方厘米时，误差标准正负2kg（含）
     * @return false:未超标  true:超标
     */
    @Override
    public StandardDto checkExcess(CheckExcessParam checkExcessParam) {
        StandardDto standardDto = new StandardDto();
        standardDto.setExcessFlag(this.isExcess(checkExcessParam, standardDto));
        return standardDto;
    }

    private boolean isExcess(CheckExcessParam checkExcessParam, StandardDto standardDto){
        Double reviewVolume = checkExcessParam.getReviewVolume();
        Double differenceValue = checkExcessParam.getDifferenceValue();
        Double moreBigValue = checkExcessParam.getMoreBigValue();

        Double dwsPureFirstVolume =  new Double(this.dwsPureStandardBFirstVolume);
        Double dwsPureSecondVolume =  new Double(this.dwsPureStandardBSecondVolume);
        Double dwsPureThirdVolume =  new Double(this.dwsPureStandardBThirdVolume);
        Double dwsPureFourthVolume =  new Double(this.dwsPureStandardBFourthVolume);

        Double dwsPureFirstVolumeDeviation =  new Double(this.dwsPureStandardBFirstVolumeDeviation);
        Double dwsPureSecondVolumeDeviation =  new Double(this.dwsPureStandardBSecondVolumeDeviation);
        Double dwsPureThirdVolumeDeviation =  new Double(this.dwsPureStandardBThirdVolumeDeviation);
        Double dwsPureFourthVolumeDeviation =  new Double(this.dwsPureStandardBFourthVolumeDeviation);

        String excessReasonTemplate = "分拣较大值为%s且体积在%s立方厘米至%s立方厘米之间并且误差%s超过标准值%s";
        StringBuilder stringBuilder = new StringBuilder();

        if (reviewVolume.compareTo(dwsPureFirstVolume) >= 0 && reviewVolume.compareTo(dwsPureSecondVolume) < 0) {
            if (differenceValue.compareTo(dwsPureFirstVolumeDeviation) > 0) {
                stringBuilder.append(dwsPureFirstVolumeDeviation);
                standardDto.setHitMessage(stringBuilder.toString());
                standardDto.setExcessReason(String.format(excessReasonTemplate, moreBigValue, dwsPureFirstVolume, dwsPureSecondVolume, differenceValue, dwsPureFirstVolumeDeviation));
                return true;
            }
            return false;
        }
        if (reviewVolume.compareTo(dwsPureSecondVolume) >= 0 && reviewVolume.compareTo(dwsPureThirdVolume) < 0) {
            if (differenceValue.compareTo(dwsPureSecondVolumeDeviation) > 0) {
                stringBuilder.append(dwsPureSecondVolumeDeviation);
                standardDto.setHitMessage(dwsPureSecondVolumeDeviation.toString());
                standardDto.setExcessReason(String.format(excessReasonTemplate, moreBigValue, dwsPureSecondVolume, dwsPureThirdVolume, differenceValue, dwsPureSecondVolumeDeviation));
                return true;
            }
            return false;
        }
        if (reviewVolume.compareTo(dwsPureThirdVolume) >= 0 && reviewVolume.compareTo(dwsPureFourthVolume) < 0) {
            if (differenceValue.compareTo(dwsPureThirdVolumeDeviation) > 0) {
                stringBuilder.append(dwsPureThirdVolumeDeviation);
                standardDto.setHitMessage(dwsPureSecondVolumeDeviation.toString());
                standardDto.setExcessReason(String.format(excessReasonTemplate, moreBigValue, dwsPureThirdVolume, dwsPureFourthVolume, differenceValue, dwsPureThirdVolumeDeviation));
                return true;
            }
            return false;
        }
        if (reviewVolume.compareTo(dwsPureFourthVolume) >= 0) {
            if (differenceValue.compareTo(dwsPureFourthVolumeDeviation) > 0) {
                stringBuilder.append(dwsPureThirdVolumeDeviation);
                standardDto.setHitMessage(dwsPureSecondVolumeDeviation.toString());
                standardDto.setExcessReason(String.format(excessReasonTemplate, moreBigValue, dwsPureFourthVolume, "∞", differenceValue, "体积2%"));
                return true;
            }
            return false;
        }
        return false;
    }

    private String getStandardVal(Double reviewVolume) {
        StringBuilder stringBuilder = new StringBuilder();
        //重量阀值
        Double dwsPureFirstVolume =  new Double(this.dwsPureStandardBFirstVolume);
        Double dwsPureSecondVolume =  new Double(this.dwsPureStandardBSecondVolume);
        Double dwsPureThirdVolume =  new Double(this.dwsPureStandardBThirdVolume);
        Double dwsPureFourthVolume =  new Double(this.dwsPureStandardBFourthVolume);

        //重量误差标准值
        Double dwsPureFirstVolumeDeviation =  new Double(this.dwsPureStandardBFirstVolumeDeviation);
        Double dwsPureSecondVolumeDeviation =  new Double(this.dwsPureStandardBSecondVolumeDeviation);
        Double dwsPureThirdVolumeDeviation =  new Double(this.dwsPureStandardBThirdVolumeDeviation);
        Double dwsPureFourthVolumeDeviation =  new Double(this.dwsPureStandardBFourthVolumeDeviation);

        if (reviewVolume.compareTo(dwsPureFirstVolume) >= 0 && reviewVolume.compareTo(dwsPureSecondVolume) < 0) {
            return stringBuilder.append(dwsPureFirstVolumeDeviation).toString();
        }
        if (reviewVolume.compareTo(dwsPureSecondVolume) >= 0 && reviewVolume.compareTo(dwsPureThirdVolume) < 0) {
            return stringBuilder.append(dwsPureSecondVolumeDeviation).toString();
        }
        if (reviewVolume.compareTo(dwsPureThirdVolume) >= 0 && reviewVolume.compareTo(dwsPureFourthVolume) < 0) {
            return stringBuilder.append(dwsPureThirdVolumeDeviation).toString();
        }
        if (reviewVolume.compareTo(dwsPureFourthVolume) >= 0) {
            return stringBuilder.append(getPercentInstance().format(dwsPureFourthVolumeDeviation)).toString();
        }
        return stringBuilder.toString();
    }
}
    
