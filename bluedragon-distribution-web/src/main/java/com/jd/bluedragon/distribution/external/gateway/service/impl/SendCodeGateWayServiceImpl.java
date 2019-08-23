package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.sendcode.response.SendCodeCheckDto;
import com.jd.bluedragon.common.dto.sendcode.response.BatchSendCarInfoDto;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.batch.domain.BatchSend;
import com.jd.bluedragon.distribution.rest.send.DeliveryResource;
import com.jd.bluedragon.distribution.rest.sendprint.SendPrintResource;
import com.jd.bluedragon.distribution.sendprint.domain.BatchSendInfoResponse;
import com.jd.bluedragon.distribution.sendprint.domain.BatchSendResult;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.external.gateway.service.SendCodeGateWayService;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.Resource;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author : xumigen
 * @date : 2019/7/27
 */
public class SendCodeGateWayServiceImpl implements SendCodeGateWayService {

    @Resource
    private SendPrintResource sendPrintResource;

    @Autowired
    private BaseService baseService;

    @Autowired
    @Qualifier("deliveryResource")
    private DeliveryResource deliveryResource;

    @Override
    @JProfiler(jKey = "DMSWEB.SendCodeGateWayServiceImpl.carrySendCarInfoNew",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<List<BatchSendCarInfoDto>> carrySendCarInfoNew(List<String> sendCodes) {
        JdCResponse<List<BatchSendCarInfoDto>> jdCResponse = new JdCResponse<>();
        if(CollectionUtils.isEmpty(sendCodes)){
            jdCResponse.toError("参数不能为空");
            return jdCResponse;
        }
        List<BatchSend> batchSendList = new ArrayList<>();
        for(String item : sendCodes){
            BatchSend batchSend = new BatchSend();
            batchSend.setSendCode(item);
            batchSendList.add(batchSend);
        }
        BatchSendInfoResponse batchSendInfoResponse = sendPrintResource.carrySendCarInfo(batchSendList);
        List<BatchSendResult> batchSendResultList = batchSendInfoResponse.getData();
        if(!Objects.equals(batchSendInfoResponse.getCode(), JdResponse.CODE_OK)){
            jdCResponse.toError(batchSendInfoResponse.getMessage());
            return jdCResponse;
        }
        jdCResponse.toSucceed(batchSendInfoResponse.getMessage());
        if(CollectionUtils.isNotEmpty(batchSendResultList)){
            List<BatchSendCarInfoDto> infoDtoList = new ArrayList<>();
            for(BatchSendResult item : batchSendResultList){
                BatchSendCarInfoDto infoDto = new BatchSendCarInfoDto();
                infoDto.setPackageBarNum(item.getPackageBarNum());
                infoDto.setSendCode(item.getSendCode());
                infoDto.setTotalBoxNum(item.getTotalBoxNum());
                infoDtoList.add(infoDto);
            }
            jdCResponse.setData(infoDtoList);
        }
        return jdCResponse;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.SendCodeGateWayServiceImpl.checkSendCodeStatus",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<SendCodeCheckDto> checkSendCodeStatus(String sendCode) {
        InvokeResult<AbstractMap.Entry<Integer, String>> invokeResult = deliveryResource.checkSendCodeStatus(sendCode);
        JdCResponse<SendCodeCheckDto> jdCResponse = new JdCResponse<>();
        if(invokeResult == null){
            jdCResponse.toError("接口返回错误！");
            return jdCResponse;
        }
        if(!Objects.equals(invokeResult.getCode(), InvokeResult.RESULT_SUCCESS_CODE)){
            jdCResponse.toError(invokeResult.getMessage());
            return jdCResponse;
        }
        //成功也会返回数据
        AbstractMap.Entry<Integer, String> entry = invokeResult.getData();
        if(entry != null){
            SendCodeCheckDto dto = new SendCodeCheckDto();
            dto.setKey(entry.getKey());
            dto.setValue(entry.getValue());
            jdCResponse.setData(dto);
        }
        jdCResponse.toSucceed(invokeResult.getMessage());
        return jdCResponse;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.SendCodeGateWayServiceImpl.checkSendCodeAndAlliance",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdVerifyResponse<SendCodeCheckDto> checkSendCodeAndAlliance(String sendCode) {
        JdCResponse<SendCodeCheckDto> jdCResponse = this.checkSendCodeStatus(sendCode);
        JdVerifyResponse<SendCodeCheckDto> jdVerifyResponse = new JdVerifyResponse<>();
        if(!Objects.equals(jdCResponse.getCode(),JdCResponse.CODE_SUCCESS)){
            jdVerifyResponse.toError(jdCResponse.getMessage());
            return jdVerifyResponse;
        }

        jdVerifyResponse.toSuccess(jdCResponse.getMessage());
        jdVerifyResponse.setData(jdCResponse.getData());
        //判断加盟 给页面返回提示类型信息
        Integer receiveSite = BusinessUtil.getReceiveSiteCodeFromSendCode(sendCode);
        if(receiveSite == null){
            return jdVerifyResponse;
        }
        BaseStaffSiteOrgDto dto = baseService.queryDmsBaseSiteByCode(String.valueOf(receiveSite));
        if(dto != null && BusinessUtil.isAllianceBusiSite(dto.getSiteType(),dto.getSubType())){
            jdVerifyResponse.addPromptBox(0,"派送至加盟商请复重！");
        }
        return jdVerifyResponse;
    }
}
