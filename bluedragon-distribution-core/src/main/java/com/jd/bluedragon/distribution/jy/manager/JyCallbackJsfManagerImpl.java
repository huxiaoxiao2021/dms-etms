package com.jd.bluedragon.distribution.jy.manager;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.base.domain.InvokeWithMsgBoxResult;
import com.jd.bluedragon.distribution.jy.api.callback.JySendVehicleCallbackJsfService;
import com.jd.bluedragon.distribution.jy.api.callback.JyUnloadVehicleCallbackJsfService;
import com.jd.bluedragon.distribution.jy.dto.send.SendScanCallbackReqDto;
import com.jd.bluedragon.distribution.jy.dto.send.SendScanCallbackRespDto;
import com.jd.bluedragon.distribution.jy.dto.unload.UnloadScanCallbackReqDto;
import com.jd.bluedragon.distribution.jy.dto.unload.UnloadScanCallbackRespDto;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


/**
 * @author : caozhixing3
 * @version V1.0
 * @Project: ql-dms-distribution
 * @Package com.jd.bluedragon.distribution.jy.manager
 * @Description:
 * @date Date : 2024年01月23日 10:34
 */
@Component("jyCallbackJsfManager")
public class JyCallbackJsfManagerImpl implements JyCallbackJsfManager{

    private static final Logger log = LoggerFactory.getLogger(JyCallbackJsfManagerImpl.class);

    @Autowired
    @Qualifier("unloadCallbackJsfService")
    private JyUnloadVehicleCallbackJsfService unloadCallbackJsfService;

    @Autowired
    @Qualifier("sendCallbackJsfService")
    private JySendVehicleCallbackJsfService sendCallbackJsfService;

    /**
     * 拣运作业工作台 卸车验货 校验环节回调
     * @param request
     * @return
     *
     * InvokeResult 中的code 返回非成功的时候（不等于200）认为服务异常。会阻断后续的代码执行逻辑。
     * 如果只是为了做提醒类的场景，则放在msgbox中处理，依赖特定的msgbox code来做处理。
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JyCallbackJsfManagerImpl.unloadScanCheckOfCallback", mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeWithMsgBoxResult<UnloadScanCallbackRespDto> unloadScanCheckOfCallback(UnloadScanCallbackReqDto request) {
        InvokeWithMsgBoxResult<UnloadScanCallbackRespDto> result = new InvokeWithMsgBoxResult<>();
        result.toSuccess();
        if(request == null){
            return result;
        }
        try{
            return unloadCallbackJsfService.unloadScanCheckOfCallback(request);
        }catch (Exception e) {
            log.error("JyCallbackJsfManagerImpl验货时校验异常，入参：{}", JsonHelper.toJson(request),e);
            result.toBizError("后端接口异常，请重试");
            return result;
        }
    }

    /**
     * 拣运作业工作台 卸车验货 执行环节回调
     * @param request
     * @return
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JyCallbackJsfManagerImpl.unloadScanOfCallback", mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeWithMsgBoxResult<UnloadScanCallbackRespDto> unloadScanOfCallback(UnloadScanCallbackReqDto request) {
        InvokeWithMsgBoxResult<UnloadScanCallbackRespDto> result = new InvokeWithMsgBoxResult<>();
        result.toSuccess();
        if(request == null){
            return result;
        }
        try{
            return unloadCallbackJsfService.unloadScanOfCallback(request);
        }catch (Exception e) {
            log.error("JyCallbackJsfManagerImpl验货时回调异常，入参：{}", JsonHelper.toJson(request),e);
            return result;
        }
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JyCallbackJsfManagerImpl.sendScanCheckOfCallback", mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeWithMsgBoxResult<SendScanCallbackRespDto> sendScanCheckOfCallback(SendScanCallbackReqDto request){
        InvokeWithMsgBoxResult<SendScanCallbackRespDto> result = new InvokeWithMsgBoxResult<>();
        result.toSuccess();
        if(request == null){
            return result;
        }
        try{
            return sendCallbackJsfService.sendScanCheckOfCallback(request);
        }catch (Exception e) {
            log.error("JyCallbackJsfManagerImpl发货时校验异常，入参：{}", JsonHelper.toJson(request),e);
            result.toBizError("后端接口异常，请重试");
            return result;
        }
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JyCallbackJsfManagerImpl.sendScanOfCallback", mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeWithMsgBoxResult<SendScanCallbackRespDto> sendScanOfCallback(SendScanCallbackReqDto request) {
        InvokeWithMsgBoxResult<SendScanCallbackRespDto> result = new InvokeWithMsgBoxResult<>();
        result.toSuccess();
        if(request == null){
            return result;
        }
        try{
            return sendCallbackJsfService.sendScanOfCallback(request);
        }catch (Exception e) {
            log.error("JyCallbackJsfManagerImpl验货时回调异常，入参：{}", JsonHelper.toJson(request),e);
            return result;
        }
    }
}
