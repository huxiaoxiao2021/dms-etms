package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.CheckSendCodeRequest;
import com.jd.bluedragon.common.dto.sendcode.response.BatchSendCarInfoDto;
import com.jd.bluedragon.common.dto.sendcode.response.SendCodeCheckDto;
import com.jd.bluedragon.common.dto.sendcode.response.SendCodeInfoDto;

import java.util.List;

/**
 * 批次号相关
 * 发布到物流网关 由安卓调用
 * @author : xumigen
 * @date : 2019/7/27
 */
public interface SendCodeGateWayService {

    /**
     * 获取批次下箱号、原包数量
     * @param sendCodes
     * @return
     */
    JdCResponse<List<BatchSendCarInfoDto>> carrySendCarInfoNew(List<String> sendCodes);

    /**
     * 判断批次号状态
     * 含有 一车一单操作增加提示，如果操作逆向则阻断
     * @param sendCode
     * @return
     */
    JdCResponse<SendCodeCheckDto> checkSendCodeStatus(String sendCode);

    /**
     * 判断批次号状态
     *  公用校验方法，目前用于以下场景
     * <p>
     *     1、批量一车一单
     * </p>
     * @param sendCode
     * @return
     */
    JdCResponse commonCheckSendCode(String sendCode);

    /**
     * 判断批次号状态与是否是加盟商站点
     * @param sendCode
     * @return
     */
    JdVerifyResponse<SendCodeCheckDto> checkSendCodeAndAlliance(String sendCode);

    /**
     * 判断批次号状态与是否是加盟商站点（额外校验： 批次的流向是否支持干支任务）
     * @return
     */
    JdVerifyResponse<SendCodeCheckDto> checkSendCodeAndAllianceForJy(CheckSendCodeRequest request);

    /**
     * 校验是否封车，没有封车就返回 站点信息等
     * @param sendCode
     * @return
     */
    JdCResponse<SendCodeInfoDto> checkSendCodeForPickupRegister(String sendCode);
}
