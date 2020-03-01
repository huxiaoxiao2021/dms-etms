package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.material.warmbox.request.WarmBoxBoardRelationReq;
import com.jd.bluedragon.common.dto.material.warmbox.request.WarmBoxInboundReq;
import com.jd.bluedragon.common.dto.material.warmbox.request.WarmBoxOutboundReq;
import com.jd.bluedragon.common.dto.material.warmbox.response.WarmBoxInOutDto;
import com.jd.bluedragon.distribution.api.request.material.warmbox.WarmBoxBoardRelationRequest;
import com.jd.bluedragon.distribution.api.request.material.warmbox.WarmBoxInboundRequest;
import com.jd.bluedragon.distribution.api.request.material.warmbox.WarmBoxOutboundRequest;
import com.jd.bluedragon.distribution.api.response.material.warmbox.WarmBoxInOutResponse;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.rest.material.WarmBoxInOutResource;
import com.jd.bluedragon.external.gateway.service.WarmBoxInOutGatewayService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * @ClassName WarmBoxInOutGatewayServiceImpl
 * @Description
 * @Author wyh
 * @Date 2020/2/27 11:49
 **/
public class WarmBoxInOutGatewayServiceImpl implements WarmBoxInOutGatewayService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WarmBoxInOutGatewayServiceImpl.class);

    @Autowired
    @Qualifier("warmBoxInOutResource")
    private WarmBoxInOutResource warmBoxInOutResource;

    @Override
    @JProfiler(jKey = "DMSWEB.WarmBoxInOutGatewayServiceImpl.listBoxBoardRelations",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<WarmBoxInOutDto> listBoxBoardRelations(WarmBoxBoardRelationReq request) {
        JdCResponse<WarmBoxInOutDto> response = new JdCResponse<>();
        response.toSucceed();
        if (null == request) {
            response.toError("参数为空！");
            return response;
        }
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Gateway获取板和保温箱绑定关系. req:[{}]", JSON.toJSONString(request));
        }

        WarmBoxBoardRelationRequest queryParam = JSON.parseObject(JSON.toJSONString(request), WarmBoxBoardRelationRequest.class);
        JdResult<WarmBoxInOutResponse> result = warmBoxInOutResource.listBoxBoardRelation(queryParam);
        if (result.isFailed()) {
            response.toError(result.getMessage());
            return response;
        }
        response.setData(JSON.parseObject(JSON.toJSONString(result.getData()), WarmBoxInOutDto.class));
        response.toSucceed(result.getMessage());

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Gateway获取板和保温箱绑定关系. resp:[{}]", JSON.toJSONString(response));
        }

        return response;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.WarmBoxInOutGatewayServiceImpl.warmBoxInbound",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<Void> warmBoxInbound(WarmBoxInboundReq request) {
        JdCResponse<Void> response = new JdCResponse<>();
        response.toSucceed();
        if (null == request) {
            response.toError("参数为空！");
            return response;
        }
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Gateway保温箱入库. req:[{}]", JSON.toJSONString(request));
        }

        WarmBoxInboundRequest reqBody = new WarmBoxInboundRequest();
        reqBody.setBoardCode(request.getBoardCode());
        reqBody.setWarmBoxCodes(request.getWarmBoxCodes());
        reqBody.setSiteCode(request.getCurrentOperate().getSiteCode());
        reqBody.setSiteName(request.getCurrentOperate().getSiteName());
        reqBody.setUserCode(request.getUser().getUserCode());
        reqBody.setUserName(request.getUser().getUserName());
        JdResult<WarmBoxInOutResponse> result = warmBoxInOutResource.warmBoxInbound(reqBody);
        if (result.isFailed()) {
            response.toError(result.getMessage());
            return response;
        }
        response.toSucceed(result.getMessage());

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Gateway保温箱入库. resp:[{}]", JSON.toJSONString(response));
        }
        return response;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.WarmBoxInOutGatewayServiceImpl.warmBoxOutbound",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<Void> warmBoxOutbound(WarmBoxOutboundReq request) {
        JdCResponse<Void> response = new JdCResponse<>();
        response.toSucceed();
        if (null == request) {
            response.toError("参数为空！");
            return response;
        }
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Gateway保温箱出库. req:[{}]", JSON.toJSONString(request));
        }

        WarmBoxOutboundRequest body = new WarmBoxOutboundRequest();
        body.setBoardCode(request.getBoardCode());
        body.setWarmBoxCodes(request.getWarmBoxCodes());
        body.setOutboundType(request.getOutboundType());
        body.setSiteCode(request.getCurrentOperate().getSiteCode());
        body.setSiteName(request.getCurrentOperate().getSiteName());
        body.setUserCode(request.getUser().getUserCode());
        body.setUserName(request.getUser().getUserName());

        JdResult<WarmBoxInOutResponse> result = warmBoxInOutResource.warmBoxOutbound(body);
        if (result.isFailed()) {
            response.toError(result.getMessage());
            return response;
        }
        response.toSucceed(result.getMessage());

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Gateway保温箱出库. resp:[{}]", JSON.toJSONString(response));
        }
        return response;
    }
}
