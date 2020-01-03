package com.jd.bluedragon.external.gateway.box;

import com.jd.bluedragon.external.gateway.base.GateWayBaseResponse;
import com.jd.bluedragon.external.gateway.dto.request.BoxGenerateRequest;
import com.jd.bluedragon.external.gateway.dto.response.BoxDto;
import com.jd.bluedragon.utils.JsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author : xumigen
 * @date : 2020/1/2
 */
public class BoxGateWayExternalServiceImpl implements BoxGateWayExternalService{
    private final Logger logger = LoggerFactory.getLogger(BoxGateWayExternalServiceImpl.class);

    @Override
    public GateWayBaseResponse<BoxDto> generateBoxCodes(BoxGenerateRequest request,String pin) {
        logger.info("生成箱号request[{}]", JsonHelper.toJson(request));
        return null;
    }
}
