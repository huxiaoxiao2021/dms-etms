package com.jd.bluedragon.distribution.external.gateway.service.impl;

import IceInternal.Ex;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.ClientRequest;
import com.jd.bluedragon.distribution.rest.clientLog.ClientLogResource;
import com.jd.bluedragon.external.gateway.service.ClientLogGatewayService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2021/8/11
 * @Description:
 */
public class ClientLogGatewayServiceImpl implements ClientLogGatewayService {

    private static Logger logger = LoggerFactory.getLogger(ClientLogGatewayServiceImpl.class);

    @Autowired
    private ClientLogResource clientLogResource;

    /**
     * 批量一车一单发货
     *
     * @param request
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.ClientLogGatewayService.clientLog",mState={JProEnum.TP,JProEnum.FunctionError})
    public JdCResponse clientLog(ClientRequest request) {
        JdCResponse resp = new JdCResponse();
        try{
            JdResponse r = clientLogResource.save(request);
            resp.init(r.getCode(),r.getMessage());
        }catch (Exception e){
            logger.error("ClientLogGatewayService clientLog error! {}", JsonHelper.toJson(request),e);
            resp.toError();
        }
        return resp;
    }
}
