package com.jd.bluedragon.distribution.weightAndVolumeCheck.service.impl;

import org.springframework.beans.factory.annotation.Value;

/**
 * @Author: liming522
 * @Description:
 * @Date: create in 2021/1/8 16:57
 */
public abstract class AbstractCheckStandardHandler implements WeightAndVolumeCheckStandardHandler{

     /**
      * C网抽检阈值:公斤
     * */
    @Value("${spotCheck.firstThresholdWeight:1.5}")
    public double firstThresholdWeight;
    @Value("${spotCheck.firstStage:0.5}")
    public double firstStage;
    @Value("${spotCheck.secondThresholdWeight:20.0}")
    public double secondThresholdWeight;
    @Value("${spotCheck.secondStage:1.0}")
    public double secondStage;
    @Value("${spotCheck.thirdThresholdWeight:50.0}")
    public double thirdThresholdWeight;
    @Value("${spotCheck.thirdStage:0.02}")
    public double thirdStage;

    /**
     * C 网抽检 三边之和 阈值 70cm/100cm/120cm/200cm
     * 误差： 0.8kg/1kg/1.5kg/2kg
     */
    @Value("${spotCheck.firstSumLWH:100}")
    public String firstSumLWH;
    @Value("${spotCheck.secondSumLWH:120}")
    public String secondSumLWH;
    @Value("${spotCheck.thirdSumLWH:200}")
    public String thirdSumLWH;
    @Value("${spotCheck.fourSumLWH:70}")
    public String fourSumLWH;

    @Value("${spotCheck.firstSumLWHStage:1}")
    public String firstSumLWHStage;
    @Value("${spotCheck.secondSumLWHStage:1.5}")
    public String secondSumLWHStage;
    @Value("${spotCheck.thirdSumLWHStage:2}")
    public String thirdSumLWHStage;
    @Value("${spotCheck.fourSumLWHStage:0.8}")
    public String fourSumLWHStage;

    /**
     * Dws抽检C网纯配运单A标准 重量比较值
     */
    @Value("${spotCheck.dws.cPure.standardA.firstWeight:1.5}")
    public String dwsPureStandardAFirstWeight;
    @Value("${spotCheck.dws.cPure.standardA.secondWeight:20}")
    public String dwsPureStandardASecondWeight;
    @Value("${spotCheck.dws.cPure.standardA.thirdWeight:50}")
    public String dwsPureStandardAThirdWeight;

    @Value("${spotCheck.dws.cPure.standardB.firstWeightDeviation:0.5}")
    public String dwsPureStandardBFirstWeightDeviation;
    @Value("${spotCheck.dws.cPure.standardB.secondWeightDeviation:0.5}")
    public String dwsPureStandardBSecondWeightDeviation;
    @Value("${spotCheck.dws.cPure.standardB.thirdWeightDeviation:1}")
    public String dwsPureStandardBThirdWeightDeviation;
    @Value("${spotCheck.dws.cPure.standardB.fourthWeightDeviation:0.02}")
    public String dwsPureStandardBFourthWeightDeviation;

    /**
     * Dws抽检C网纯配运单B标准 体积比较值
     */
    @Value("${spotCheck.dws.cPure.standardB.firstVolume:12700}")
    public String dwsPureStandardBFirstVolume;
    @Value("${spotCheck.dws.cPure.standardB.secondVolume:37037}")
    public String dwsPureStandardBSecondVolume;
    @Value("${spotCheck.dws.cPure.standardB.thirdVolume:64000}")
    public String dwsPureStandardBThirdVolume;
    @Value("${spotCheck.dws.cPure.standardB.fourthVolume:296252}")
    public String dwsPureStandardBFourthVolume;

    @Value("${spotCheck.dws.cPure.standardB.firstVolumeDeviation:0.8}")
    public String dwsPureStandardBFirstVolumeDeviation;
    @Value("${spotCheck.dws.cPure.standardB.secondVolumeDeviation:1}")
    public String dwsPureStandardBSecondVolumeDeviation;
    @Value("${spotCheck.dws.cPure.standardB.thirdVolumeDeviation:1.5}")
    public String dwsPureStandardBThirdVolumeDeviation;
    @Value("${spotCheck.dws.cPure.standardB.fourthVolumeDeviation:2}")
    public String dwsPureStandardBFourthVolumeDeviation;

    public Double keeTwoDecimals(Double param){
        if(param == null){
            return 0.00;
        }
        param = (double)Math.round(param*100)/100;
        return param;
    }

}
    
