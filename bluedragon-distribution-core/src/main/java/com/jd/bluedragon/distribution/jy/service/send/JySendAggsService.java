package com.jd.bluedragon.distribution.jy.service.send;


import com.jd.bluedragon.distribution.jy.send.JySendAggsEntity;

import java.util.List;

/**
 * 发货数据统计表
 *
 * @author liuduo8
 * @email liuduo3@jd.com
 * @date 2022-05-30 15:26:08
 */
public interface JySendAggsService {
    JySendAggsEntity getVehicleSendStatistics(String sendVehicleBizId);

    /**
     * 按发车任务查发货流向进度
     * @param sendVehicleBizId
     * @return
     */
    List<JySendAggsEntity> findBySendVehicleBiz(String sendVehicleBizId);

    //插入或修改
    int insertOrUpdateJySendGoodsAggs(JySendAggsEntity entity);

    //插入或修改
    int insertOrUpdateJySendGoodsAggsBak(JySendAggsEntity entity);
}


