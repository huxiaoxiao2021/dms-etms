package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.material.batch.request.MaterialBatchSendReq;
import com.jd.bluedragon.common.dto.material.batch.response.MaterialTypeDto;
import com.jd.bluedragon.common.dto.sendcode.response.SendCodeCheckDto;
import com.jd.bluedragon.distribution.api.request.material.batch.MaterialBatchSendRequest;
import com.jd.bluedragon.distribution.api.response.material.batch.MaterialTypeResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.material.util.MaterialServiceFactory;
import com.jd.bluedragon.distribution.rest.material.MaterialBatchSendResource;
import com.jd.bluedragon.external.gateway.service.MaterialBatchSendGatewayService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Resource;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName MaterialBatchSendGatewayServiceImpl
 * @Description
 * @Author wyh
 * @Date 2020/3/24 11:13
 **/
public class MaterialBatchSendGatewayServiceImpl implements MaterialBatchSendGatewayService {

    private static final byte SEND_MODE = MaterialServiceFactory.MaterialSendModeEnum.TYPE_BATCH_SEND.getCode();

    @Resource
    private MaterialBatchSendResource materialBatchSendResource;

    @Override
    @JProfiler(jKey = "DMSWEB.MaterialBatchSendGatewayServiceImpl.materialBatchSend",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<Void> materialBatchSend(MaterialBatchSendReq request) {
        JdCResponse<Void> response = new JdCResponse<>();
        if (null == request || StringUtils.isEmpty(request.getSendCode()) || CollectionUtils.isEmpty(request.getSendDetails())) {
            response.toError("参数不全！");
            return response;
        }

        MaterialBatchSendRequest restReq = new MaterialBatchSendRequest(SEND_MODE);

        List<MaterialBatchSendRequest.MaterialSendByTypeDetail> sendDetails = new ArrayList<>(request.getSendDetails().size());
        for (MaterialBatchSendReq.MaterialSendByTypeDetailDto sendDetail : request.getSendDetails()) {
            MaterialBatchSendRequest.MaterialSendByTypeDetail detail = new MaterialBatchSendRequest.MaterialSendByTypeDetail();
            detail.setMaterialName(sendDetail.getMaterialName());
            detail.setMaterialTypeCode(sendDetail.getMaterialTypeCode());
            detail.setSendNum(sendDetail.getSendNum());
            sendDetails.add(detail);
        }
        restReq.setSendDetails(sendDetails);

        setCommonSendRequest(request, restReq);

        JdResult<Boolean> result = materialBatchSendResource.materialBatchSend(restReq);
        if (result.isFailed()) {
            response.toError(result.getMessage());
            return response;
        }
        response.toSucceed(result.getMessage());
        return response;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.MaterialBatchSendGatewayServiceImpl.cancelMaterialBatchSend",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<Void> cancelMaterialBatchSend(MaterialBatchSendReq request) {
        JdCResponse<Void> response = new JdCResponse<>();
        if (null == request || StringUtils.isEmpty(request.getSendCode()) || null == request.getCurrentOperate() || request.getCurrentOperate().getSiteCode() == 0) {
            response.toError("参数不全！");
            return response;
        }

        MaterialBatchSendRequest restReq = new MaterialBatchSendRequest(SEND_MODE);
        setCommonSendRequest(request, restReq);

        JdResult<Boolean> result = materialBatchSendResource.cancelMaterialBatchSend(restReq);
        if (result.isFailed()) {
            response.toError(result.getMessage());
            return response;
        }
        response.toSucceed(result.getMessage());
        return response;
    }

    private void setCommonSendRequest(MaterialBatchSendReq request, MaterialBatchSendRequest restReq) {
        restReq.setSendCode(request.getSendCode());
        restReq.setSiteCode(request.getCurrentOperate().getSiteCode());
        restReq.setSiteName(request.getCurrentOperate().getSiteName());
        restReq.setUserCode(request.getUser().getUserCode());
        restReq.setUserErp(request.getUser().getUserErp());
        restReq.setUserName(request.getUser().getUserName());
    }

    @Override
    @JProfiler(jKey = "DMSWEB.MaterialBatchSendGatewayServiceImpl.listMaterialType",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<List<MaterialTypeDto>> listMaterialType(MaterialBatchSendReq request) {
        JdCResponse<List<MaterialTypeDto>> response = new JdCResponse<>();
        if (null == request) {
            response.toError("参数为空！");
            return response;
        }
        MaterialBatchSendRequest restReq = new MaterialBatchSendRequest(SEND_MODE);
        restReq.setSiteCode(request.getCurrentOperate().getSiteCode());
        restReq.setSiteName(request.getCurrentOperate().getSiteName());
        restReq.setUserCode(request.getUser().getUserCode());
        restReq.setUserErp(request.getUser().getUserErp());
        restReq.setUserName(request.getUser().getUserName());

        JdResult<List<MaterialTypeResponse>> result = materialBatchSendResource.listMaterialType(restReq);
        if (result.isFailed()) {
            response.toError(result.getMessage());
            return response;
        }
        response.setData(JSON.parseArray(JSON.toJSONString(result.getData()), MaterialTypeDto.class));
        response.toSucceed(result.getMessage());
        return response;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.MaterialBatchSendGatewayServiceImpl.getSendCodeDestination",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<SendCodeCheckDto> getSendCodeDestination(MaterialBatchSendReq request) {
        JdCResponse<SendCodeCheckDto> jdCResponse = new JdCResponse<>();
        if (request == null || StringUtils.isEmpty(request.getSendCode())) {
            jdCResponse.toError("请输入批次号！");
            return jdCResponse;
        }
        InvokeResult<AbstractMap.Entry<Integer, String>> result = materialBatchSendResource.getSendCodeDestination(request.getSendCode());
        if (result.getCode() != InvokeResult.RESULT_SUCCESS_CODE) {
            jdCResponse.toError(result.getMessage());
            return jdCResponse;
        }
        jdCResponse.toSucceed(result.getMessage());

        AbstractMap.Entry<Integer, String> entry = result.getData();
        if (entry != null) {
            SendCodeCheckDto sendCodeCheckDto = new SendCodeCheckDto();
            sendCodeCheckDto.setValue(entry.getValue());
            sendCodeCheckDto.setKey(entry.getKey());
            jdCResponse.setData(sendCodeCheckDto);
        }

        return jdCResponse;
    }
}
