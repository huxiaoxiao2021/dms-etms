package com.jd.bluedragon.distribution.jy.api.callback;

import com.jd.bluedragon.distribution.base.domain.InvokeWithMsgBoxResult;
import com.jd.bluedragon.distribution.jy.dto.send.SendScanCallbackReqDto;
import com.jd.bluedragon.distribution.jy.dto.send.SendScanCallbackRespDto;
/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2024/1/18
 * @Description:
 *
 *  拣运作业工作台 装车发货 回调 协议
 */
public interface JySendVehicleCallbackJsfService {

    /**
     * 发送扫描回调检查
     * @param request 发送扫描回调请求数据传输对象
     * @return SendScanCallbackRespDto 发送扫描回调响应数据传输对象
     */
    InvokeWithMsgBoxResult<SendScanCallbackRespDto> sendScanCheckOfCallback(SendScanCallbackReqDto request);

    /**
     * 发送回调扫描的请求
     * @param request 发送回调扫描请求的参数
     * @return SendScanCallbackRespDto 发送回调扫描的响应结果
     */
    InvokeWithMsgBoxResult<SendScanCallbackRespDto> sendScanOfCallback(SendScanCallbackReqDto request);
}
