package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.distribution.jy.send.JySendAggsEntity;
import com.jd.bluedragon.distribution.jy.send.JySendProductAggsEntity;
import com.jd.bluedragon.distribution.jy.unload.JyUnloadAggsEntity;

import java.util.List;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/1/9 11:30
 * @Description:
 */
public interface JySendOrUnloadAggDataGatewayService {
    List<JySendAggsEntity> getSendAggMainData(JySendAggsEntity query);

    List<JySendAggsEntity> getSendAggBakData(JySendAggsEntity query);

    List<JySendProductAggsEntity> getSendProductAggMainData(JySendProductAggsEntity query);

    List<JySendProductAggsEntity> getSendProductAggBakData(JySendProductAggsEntity query);

    List<JyUnloadAggsEntity> getUnloadAggsMainData(JyUnloadAggsEntity query);

    List<JyUnloadAggsEntity> getUnloadAggsBakData(JyUnloadAggsEntity query);

}
