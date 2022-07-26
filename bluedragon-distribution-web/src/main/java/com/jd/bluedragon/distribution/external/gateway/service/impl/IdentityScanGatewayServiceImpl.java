package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.identity.IdentityContentEntity;
import com.jd.bluedragon.common.dto.identity.IdentityRecogniseRequest;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.distribution.aicv.IDCRServiceProxy;
import com.jd.bluedragon.external.gateway.service.IdentityScanGatewayService;
import com.jd.wl.ai.cv.center.outter.api.dto.IDCRRequestDto;
import com.jd.wl.ai.cv.center.outter.api.dto.IDCRResponseDto;
import com.jd.wl.ai.cv.center.outter.api.dto.IDCRStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.external.gateway.service.impl
 * @ClassName: IdentityScanGatewayServiceImpl
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2022/7/7 00:57
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
@Service("identityScanGatewayService")
@Slf4j
public class IdentityScanGatewayServiceImpl implements IdentityScanGatewayService {

    @Autowired
    private IDCRServiceProxy idcrServiceProxy;

    @Autowired
    private UccPropertyConfiguration uccPropertyConfiguration;

    @Override
    public JdCResponse<IdentityContentEntity> recognise(String picUrl) {
        IDCRRequestDto idcrRequestDto = new IDCRRequestDto();
        idcrRequestDto.setServiceUUID(UUID.randomUUID().toString());
        idcrRequestDto.setPicUrl(picUrl);

        JdCResponse<IdentityContentEntity> jdCResponse = new JdCResponse<>();
        jdCResponse.toSucceed();

        IDCRResponseDto idcrResponseDto = idcrServiceProxy.recognisePhoto(idcrRequestDto);

        if (idcrResponseDto != null && IDCRStatusEnum.OK.getCode().equals(idcrResponseDto.getStatus())) {
            IdentityContentEntity identityContentEntity = new IdentityContentEntity();
            identityContentEntity.setName(idcrResponseDto.getRecognizedName());
            identityContentEntity.setIdNumber(idcrResponseDto.getRecognizedIDNo());
            jdCResponse.toSucceed();
            jdCResponse.setData(identityContentEntity);
        } else {
            jdCResponse.toFail(idcrResponseDto == null? IDCRStatusEnum.ERROR.getMessage() : idcrResponseDto.getMessage());
            return jdCResponse;
        }
        return jdCResponse;
    }

    @Override
    public JdCResponse<IdentityContentEntity> recogniseWithSwitch(IdentityRecogniseRequest recogniseRequest) {

        JdCResponse<IdentityContentEntity> jdCResponse = new JdCResponse<>();
        jdCResponse.toSucceed();

        if (!uccPropertyConfiguration.getIdentityRecogniseSiteSwitch().contains(Constants.STR_ALL)
                && !uccPropertyConfiguration.getIdentityRecogniseSiteSwitch().contains(String.valueOf(recogniseRequest.getSiteCode()))) {
            jdCResponse.toFail("该场地暂不支持身份证识别");
            return jdCResponse;
        }

        return this.recognise(recogniseRequest.getPicUrl());

    }
}
