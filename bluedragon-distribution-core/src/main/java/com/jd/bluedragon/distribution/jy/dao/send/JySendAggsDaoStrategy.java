package com.jd.bluedragon.distribution.jy.dao.send;

import com.jd.bluedragon.distribution.jy.send.JySendAggsEntity;

import java.util.List;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2022/12/12 19:52
 * @Description: 拣运JySendAggsDao策略接口
 */
public interface JySendAggsDaoStrategy {

    /**
     * 新增
     *
     * @param
     * @return
     */
     int insert(JySendAggsEntity entity);

     JySendAggsEntity getVehicleSendStatistics(String sendVehicleBizId);

     List<JySendAggsEntity> findBySendVehicleBiz(String sendVehicleBizId);

    /**
     * 新增
     *
     * @param
     * @return
     */
     int insertOrUpdate(JySendAggsEntity entity);
}
