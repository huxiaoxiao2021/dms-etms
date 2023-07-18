package com.jd.bluedragon.distribution.jy.dao.send;

import com.jd.bluedragon.distribution.jy.send.*;

import java.util.List;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/7/5 16:34
 * @Description: 策略类
 */
public interface JySendPredictAggsDaoStrategy {

    Long getunScanSumByCondition(JySendPredictAggsRequest request);


    List<JySendPredictProductType> getSendPredictProductTypeList(JySendPredictAggsRequest query);
}
