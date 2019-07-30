package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.sendcode.response.SendCodeCheckDto;
import com.jd.bluedragon.common.dto.sendcode.response.BatchSendCarInfoDto;

import java.util.List;

/**
 * 批次号相关
 * @author : xumigen
 * @date : 2019/7/27
 */
public interface SendCodeGateWayService {

    JdCResponse<List<BatchSendCarInfoDto>> carrySendCarInfo(List<String> sendCodes);

    /**
     * 判断批次号状态
     * @param sendCode
     * @return
     */
    JdCResponse<SendCodeCheckDto> checkSendCodeStatus(String sendCode);

    /**
     * 判断批次号状态与是否是加盟商站点
     * @param sendCode
     * @return
     */
    JdVerifyResponse<SendCodeCheckDto> checkSendCodeAndAlliance(String sendCode);
}
