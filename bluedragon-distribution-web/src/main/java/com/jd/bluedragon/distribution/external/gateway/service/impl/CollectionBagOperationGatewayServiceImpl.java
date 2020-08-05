package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.material.collectionbag.CollectionBagOperationReq;
import com.jd.bluedragon.distribution.api.request.material.collectionbag.CollectionBagRequest;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.rest.material.CollectionBagOperationResource;
import com.jd.bluedragon.external.gateway.service.CollectionBagOperationGatewayService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * @ClassName CollectionBagOperationGatewayServiceImpl
 * @Description
 * @Author wyh
 * @Date 2020/7/8 18:45
 **/
public class CollectionBagOperationGatewayServiceImpl implements CollectionBagOperationGatewayService {

    @Autowired
    @Qualifier("collectionBagOperationResource")
    private CollectionBagOperationResource operationResource;

    @Override
    @JProfiler(jKey = "DMSWEB.CollectionBagOperationGatewayServiceImpl.receiveCollectionBag",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<Void> receiveCollectionBag(CollectionBagOperationReq request) {
        JdCResponse<Void> response = new JdCResponse<>();
        response.toSucceed();
        if (null == request || CollectionUtils.isEmpty(request.getBoxCodes())) {
            response.toError("参数不全！");
            return response;
        }
        CollectionBagRequest reqBody = new CollectionBagRequest();
        reqBody.setCollectionBagCodes(request.getBoxCodes());
        reqBody.setSiteCode(request.getCurrentOperate().getSiteCode());
        reqBody.setSiteName(request.getCurrentOperate().getSiteName());
        reqBody.setUserCode(request.getUser().getUserCode());
        reqBody.setUserErp(request.getUser().getUserErp());
        reqBody.setUserName(request.getUser().getUserName());
        JdResult<Boolean> result = operationResource.receive(reqBody);
        if (result.isFailed()) {
            response.toError(result.getMessage());
            return response;
        }
        response.toSucceed(result.getMessage());
        return response;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.CollectionBagOperationGatewayServiceImpl.sendCollectionBag",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<Void> sendCollectionBag(CollectionBagOperationReq request) {
        JdCResponse<Void> response = new JdCResponse<>();
        response.toSucceed();
        if (null == request || null == request.getReceiveSiteCode() || CollectionUtils.isEmpty(request.getBoxCodes())) {
            response.toError("参数不全！");
            return response;
        }

        CollectionBagRequest body = new CollectionBagRequest();
        body.setCollectionBagCodes(request.getBoxCodes());
        body.setReceiveSiteCode(request.getReceiveSiteCode());
        body.setReceiveSiteName(request.getReceiveSiteName());
        body.setSiteCode(request.getCurrentOperate().getSiteCode());
        body.setSiteName(request.getCurrentOperate().getSiteName());
        body.setUserErp(request.getUser().getUserErp());
        body.setUserCode(request.getUser().getUserCode());
        body.setUserName(request.getUser().getUserName());

        JdResult<Boolean> result = operationResource.send(body);
        if (result.isFailed()) {
            response.toError(result.getMessage());
            return response;
        }
        response.toSucceed(result.getMessage());
        return response;
    }
}
