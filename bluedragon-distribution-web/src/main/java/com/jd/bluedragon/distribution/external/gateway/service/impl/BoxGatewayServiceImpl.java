package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.box.response.BoxDto;
import com.jd.bluedragon.distribution.api.response.BoxResponse;
import com.jd.bluedragon.distribution.rest.box.BoxResource;
import com.jd.bluedragon.external.gateway.service.BoxGatewayService;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 箱相关业务
 * @author  xumigen
 * @date : 2019/7/3
 */
public class BoxGatewayServiceImpl implements BoxGatewayService {

    @Resource
    private BoxResource boxResource;

    @Override
    public JdCResponse<BoxDto> boxValidation(String boxCode, Integer operateType) {
        BoxResponse boxResponse = boxResource.validation(boxCode,operateType);
        JdCResponse<BoxDto> jdResponse = new JdCResponse<>();
        if(Objects.equals(boxResponse.getCode(),BoxResponse.CODE_OK)){
            BoxDto boxDto = new BoxDto();
            boxDto.setBoxCode(boxResponse.getBoxCode());
            boxDto.setCreateSiteCode(boxResponse.getCreateSiteCode());
            boxDto.setCreateSiteName(boxResponse.getCreateSiteName());
            boxDto.setReceiveSiteCode(boxResponse.getReceiveSiteCode());
            boxDto.setReceiveSiteName(boxResponse.getReceiveSiteName());
            boxDto.setSiteType(boxResponse.getSiteType());
            boxDto.setTransportType(boxResponse.getTransportType());
            jdResponse.toSucceed();
            jdResponse.setData(boxDto);
        }else{
            jdResponse.toError(boxResponse.getMessage());
        }

        return jdResponse;
    }
}
