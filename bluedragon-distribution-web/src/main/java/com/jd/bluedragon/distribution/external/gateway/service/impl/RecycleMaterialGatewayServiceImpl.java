package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.distribution.external.gateway.service.RecycleMaterialGatewayService;
import com.jd.bluedragon.distribution.rest.recyclematerial.RecycleMaterialResource;
import com.jd.ql.dms.common.domain.JdResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * @author : xumigen
 * @date : 2019/6/14
 */
public class RecycleMaterialGatewayServiceImpl implements RecycleMaterialGatewayService {

    @Autowired
    @Qualifier("recycleMaterialResource")
    private RecycleMaterialResource recycleMaterialResource;

    @Override
    public JdResponse<String> updateStatus(JSONObject vo) {
        return recycleMaterialResource.updateStatus(vo);
    }
}
