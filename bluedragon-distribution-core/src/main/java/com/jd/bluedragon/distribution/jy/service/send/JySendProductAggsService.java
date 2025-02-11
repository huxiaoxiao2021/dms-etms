package com.jd.bluedragon.distribution.jy.service.send;


import com.jd.bluedragon.distribution.jy.send.JySendProductAggsEntity;
import com.jd.bluedragon.distribution.jy.send.JySendProductAggsEntityQuery;
import com.jd.bluedragon.distribution.jy.send.JySendVehicleProductType;

import java.util.ArrayList;
import java.util.List;

/**
 * 发货数据统计表
 *
 * @author chenyaguo
 * @email
 * @date 2022-11-02 15:26:08
 */
public interface JySendProductAggsService {

    List<JySendVehicleProductType> getSendVehicleProductTypeList(JySendProductAggsEntityQuery query);

    Long getToScanCountSum(JySendProductAggsEntityQuery query);

    Boolean insertOrUpdateJySendProductAggsMain(JySendProductAggsEntity entity);

    Boolean insertOrUpdateJySendProductAggsBak(JySendProductAggsEntity entity);

    /**
     * 根据流向查询数量
     * @param query
     * @return
     */
    List<JySendProductAggsEntity> getToScanNumByEndSiteList(JySendProductAggsEntityQuery query);
}

