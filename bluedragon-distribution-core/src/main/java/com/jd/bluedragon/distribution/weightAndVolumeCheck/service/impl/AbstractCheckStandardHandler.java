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
    @Value("${spotCheck.firstThresholdWeight:1.0}")
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

    public Double keeTwoDecimals(Double param){
        if(param == null){
            return 0.00;
        }
        param = (double)Math.round(param*100)/100;
        return param;
    }

}
    
