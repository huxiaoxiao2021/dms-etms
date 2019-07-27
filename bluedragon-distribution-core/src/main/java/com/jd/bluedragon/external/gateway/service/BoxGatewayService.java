package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.box.response.BoxDto;

import java.util.List;

/**
 * @author : xumigen
 * @date : 2019/7/3
 */
public interface BoxGatewayService {

    JdVerifyResponse<BoxDto> boxValidation(String boxCode, Integer operateType);

    JdCResponse<List<BoxDto>> getGroupEffectiveBoxes(String boxCode);
}
