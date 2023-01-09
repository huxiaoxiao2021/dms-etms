package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.distribution.jy.send.JySendAggsEntity;
import com.jd.bluedragon.distribution.jy.send.JySendProductAggsEntity;
import com.jd.bluedragon.distribution.jy.service.send.JySendAggsService;
import com.jd.bluedragon.distribution.jy.service.send.JySendProductAggsService;
import com.jd.bluedragon.distribution.jy.service.unload.JyUnloadAggsService;
import com.jd.bluedragon.distribution.jy.unload.JyUnloadAggsEntity;
import com.jd.bluedragon.external.gateway.service.JySendOrUnloadAggDataGatewayService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/1/9 11:33
 * @Description:
 */
public class JySendOrUnloadAggDataGatewayServiceImpl implements JySendOrUnloadAggDataGatewayService {

    @Autowired
    private JySendAggsService jySendAggsService;
    @Autowired
    private JySendProductAggsService jySendProductAggsService;
    @Autowired
    private JyUnloadAggsService jyUnloadAggsService;

    @Override
    public List<JySendAggsEntity> getSendAggMainData(JySendAggsEntity query) {
        return jySendAggsService.getSendAggMainData(query);
    }

    @Override
    public List<JySendAggsEntity> getSendAggBakData(JySendAggsEntity query) {
        return jySendAggsService.getSendAggBakData(query);
    }

    @Override
    public List<JySendProductAggsEntity> getSendProductAggMainData(JySendProductAggsEntity query) {
        return jySendProductAggsService.getSendProductAggMainData(query);
    }

    @Override
    public List<JySendProductAggsEntity> getSendProductAggBakData(JySendProductAggsEntity query) {
        return jySendProductAggsService.getSendProductAggBakData(query);
    }

    @Override
    public List<JyUnloadAggsEntity> getUnloadAggsMainData(JyUnloadAggsEntity query) {
        return jyUnloadAggsService.getUnloadAggsMainData(query);
    }

    @Override
    public List<JyUnloadAggsEntity> getUnloadAggsBakData(JyUnloadAggsEntity query) {
        return jyUnloadAggsService.getUnloadAggsBakData(query);
    }
}
