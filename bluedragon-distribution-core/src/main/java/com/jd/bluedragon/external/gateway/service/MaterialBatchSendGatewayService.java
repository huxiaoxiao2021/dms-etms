package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.material.batch.request.MaterialBatchSendReq;
import com.jd.bluedragon.common.dto.material.batch.response.MaterialTypeDto;
import com.jd.bluedragon.common.dto.sendcode.response.SendCodeCheckDto;

import java.util.List;

/**
 * @ClassName MaterialBatchSendGatewayService
 * @Description 物资按类型批量发货网关
 * @Author wyh
 * @Date 2020/3/24 9:44
 **/
public interface MaterialBatchSendGatewayService {

    JdCResponse<Void> materialBatchSend(MaterialBatchSendReq request);

    JdCResponse<Void> cancelMaterialBatchSend(MaterialBatchSendReq request);

    JdCResponse<List<MaterialTypeDto>> listMaterialType(MaterialBatchSendReq req);

    JdCResponse<SendCodeCheckDto> checkSendCode(MaterialBatchSendReq req);
}
