package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.distribution.jy.send.JySendPredictAggsPO;
import com.jd.bluedragon.distribution.jy.send.JySendPredictAggsRequest;
import com.jd.bluedragon.distribution.jy.send.JySendPredictProductType;
import com.jd.bluedragon.distribution.jy.send.JySendProductAggsEntityQuery;

import java.util.List;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/7/6 10:45
 * @Description: 波次待扫
 */
public interface JySendPredictAggsService {


    /**
     * 获取待扫总数
     * @param query
     * @return
     */
    Long getToScanCountSum(JySendPredictAggsRequest query);

    /**
     * 获取产品类型列表及每个产品类型待扫数
     * @param query
     * @return
     */
    List<JySendPredictProductType> getSendPredictProductTypeList(JySendPredictAggsRequest query);

}
