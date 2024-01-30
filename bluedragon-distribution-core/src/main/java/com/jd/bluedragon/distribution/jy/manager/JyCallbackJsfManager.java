package com.jd.bluedragon.distribution.jy.manager;

import com.jd.bluedragon.distribution.base.domain.InvokeWithMsgBoxResult;
import com.jd.bluedragon.distribution.jy.dto.send.SendScanCallbackReqDto;
import com.jd.bluedragon.distribution.jy.dto.send.SendScanCallbackRespDto;
import com.jd.bluedragon.distribution.jy.dto.unload.UnloadScanCallbackReqDto;
import com.jd.bluedragon.distribution.jy.dto.unload.UnloadScanCallbackRespDto;

/**
 * @author : caozhixing3
 * @version V1.0
 * @Project: ql-dms-distribution
 * @Package com.jd.bluedragon.distribution.jy.manager
 * @Description:
 * @date Date : 2024年01月23日 10:34
 */
public interface JyCallbackJsfManager {

    /**
     * 执行卸载扫描回调检查
     * @param request 卸载扫描回调请求数据传输对象
     * @return UnloadScanCallbackRespDto 卸载扫描回调响应数据传输对象
     * @throws Exception 可能抛出异常
     */
    InvokeWithMsgBoxResult<UnloadScanCallbackRespDto> unloadScanCheckOfCallback(UnloadScanCallbackReqDto request);

    /**
     * 执行卸载扫描回调操作，并返回操作结果
     * @param request 卸载扫描回调请求数据传输对象
     * @return 返回卸载扫描回调响应数据传输对象
     */
    InvokeWithMsgBoxResult<UnloadScanCallbackRespDto> unloadScanOfCallback(UnloadScanCallbackReqDto request);

    /**
     * 发送扫描回调检查
     * @param request 发送扫描回调请求数据传输对象
     * @return SendScanCallbackRespDto 发送扫描回调响应数据传输对象
     */
    InvokeWithMsgBoxResult<SendScanCallbackRespDto> sendScanCheckOfCallback(SendScanCallbackReqDto request);

    /**
     * 发送扫描回调请求
     * @param request 发送扫描回调请求数据传输对象
     * @return SendScanCallbackRespDto 发送扫描回调响应数据传输对象
     */
    InvokeWithMsgBoxResult<SendScanCallbackRespDto> sendScanOfCallback(SendScanCallbackReqDto request);
}
