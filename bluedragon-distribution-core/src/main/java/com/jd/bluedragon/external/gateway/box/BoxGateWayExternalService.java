package com.jd.bluedragon.external.gateway.box;

import com.jd.bluedragon.external.gateway.base.GateWayBaseResponse;
import com.jd.bluedragon.external.gateway.dto.request.BoxGenerateRequest;
import com.jd.bluedragon.external.gateway.dto.response.BoxDto;

/**
 * 箱号相关，发布物流网关。
 * 目前的调用方有：经济网
 * @author : xumigen
 * @date : 2020/1/2
 */
public interface BoxGateWayExternalService {

    /**
     * 箱号获取
     * @param request 请求参数
     * @param pin 京东PIN码 物流网关会透传过来
     * @return
     */
    GateWayBaseResponse<BoxDto> generateBoxCodes(BoxGenerateRequest request,String pin);
}
