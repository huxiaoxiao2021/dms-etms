package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.distribution.jy.send.JySendAggsEntity;
import com.jd.bluedragon.distribution.jy.send.JySendAggsEntityQuery;
import com.jd.bluedragon.distribution.jy.send.JySendProductAggsEntity;
import com.jd.bluedragon.distribution.jy.send.JySendProductAggsEntityQuery;
import com.jd.bluedragon.distribution.jy.unload.JyUnloadAggsEntity;

import java.util.List;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/1/9 11:30
 * @Description:
 */
public interface JySendOrUnloadAggDataGatewayService {

    JdCResponse<Boolean> checkJySendAggsData(JySendAggsEntityQuery query);


    JdCResponse<Boolean> checkJySendProductAggsData(JySendProductAggsEntityQuery query);
}
