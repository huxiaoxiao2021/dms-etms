package com.jd.bluedragon.distribution.capability.send.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.capability.send.domain.SendChainEnum;
import com.jd.bluedragon.distribution.capability.send.domain.SendOfCAContext;
import com.jd.bluedragon.distribution.capability.send.exce.SendOfCapabilityAreaException;
import com.jd.bluedragon.distribution.capability.send.factory.SendOfCapabilityAreaFactory;
import com.jd.bluedragon.distribution.capability.send.service.ISendOfCapabilityAreaService;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.distribution.api.request.SendRequest;
import com.jd.bluedragon.distribution.send.domain.SendResult;
import com.jd.bluedragon.distribution.send.utils.SendBizSourceEnum;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2023/9/7
 * @Description:
 */
@Service
public class SendOfCapabilityAreaServiceImpl implements ISendOfCapabilityAreaService {

    private static final Logger logger = LoggerFactory.getLogger(SendOfCapabilityAreaServiceImpl.class);

    @Autowired
    private SendOfCapabilityAreaFactory sendOfCapabilityAreaFactory;


    /**
     * 发货
     *
     * 整体采用责任链模式+策略模式组合使用完成了对发货服务的封装
     *
     * 统一发货服务，封装了分拣条线所有发货服务，所有处理单元均在com.jd.bluedragon.distribution.capability.send.handler路径下
     *
     * 特别注意：针对返回值场景，中的JdVerifyResponse 的 msgBox 可自行根据使用场景在处理逻辑中直接处理，也可以等待SendRespMsgBoxHandler统一处理，但切记修改时需要考虑调入入口
     *
     * 具体设计文档参考: https://joyspace.jd.com/teams/DDWcQ85zjo0cDVQIMjFJ/vOOnoOJMJZ3yfs3QrhJi
     *
     * @param request
     * @return
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "com.jd.bluedragon.capability.send.service.impl.SendOfCapabilityAreaServiceImpl.doSend", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdVerifyResponse<SendResult> doSend(SendRequest request) {
        //基础参数校验 构建出参
        JdVerifyResponse<SendResult> response = paramCheck(request);

        //构建入参
        SendOfCAContext context = new SendOfCAContext();
        context.setRequest(request);
        context.setResponse(response);
        context.setBarCode(request.getBarCode());
        try {
            if(response.codeSuccess()){
                //调用执行逻辑
                sendOfCapabilityAreaFactory.getSendHandlerChain(
                                        SendBizSourceEnum.getEnum(request.getBizSource()))
                        .handle(context);
            }

        }catch (SendOfCapabilityAreaException e){
            //仅仅捕获自定义异常
            logger.error("SendOfCapabilityAreaServiceImpl.doSend error! msg:{},req:{}",e.getMessage(),JsonHelper.toJson(request),e);
            response.toError(e.getMessage());
            return response;
        }finally {
            if(logger.isInfoEnabled()){
                logger.info("SendOfCapabilityAreaServiceImpl.doSend context:{}",JsonHelper.toJson(context));
            }
        }

        return response;
    }



    /**
     * 基础参数校验
     * @param request
     * @return
     */
    private JdVerifyResponse<SendResult> paramCheck(SendRequest request){
        JdVerifyResponse<SendResult> response = new JdVerifyResponse<>();
        response.toSuccess();

        if(request.getBizSource() == null){
            response.toFail("BizSource is null!");
        }

        if(SendBizSourceEnum.getEnum(request.getBizSource()) == null){
            response.toFail(String.format("SendBizSourceEnum %s not be null!", request.getBizSource()));
        }

        if(StringUtils.isBlank(request.getBarCode())){
            response.toFail("BarCode is null!");
        }

        if(StringUtils.isBlank(request.getSendCode())){
            response.toFail("SendCode is null!");
        }

        if(request.getSiteCode()  == null){
            response.toFail("SiteCode is null!");
        }

        if(request.getIsCancelLastSend()  == null){
            response.toFail("IsCancelLastSend is null!");
        }

        return response;

    }

}
