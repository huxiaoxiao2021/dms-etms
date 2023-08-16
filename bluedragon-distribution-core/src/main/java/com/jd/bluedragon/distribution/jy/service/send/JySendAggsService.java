package com.jd.bluedragon.distribution.jy.service.send;


import com.jd.bluedragon.distribution.jy.send.JySendAggsEntity;
import com.jd.bluedragon.distribution.jy.send.JySendAggsEntityQuery;

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

    List<JySendAggsEntity> getSendStatisticsByBizList(List<String> sendVehicleBizId);

    /**
     * 按发车任务查发货流向进度
     * @param sendVehicleBizId
     * @return
     */
    List<JySendAggsEntity> findBySendVehicleBiz(String sendVehicleBizId);

    //插入或修改主库
    Boolean insertOrUpdateJySendGoodsAggsMain(JySendAggsEntity entity);

    //插入或修改备库
    Boolean insertOrUpdateJySendGoodsAggsBak(JySendAggsEntity entity);

    /**
     * 查询一条发货任务的异常记录（不齐）
     */
    JySendAggsEntity findSendAggExistAbnormal(String sendVehicleBizId);

    /**
     * 根据bizId(派车明细单号)批量获取统计数据
     * @param bizIds
     * @return
     */
    List<JySendAggsEntity> findBySendVehicleDetailBizs(List<String> bizIds);
}


