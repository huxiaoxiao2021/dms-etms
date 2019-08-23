package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.base.response.MsgBoxTypeEnum;
import com.jd.bluedragon.common.dto.base.response.ResponseCodeConstants;
import com.jd.bluedragon.common.dto.send.request.DeliveryVerifyRequest;
import com.jd.bluedragon.common.dto.send.request.SinglePackageSendRequest;
import com.jd.bluedragon.distribution.api.request.PackageSendRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.rest.send.DeliveryResource;
import com.jd.bluedragon.distribution.send.domain.SendResult;
import com.jd.bluedragon.distribution.send.service.DeliveryVerifyService;
import com.jd.bluedragon.distribution.send.utils.SendBizSourceEnum;
import com.jd.bluedragon.external.gateway.service.SendGatewayService;
import com.jd.dms.logger.annotation.BusinessLog;
import com.jd.ql.basic.util.DateUtil;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Objects;

/**
 * @author : xumigen
 * @date : 2019/7/20
 */
public class SendGatewayServiceImpl implements SendGatewayService {

    @Autowired
    @Qualifier("deliveryResource")
    private DeliveryResource deliveryResource;

    @Autowired
    @Qualifier("deliveryVerifyService")
    private DeliveryVerifyService deliveryVerifyService;

    @Override
    @JProfiler(jKey = "DMSWEB.SendGatewayServiceImpl.packageSendVerifyForBox",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdVerifyResponse packageSendVerifyForBox(DeliveryVerifyRequest request) {
        return deliveryVerifyService.packageSendVerifyForBoxCode(request);
    }

    @Override
    @JProfiler(jKey = "DMSWEB.SendGatewayServiceImpl.newPackageSendGoods",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    @BusinessLog(sourceSys = 1, bizType = 100, operateType = 1005)
    public JdVerifyResponse newPackageSendGoods(SinglePackageSendRequest cRequest) {
        // 安卓PDA发货
        PackageSendRequest request = new PackageSendRequest();
        request.setBizSource(SendBizSourceEnum.ANDROID_PDA_SEND.getCode());
        request.setIsForceSend(cRequest.isForceSend());
        request.setIsCancelLastSend(cRequest.isCancelLastSend());
        request.setReceiveSiteCode(cRequest.getReceiveSiteCode());
        request.setBoxCode(cRequest.getBoxCode());
        request.setSendCode(cRequest.getSendCode());
        request.setTurnoverBoxCode(cRequest.getTurnoverBoxCode());
//        request.setOpType(0);
//        request.setKey("");
        request.setUserCode(cRequest.getUser().getUserCode());
        request.setUserName(cRequest.getUser().getUserName());
        request.setSiteCode(cRequest.getCurrentOperate().getSiteCode());
        request.setSiteName(cRequest.getCurrentOperate().getSiteName());
        request.setBusinessType(cRequest.getBusinessType());
//        request.setId(0);
        request.setOperateTime(DateUtil.format(cRequest.getCurrentOperate().getOperateTime(),DateUtil.FORMAT_DATE_TIME));
        InvokeResult<SendResult> invokeResult = deliveryResource.newPackageSend(request);
        JdVerifyResponse jdVerifyResponse = new JdVerifyResponse();
        jdVerifyResponse.toSuccess();
        if(invokeResult.getCode() != InvokeResult.RESULT_SUCCESS_CODE ){
            jdVerifyResponse.toFail(invokeResult.getMessage());
            return jdVerifyResponse;
        }
        SendResult sendResult = invokeResult.getData();
        if(Objects.equals(sendResult.getKey(),SendResult.CODE_OK)){
            jdVerifyResponse.toSuccess(sendResult.getValue());
            return jdVerifyResponse;
        }

        if(Objects.equals(sendResult.getKey(),SendResult.CODE_WARN)){
            jdVerifyResponse.addBox(MsgBoxTypeEnum.PROMPT,sendResult.getKey(),sendResult.getValue());
            return jdVerifyResponse;
        }

        if(Objects.equals(sendResult.getKey(),SendResult.CODE_SENDED)){
            jdVerifyResponse.addBox(MsgBoxTypeEnum.INTERCEPT,sendResult.getKey(),sendResult.getValue());
            return jdVerifyResponse;
        }

        if(Objects.equals(sendResult.getKey(),SendResult.CODE_CONFIRM)){
            if(sendResult.getInterceptCode() == ResponseCodeConstants.JdVerifyResponseMsgBox.SEND_WRONG_SITE.getCode()){
                JdVerifyResponse.MsgBox msgBox = new JdVerifyResponse.MsgBox();
                msgBox.setType(MsgBoxTypeEnum.CONFIRM);
                msgBox.setCode(sendResult.getKey());
                msgBox.setMsg(sendResult.getValue());
                msgBox.setData(sendResult.getReceiveSiteCode());
                jdVerifyResponse.addBox(msgBox);
            }else{
                jdVerifyResponse.addBox(MsgBoxTypeEnum.CONFIRM,sendResult.getKey(),sendResult.getValue());
            }
            return jdVerifyResponse;
        }
        jdVerifyResponse.addBox(MsgBoxTypeEnum.INTERCEPT,sendResult.getKey(),sendResult.getValue());
        return jdVerifyResponse;
    }

}
