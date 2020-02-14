package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.box.response.BoxDto;

import java.util.List;

/**
 * 箱号相关
 * 发布到物流网关 由安卓调用
 * @author : xumigen
 * @date : 2019/7/3
 */
public interface BoxGatewayService {

    JdVerifyResponse<BoxDto> boxValidation(String boxCode, Integer operateType);

    JdCResponse<List<BoxDto>> getGroupEffectiveBoxes(String boxCode);

    JdCResponse<BoxDto> getBoxInfo(String boxCode);

}
