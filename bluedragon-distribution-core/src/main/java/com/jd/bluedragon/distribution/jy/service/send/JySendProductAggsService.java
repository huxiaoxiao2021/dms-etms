package com.jd.bluedragon.distribution.jy.service.send;


import com.jd.bluedragon.distribution.jy.send.JySendProductAggsEntity;
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

    int insertOrUpdateJySendProductAggsMain(JySendProductAggsEntity entity);

    int insertOrUpdateJySendProductAggsBak(JySendProductAggsEntity entity);
}

