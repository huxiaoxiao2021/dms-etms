package com.jd.bluedragon.distribution.jy.service.send;


import com.jd.bluedragon.distribution.jy.send.JySendAggsEntity;

/**
 * 发货数据统计表
 *
 * @author liuduo8
 * @email liuduo3@jd.com
 * @date 2022-05-30 15:26:08
 */
public interface JySendAggsService {
    JySendAggsEntity getVehicleSendStatistics(String sendVehicleBizId);


}

