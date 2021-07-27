package com.jd.bluedragon.distribution.weightAndVolumeCheck.service.impl;

import com.jd.bluedragon.distribution.weightAndVolumeCheck.dto.CheckExcessParam;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.dto.StandardDto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @Author: liming522
 * @Description:
 * @Date: create in 2021/1/8 17:11
 */
@Service("weightAndVolumeCheckBHandler")
public class WeightAndVolumeCheckBHandler extends AbstractCheckStandardHandler{

    /**
     * 校验标准B：
     *  1. 70cm=<三边之和<100cm，误差标准正负0.8 kg（含）
     *  2. 100cm=<三边之和<120cm，误差标准正负1 kg（含）
     *  3. 120cm=<三边之和<200cm，误差标准正负1.5 kg（含）
     *  4. 三边之和>200cm，误差标准正负2kg（含）
     * @return false:未超标  true:超标
     */
    @Override
    public StandardDto checkExcess(CheckExcessParam checkExcessParam) {
        StandardDto standardDto = new StandardDto();
        StringBuilder excessReason = new StringBuilder();
        standardDto.setExcessFlag(this.isExcess(checkExcessParam, excessReason));
        standardDto.setExcessReason(excessReason.toString());
        standardDto.setHitMessage(this.getStandardVal(checkExcessParam.getSumLWH().doubleValue()));
        return standardDto;
    }

    private boolean isExcess(CheckExcessParam checkExcessParam, StringBuilder excessReason){
        BigDecimal sumLWH  = checkExcessParam.getSumLWH();
        Double differenceValue = checkExcessParam.getDifferenceValue();
        // 三边之和的阀值
        BigDecimal sumLWH70  = new BigDecimal(fourSumLWH);
        BigDecimal sumLWH100 = new BigDecimal(firstSumLWH);
        BigDecimal sumLWH120 = new BigDecimal(secondSumLWH);
        BigDecimal sumLWH200 = new BigDecimal(thirdSumLWH);

        //重量误差值
        Double sumLWHStage08 =  new Double(fourSumLWHStage);
        Double sumLWHStage1  =  new Double(firstSumLWHStage);
        Double sumLWHStage15 =  new Double(secondSumLWHStage);
        Double sumLWHStage2  =  new Double(thirdSumLWHStage);

        String excessReasonTemplate = "三边之和在%scm和%scm之间并且误差%s超过误差标准值%skg";
        if(sumLWH.compareTo(sumLWH70)>=0 && sumLWH.compareTo(sumLWH100)<0){
            if(differenceValue.compareTo(sumLWHStage08)>0){
                excessReason.append(String.format(excessReasonTemplate, sumLWH, sumLWH70, sumLWH100, differenceValue, sumLWHStage08));
                return true;
            }
            return  false;
        }

        if(sumLWH.compareTo(sumLWH100)>=0 && sumLWH.compareTo(sumLWH120)<0){
            if(differenceValue.compareTo(sumLWHStage1)>0){
                excessReason.append(String.format(excessReasonTemplate, sumLWH, sumLWH100, sumLWH120, differenceValue, sumLWHStage1));
                return true;
            }
            return false;
        }

        if(sumLWH.compareTo(sumLWH120)>=0 && sumLWH.compareTo(sumLWH200)<0){
            if(differenceValue.compareTo(sumLWHStage15)>0){
                excessReason.append(String.format(excessReasonTemplate, sumLWH, sumLWH120, sumLWH200, differenceValue, sumLWHStage15));
                return true;
            }
            return false;
        }

        if(sumLWH.compareTo(sumLWH200)>0){
            if(differenceValue.compareTo(sumLWHStage2)>0){
                excessReason.append(String.format(excessReasonTemplate, sumLWH, sumLWH200, "∞", differenceValue, sumLWHStage2));
                return true;
            }
            return false;
        }
        return false;
    }

    /**
     * 获取 标准B的误差标准值
     * 1.  70cm=<三边之和<100cm，误差标准正负0.8 kg（含）
     * 2. 100cm=<三边之和<120cm，误差标准正负1 kg（含）
     * 3. 120cm=<三边之和<200cm，误差标准正负1.5 kg（含）
     * 4. 三边之和>200cm，误差标准正负2kg（含）
     * @param sumLWH
     * @return
     */
    private String getStandardVal(double sumLWH) {
        StringBuilder stringBuilder = new StringBuilder();
        Double sumLWH70  = new Double(fourSumLWH);
        Double sumLWH100 = new Double(firstSumLWH);
        Double sumLWH120 = new Double(secondSumLWH);
        Double sumLWH200 = new Double(thirdSumLWH);

        if(sumLWH>= sumLWH70 && sumLWH< sumLWH100){
            return stringBuilder.append(fourSumLWHStage).toString();
        }
        if(sumLWH>=sumLWH100 && sumLWH< sumLWH120){
            return stringBuilder.append(firstSumLWHStage).toString();
        }
        if(sumLWH>=sumLWH120 && sumLWH<sumLWH200){
            return stringBuilder.append(secondSumLWHStage).toString();
        }
        if(sumLWH>=sumLWH200){
            return stringBuilder.append(thirdSumLWHStage).toString();
        }
        return stringBuilder.toString();
    }
}
    
