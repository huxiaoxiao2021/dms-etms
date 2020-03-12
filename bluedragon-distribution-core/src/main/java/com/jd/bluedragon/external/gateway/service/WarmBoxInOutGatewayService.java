package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.material.warmbox.request.WarmBoxBoardRelationReq;
import com.jd.bluedragon.common.dto.material.warmbox.request.WarmBoxInboundReq;
import com.jd.bluedragon.common.dto.material.warmbox.request.WarmBoxOutboundReq;
import com.jd.bluedragon.common.dto.material.warmbox.response.WarmBoxInOutDto;

/**
 * @ClassName WarmBoxInOutGatewayService
 * @Description 保温箱操作Gateway
 * @Author wyh
 * @Date 2020/2/26 17:17
 **/
public interface WarmBoxInOutGatewayService {

    JdCResponse<WarmBoxInOutDto> listBoxBoardRelations(WarmBoxBoardRelationReq request);

    JdCResponse<Void> warmBoxInbound(WarmBoxInboundReq request);

    JdCResponse<Void> warmBoxOutbound(WarmBoxOutboundReq request);
}
