package com.jd.bluedragon.distribution.weightAndVolumeCheck.service.impl;

import com.jd.bluedragon.distribution.weightAndVolumeCheck.dto.CheckExcessParam;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.dto.StandardDto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static java.text.NumberFormat.getPercentInstance;

/**
 * @Author: liming522
 * @Description:
 * @Date: create in 2021/1/8 17:10
 */
@Service("weightAndVolumeCheckAHandler")
public class WeightAndVolumeCheckAHandler extends AbstractCheckStandardHandler{

    /**
     *  校验是否超标
     * @param checkExcessParam
     * @return
     */
    @Override
    public StandardDto checkExcess(CheckExcessParam checkExcessParam) {
        StandardDto standardDto = new StandardDto();
        standardDto.setExcessFlag(this.isExcess(checkExcessParam));
        standardDto.setHitMessage(this.getStandardVal(checkExcessParam.getMoreBigValue()));
        return standardDto;
    }

    /**
     * 校验标准A:
     *    1. 分拣【较大值】小于等于1公斤，不论误差多少均判断为正常
     *    2. 分拣【较大值】1公斤至20公斤（含),误差标准正负0.5 kg（含）
     *    3. 分拣【较大值】20公斤至50公斤（含),误差标准正负1 kg（含）
     *    4. 分拣【较大值】50公斤以上，误差标准为重量的正负2%（含）
     * @return  false:未超标  true:超标
     * @param checkExcessParam
     * @return
     */
    private boolean isExcess(CheckExcessParam checkExcessParam){
        Double moreBigValue = checkExcessParam.getMoreBigValue();
        Double differenceValue = checkExcessParam.getDifferenceValue();
        Double reviewWeight = checkExcessParam.getReviewWeight();
        // 特殊处理-如果 较大值 <=1kg 都算未超标
        if(moreBigValue <= 1){
            return  false;
        }
        //重量阀值
        BigDecimal firstWeight1   =   new BigDecimal(firstThresholdWeight);
        BigDecimal secondWeight20 =   new BigDecimal(secondThresholdWeight);
        BigDecimal thirdWeight50  =   new BigDecimal(thirdThresholdWeight);

        //重量误差标准值
        BigDecimal  firstStage05 = new BigDecimal(firstStage);
        BigDecimal  secondStage1 = new BigDecimal(secondStage);
        BigDecimal  thirdStage002 = new BigDecimal(thirdStage);

        if(moreBigValue <= firstWeight1.doubleValue()){
            return false;
        }

        if(firstWeight1.doubleValue() < moreBigValue && moreBigValue <= secondWeight20.doubleValue()){
            if(differenceValue >= firstStage05.doubleValue()){
                return true;
            }
            return  false;
        }

        if(secondWeight20.doubleValue() <moreBigValue && moreBigValue<=thirdWeight50.doubleValue()){
            if(differenceValue>=secondStage1.doubleValue()){
                return true;
            }
            return false;
        }

        if(moreBigValue>thirdWeight50.doubleValue()){
            if(differenceValue > keeTwoDecimals(reviewWeight*thirdStage002.doubleValue())){
                return true;
            }
            return false;
        }
        return false;
    }

    /**
     *
     * 获取A标准的：误差标准值
     *   1、1kg~20kg（含）的（+-）0.5kg（含）误差为正常
     *   2、20kg~50kg（含）的（+-）1kg（含）误差为正常
     *   3、50kg以上，允许误差值为总重量的2%（含）进行上下浮动
     * @param moreBigWeight 复核重量与重量体积较大值
     * @return
     */
    private String getStandardVal(double moreBigWeight){
        StringBuilder stringBuilder = new StringBuilder();
        if(moreBigWeight > firstThresholdWeight && moreBigWeight <= secondThresholdWeight){
            return stringBuilder.append(firstStage).toString();
        }
        if(moreBigWeight > secondThresholdWeight && moreBigWeight <= thirdThresholdWeight){
            return stringBuilder.append(secondStage).toString();
        }
        if(moreBigWeight > thirdThresholdWeight){
            return stringBuilder.append(getPercentInstance().format(thirdStage)).toString();
        }
        return stringBuilder.toString();
    }
}
    
