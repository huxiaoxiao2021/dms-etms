package com.jd.bluedragon.distribution.jy.dao.send;

import com.jd.bluedragon.distribution.jy.send.JySendAggsEntity;
import com.jd.bluedragon.distribution.jy.send.JySendProductAggsEntity;
import com.jd.bluedragon.distribution.jy.send.JySendProductAggsEntityQuery;
import com.jd.bluedragon.distribution.jy.send.JySendVehicleProductType;

import java.util.List;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/1/3 17:49
 * @Description:
 */
public interface JySendProductAggsDaoStrategy {


    List<JySendVehicleProductType> getSendVehicleProductTypeList(JySendProductAggsEntityQuery query);

    Long getToScanCountSum(JySendProductAggsEntityQuery query);

}
