package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.base.response.MsgBoxTypeEnum;
import com.jd.bluedragon.common.dto.base.response.ResponseCodeConstants;
import com.jd.bluedragon.common.dto.send.request.DeliveryRequest;
import com.jd.bluedragon.common.dto.send.request.DeliveryVerifyRequest;
import com.jd.bluedragon.common.dto.send.request.DifferentialQueryRequest;
import com.jd.bluedragon.common.dto.send.request.SinglePackageSendRequest;
import com.jd.bluedragon.common.dto.send.request.TransPlanRequest;
import com.jd.bluedragon.common.dto.send.response.SendThreeDetailDto;
import com.jd.bluedragon.common.dto.send.response.TransPlanDto;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.ColdChainDeliveryRequest;
import com.jd.bluedragon.distribution.api.request.PackageSendRequest;
import com.jd.bluedragon.distribution.api.request.TransPlanScheduleRequest;
import com.jd.bluedragon.distribution.api.response.ColdChainSendResponse;
import com.jd.bluedragon.distribution.api.response.DeliveryResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.coldchain.domain.TransPlanDetailResult;
import com.jd.bluedragon.distribution.rest.send.ColdChainDeliveryResource;
import com.jd.bluedragon.distribution.rest.send.DeliveryResource;
import com.jd.bluedragon.distribution.send.domain.SendResult;
import com.jd.bluedragon.distribution.send.domain.SendThreeDetail;
import com.jd.bluedragon.distribution.send.domain.ThreeDeliveryResponse;
import com.jd.bluedragon.distribution.send.service.DeliveryVerifyService;
import com.jd.bluedragon.distribution.send.utils.SendBizSourceEnum;
import com.jd.bluedragon.external.gateway.service.SendGatewayService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.dms.logger.annotation.BusinessLog;
import com.jd.ql.basic.util.DateUtil;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
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

    @Autowired
    @Qualifier("coldChainDeliveryResource")
    private ColdChainDeliveryResource coldChainDeliveryResource;

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

    @Override
    @JProfiler(jKey = "DMSWEB.SendGatewayServiceImpl.getTransPlan",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    @BusinessLog(sourceSys = 1, bizType = 100, operateType = 1006)
    public JdCResponse<List<TransPlanDto>> getTransPlan(TransPlanRequest request){
        JdCResponse<List<TransPlanDto>> res=new JdCResponse<>();
        res.toSucceed();

        if (null==request.getCreateSiteCode() ||  null==request.getReceiveSiteCode()) {
            res.toFail("始发和目的站点都不能为空");
            return res;
        }

        TransPlanScheduleRequest req=new TransPlanScheduleRequest();
        req.setCreateSiteCode(request.getCreateSiteCode());
        req.setReceiveSiteCode(request.getReceiveSiteCode());

        ColdChainSendResponse<List<TransPlanDetailResult>> rs=coldChainDeliveryResource.getTransPlan(req);
        res.setCode(rs.getCode());
        res.setMessage(rs.getMessage());
        List<TransPlanDetailResult> data=rs.getData();
        if (null!=data){
            String datastr= JsonHelper.toJson(data);
            res.setData(JsonHelper.jsonToList(datastr,TransPlanDto.class));
        }

        return res;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.SendGatewayServiceImpl.checkJpWaybill",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    @BusinessLog(sourceSys = 1, bizType = 100, operateType = 1007)
    public JdCResponse<Boolean> checkJpWaybill(DeliveryRequest request){
        JdCResponse<Boolean> res=parameterCheck(request);
        if (!JdCResponse.CODE_SUCCESS.equals(res.getCode())){
            return res;
        }

        com.jd.bluedragon.distribution.api.request.DeliveryRequest req=new com.jd.bluedragon.distribution.api.request.DeliveryRequest();
        BeanUtils.copyProperties(request, req);
        if(null!=request.getUser()){
            req.setUserCode(request.getUser().getUserCode());
            req.setUserName(request.getUser().getUserName());
        }
        if(null!=request.getCurrentOperate()){
            req.setSiteCode(request.getCurrentOperate().getSiteCode());
            req.setSiteName(request.getCurrentOperate().getSiteName());
        }

        DeliveryResponse rs=deliveryResource.checkJpWaybill(req);
        if (null==rs){
            res.toFail("检验异常");
            return res;
        }

        if (!JdResponse.CODE_OK.equals(rs.getCode())){
            res.setCode(rs.getCode());
            res.setMessage(rs.getMessage());
            return res;
        }

        return res;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.SendGatewayServiceImpl.checkThreeDelivery",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    @BusinessLog(sourceSys = 1, bizType = 100, operateType = 1008)
    public JdCResponse<Boolean> checkThreeDeliveryNew(DeliveryRequest request){
        JdCResponse<Boolean> res=parameterCheck(request);
        if (!JdCResponse.CODE_SUCCESS.equals(res.getCode())){
            return res;
        }

        com.jd.bluedragon.distribution.api.request.DeliveryRequest req=new com.jd.bluedragon.distribution.api.request.DeliveryRequest();
        BeanUtils.copyProperties(request, req);
        if(null!=request.getUser()){
            req.setUserCode(request.getUser().getUserCode());
            req.setUserName(request.getUser().getUserName());
        }
        if(null!=request.getCurrentOperate()){
            req.setSiteCode(request.getCurrentOperate().getSiteCode());
            req.setSiteName(request.getCurrentOperate().getSiteName());
        }

        DeliveryResponse rs=deliveryResource.checkThreeDeliveryNew(req);
        if (null==rs){
            res.toFail("检验异常");
            return res;
        }

        if (!JdResponse.CODE_OK.equals(rs.getCode())){
            res.setCode(rs.getCode());
            res.setMessage(rs.getMessage());
            return res;
        }

        return res;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.SendGatewayServiceImpl.cancelLastDeliveryInfo",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    @BusinessLog(sourceSys = 1, bizType = 100, operateType = 1009)
    public JdCResponse<Boolean> cancelLastDeliveryInfo(DeliveryRequest request){
        JdCResponse<Boolean> res=parameterCheck(request);
        if (!JdCResponse.CODE_SUCCESS.equals(res.getCode())){
            return res;
        }

        com.jd.bluedragon.distribution.api.request.DeliveryRequest req=new com.jd.bluedragon.distribution.api.request.DeliveryRequest();
        BeanUtils.copyProperties(request, req);
        if(null!=request.getUser()){
            req.setUserCode(request.getUser().getUserCode());
            req.setUserName(request.getUser().getUserName());
        }
        if(null!=request.getCurrentOperate()){
            req.setSiteCode(request.getCurrentOperate().getSiteCode());
            req.setSiteName(request.getCurrentOperate().getSiteName());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            req.setOperateTime(sdf.format(request.getCurrentOperate().getOperateTime()));
        }

        ThreeDeliveryResponse rs=deliveryResource.cancelLastDeliveryInfo(req);
        if (null==rs){
            res.toFail("检验异常");
            return res;
        }

        if (!JdResponse.CODE_OK.equals(rs.getCode())){
            res.setCode(rs.getCode());
            res.setMessage(rs.getMessage());
            return res;
        }

        return res;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.SendGatewayServiceImpl.checkThreeDelivery",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    @BusinessLog(sourceSys = 1, bizType = 100, operateType = 1010)
    public JdCResponse<Boolean> checkThreeDelivery(List<DeliveryRequest> request){
        JdCResponse<Boolean> res=new JdCResponse<>();
        res.toSucceed();

        if (null==request || request.size()<=0){
            res.toFail("入参不能为空");
            return res;
        }
        List<com.jd.bluedragon.distribution.api.request.DeliveryRequest> listRequest =new ArrayList<>();
        for (DeliveryRequest ltem : request) {
            JdCResponse<Boolean> ltemcheck=parameterCheck(ltem);
            if (!JdCResponse.CODE_SUCCESS.equals(ltemcheck.getCode())){
                return ltemcheck;
            }

            com.jd.bluedragon.distribution.api.request.DeliveryRequest req=new com.jd.bluedragon.distribution.api.request.DeliveryRequest();
            BeanUtils.copyProperties(ltem, req);
            if(null!=ltem.getUser()){
                req.setUserCode(ltem.getUser().getUserCode());
                req.setUserName(ltem.getUser().getUserName());
            }
            if(null!=ltem.getCurrentOperate()){
                req.setSiteCode(ltem.getCurrentOperate().getSiteCode());
                req.setSiteName(ltem.getCurrentOperate().getSiteName());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                req.setOperateTime(sdf.format(ltem.getCurrentOperate().getOperateTime()));
            }

            listRequest.add(req);
        }

        ThreeDeliveryResponse rs=deliveryResource.checkThreeDelivery(listRequest);
        if (null==rs){
            res.toFail("检验异常");
            return res;
        }

        if (!JdResponse.CODE_OK.equals(rs.getCode())){
            res.setCode(rs.getCode());
            res.setMessage(rs.getMessage());
            return res;
        }

        return res;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.SendGatewayServiceImpl.coldChainSendDelivery",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    @BusinessLog(sourceSys = 1, bizType = 100, operateType = 1011)
    public JdCResponse<Boolean> coldChainSendDelivery(List<DeliveryRequest> request){
        JdCResponse<Boolean> res=new JdCResponse<>();
        res.toSucceed();

        if (null==request || request.size()<=0){
            res.toFail("入参不能为空");
            return res;
        }

        List<ColdChainDeliveryRequest> listRequest=new ArrayList<>();
        for (DeliveryRequest ltem : request) {
            JdCResponse<Boolean> ltemcheck=parameterCheck(ltem);
            if (!JdCResponse.CODE_SUCCESS.equals(ltemcheck.getCode())){
                return ltemcheck;
            }

            ColdChainDeliveryRequest req=new ColdChainDeliveryRequest();
            BeanUtils.copyProperties(ltem, req);
            if(null!=ltem.getUser()){
                req.setUserCode(ltem.getUser().getUserCode());
                req.setUserName(ltem.getUser().getUserName());
            }
            if(null!=ltem.getCurrentOperate()){
                req.setSiteCode(ltem.getCurrentOperate().getSiteCode());
                req.setSiteName(ltem.getCurrentOperate().getSiteName());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                req.setOperateTime(sdf.format(ltem.getCurrentOperate().getOperateTime()));
            }

            listRequest.add(req);
        }

        DeliveryResponse rs=coldChainDeliveryResource.coldChainSendDelivery(listRequest);
        if (null==rs){
            res.toFail("检验异常");
            return res;
        }

        if (!JdResponse.CODE_OK.equals(rs.getCode())){
            res.setCode(rs.getCode());
            res.setMessage(rs.getMessage());
            return res;
        }

        return res;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.SendGatewayServiceImpl.differentialQuery",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    @BusinessLog(sourceSys = 1, bizType = 100, operateType = 1012)
    public JdCResponse<List<SendThreeDetailDto>> differentialQuery(DifferentialQueryRequest request){
        JdCResponse<List<SendThreeDetailDto>> res=new JdCResponse<>();
        res.toSucceed();

        com.jd.bluedragon.distribution.api.request.DifferentialQueryRequest req=new com.jd.bluedragon.distribution.api.request.DifferentialQueryRequest();
        req.setQueryType(request.getQueryType());

        if (null!=request.getSendList() && request.getSendList().size()>0){
            List<com.jd.bluedragon.distribution.api.request.DeliveryRequest> listRequest =new ArrayList<>();
            for (DeliveryRequest ltem : request.getSendList()) {
                com.jd.bluedragon.distribution.api.request.DeliveryRequest dr=new com.jd.bluedragon.distribution.api.request.DeliveryRequest();
                BeanUtils.copyProperties(ltem, dr);
                if(null!=ltem.getUser()){
                    dr.setUserCode(ltem.getUser().getUserCode());
                    dr.setUserName(ltem.getUser().getUserName());
                }
                if(null!=ltem.getCurrentOperate()){
                    dr.setSiteCode(ltem.getCurrentOperate().getSiteCode());
                    dr.setSiteName(ltem.getCurrentOperate().getSiteName());
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    dr.setOperateTime(sdf.format(ltem.getCurrentOperate().getOperateTime()));
                }

                listRequest.add(dr);
            }

            req.setSendList(listRequest);
        }

        ThreeDeliveryResponse rs=deliveryResource.differentialQuery(req);
        if (null==rs){
            res.toFail("检验异常");
            return res;
        }

        if (JdResponse.CODE_OK.equals(rs.getCode())){
            List<SendThreeDetail> data=rs.getData();
            if (null!=data){
                String datastr= JsonHelper.toJson(data);
                res.setData(JsonHelper.jsonToList(datastr,SendThreeDetailDto.class));
            }
        }else {
            res.setCode(rs.getCode());
            res.setMessage(rs.getMessage());
            return res;
        }

        return res;
    }

    /**
     * 参数校验
     * @param request
     * @return
     */
    private JdCResponse<Boolean> parameterCheck(DeliveryRequest request){
        JdCResponse<Boolean> res=new JdCResponse<>();
        res.toSucceed();

        if (request == null) {
            res.toFail("入参不能为空");
            return res;
        }
        if(StringUtils.isBlank(request.getBoxCode())){
            res.toFail("箱号不能为空");
            return res;
        }
        if(request.getCurrentOperate()==null ||
                request.getCurrentOperate().getSiteCode() == 0){
            res.toFail("始发站点ID不能为空");
            return res;
        }
        if (null==request.getCurrentOperate().getOperateTime()){
            res.toFail("操作时间不能为空");
            return res;
        }
        if(request.getReceiveSiteCode() == null){
            res.toFail("目的地站点ID不能为空");
            return res;
        }

        return res;
    }
}
