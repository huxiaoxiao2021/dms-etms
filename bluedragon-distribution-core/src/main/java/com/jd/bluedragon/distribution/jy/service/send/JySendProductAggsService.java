package com.jd.bluedragon.distribution.jy.service.send;


import com.jd.bluedragon.distribution.jy.send.JySendProductAggsEntity;
import com.jd.bluedragon.distribution.jy.send.JySendProductAggsEntityQuery;
import com.jd.bluedragon.distribution.jy.send.JySendVehicleProductType;

import java.util.List;

/**
 * 发货数据统计表
 *
 * @author chenyaguo
 * @email
 * @date 2022-11-02 15:26:08
 */
public interface JySendProductAggsService {

    List<JySendVehicleProductType> getSendVehicleProductTypeList(String sendVehicleBizId);

    Long getToScanCountSum(String sendVehicleBizId);

    Boolean insertOrUpdateJySendProductAggsMain(JySendProductAggsEntity entity);

    Boolean insertOrUpdateJySendProductAggsBak(JySendProductAggsEntity entity);

    List<JySendProductAggsEntity> getSendProductAggMainData(JySendProductAggsEntity query);

    List<JySendProductAggsEntity> getSendProductAggBakData(JySendProductAggsEntity query);

    List<JySendProductAggsEntity> getSendAggsListByCondition(JySendProductAggsEntityQuery query);
}

