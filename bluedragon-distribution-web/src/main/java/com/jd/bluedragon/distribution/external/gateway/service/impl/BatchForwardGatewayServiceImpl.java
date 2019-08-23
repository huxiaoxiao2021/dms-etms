package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.send.request.WholeBatchRetransRequest;
import com.jd.bluedragon.common.dto.send.response.SendCodeSiteDto;
import com.jd.bluedragon.distribution.api.request.BatchForwardRequest;
import com.jd.bluedragon.distribution.base.domain.CreateAndReceiveSiteInfo;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.rest.batchForward.BatchForwardResource;
import com.jd.bluedragon.distribution.send.domain.SendResult;
import com.jd.bluedragon.external.gateway.service.BatchForwardGatewayService;
import com.jd.dms.logger.annotation.BusinessLog;
import com.jd.etms.sdk.util.DateUtil;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 整批转发
 * @author : xumigen
 * @date : 2019/7/11
 */
public class BatchForwardGatewayServiceImpl implements BatchForwardGatewayService {

    @Resource
    private BatchForwardResource batchForwardResource;

    @Override
    @JProfiler(jKey = "DMSWEB.BatchForwardGatewayServiceImpl.checkSendCodeAndGetSite",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<SendCodeSiteDto> checkSendCodeAndGetSite(String batchcode, Integer batchcodeFlag) {
        InvokeResult<CreateAndReceiveSiteInfo> invokeResult = batchForwardResource.checkSendCode(batchcode,batchcodeFlag);
        JdCResponse<SendCodeSiteDto> jdCResponse = new JdCResponse<>();
        if(invokeResult.getCode() == InvokeResult.RESULT_SUCCESS_CODE && invokeResult.getData() != null){
            CreateAndReceiveSiteInfo siteInfo = invokeResult.getData();
            SendCodeSiteDto sendCodeSiteDto = new SendCodeSiteDto();
            sendCodeSiteDto.setCreateSiteCode(siteInfo.getCreateSiteCode());
            sendCodeSiteDto.setCreateSiteName(siteInfo.getCreateSiteName());
            sendCodeSiteDto.setCreateSiteSubType(siteInfo.getCreateSiteSubType());
            sendCodeSiteDto.setCreateSiteType(siteInfo.getCreateSiteType());
            sendCodeSiteDto.setReceiveSiteCode(siteInfo.getReceiveSiteCode());
            sendCodeSiteDto.setReceiveSiteSubType(siteInfo.getReceiveSiteSubType());
            sendCodeSiteDto.setReceiveSiteType(siteInfo.getReceiveSiteType());
            sendCodeSiteDto.setReceiveSiteName(siteInfo.getReceiveSiteName());
            jdCResponse.toSucceed(invokeResult.getMessage());
            jdCResponse.setData(sendCodeSiteDto);
            return jdCResponse;
        }
        jdCResponse.toError(invokeResult.getMessage());
        return jdCResponse;
    }

    @Override
    @BusinessLog(sourceSys = 1,bizType = 100,operateType = 1007)
    @JProfiler(jKey = "DMSWEB.BatchForwardGatewayServiceImpl.batchForwardSend",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<String> batchForwardSend(WholeBatchRetransRequest retransRequest) {
        BatchForwardRequest request = new BatchForwardRequest();
        request.setOldSendCode(retransRequest.getOldSendCode());
        request.setNewSendCode(retransRequest.getNewSendCode());
        request.setUserCode(retransRequest.getUser().getUserCode());
        request.setUserName(retransRequest.getUser().getUserName());
        request.setSiteCode(retransRequest.getCurrentOperate().getSiteCode());
        request.setSiteName(retransRequest.getCurrentOperate().getSiteName());
        request.setBusinessType(retransRequest.getBusinessType());
        request.setOperateTime(DateUtil.format(retransRequest.getCurrentOperate().getOperateTime(),DateUtil.FORMAT_DATE_TIME));
        InvokeResult invokeResult = batchForwardResource.batchForwardSend(request);
        JdCResponse<String> jdCResponse = new JdCResponse<>();
        if(Objects.equals(invokeResult.getCode(), SendResult.CODE_OK)){
            jdCResponse.toSucceed(invokeResult.getMessage());
            return jdCResponse;
        }
        jdCResponse.toError(invokeResult.getMessage());
        return jdCResponse;
    }
}
