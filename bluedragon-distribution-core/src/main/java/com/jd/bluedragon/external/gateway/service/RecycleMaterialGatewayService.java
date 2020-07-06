package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.recyclematerial.request.BoxMaterialRelationJSFRequest;
import com.jd.bluedragon.common.dto.recyclematerial.request.RecycleMaterialRequest;

/**
 * 循环物资实操装态更新 发布物流网关
 * 发布到物流网关 由安卓调用
 * @author : xumigen
 * @date : 2019/6/14
 */
public interface RecycleMaterialGatewayService {

    JdCResponse<String> updateStatus(RecycleMaterialRequest request);

    JdCResponse<String> getBoxMaterialRelation( String boxCode);

    JdCResponse<Boolean> boxMaterialRelationAlter(BoxMaterialRelationJSFRequest request);
}
