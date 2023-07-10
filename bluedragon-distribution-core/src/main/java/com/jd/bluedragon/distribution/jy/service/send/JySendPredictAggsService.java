package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.distribution.jy.send.JySendPredictAggsPO;
import com.jd.bluedragon.distribution.jy.send.JySendPredictAggsRequest;
import com.jd.bluedragon.distribution.jy.send.JySendProductAggsEntityQuery;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/7/6 10:45
 * @Description: 波次待扫
 */
public interface JySendPredictAggsService {


    Long getToScanCountSum(JySendPredictAggsRequest query);

    /**
     * （主库）插入或者新增波次待扫数据
     * @param entity
     * @return
     */
    Boolean insertOrUpdateJySendPredictAggsMain(JySendPredictAggsPO entity);

    /**
     * （备库）插入或者新增波次待扫数据
     * @param entity
     * @return
     */
    Boolean insertOrUpdateJySendPredictAggsBak(JySendPredictAggsPO entity);
}
