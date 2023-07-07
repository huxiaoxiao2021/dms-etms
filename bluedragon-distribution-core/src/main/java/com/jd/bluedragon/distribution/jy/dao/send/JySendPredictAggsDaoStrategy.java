package com.jd.bluedragon.distribution.jy.dao.send;

import com.jd.bluedragon.distribution.jy.send.JySendPredictAggsPO;
import com.jd.bluedragon.distribution.jy.send.JySendPredictAggsRequest;

import java.util.List;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/7/5 16:34
 * @Description: 策略类
 */
public interface JySendPredictAggsDaoStrategy {

    Long getunScanCountByCondition(JySendPredictAggsRequest request);

    List<JySendPredictAggsPO> getListByCondition(JySendPredictAggsRequest request);
}
