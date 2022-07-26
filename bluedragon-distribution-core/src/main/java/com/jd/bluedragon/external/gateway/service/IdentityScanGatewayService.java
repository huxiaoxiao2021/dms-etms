package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.identity.IdentityContentEntity;
import com.jd.bluedragon.common.dto.identity.IdentityRecogniseRequest;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.external.gateway.service
 * @ClassName: IdentityScanGatewayService
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2022/7/7 00:48
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public interface IdentityScanGatewayService {

    /**
     * 身份证识别
     * @param picUrl
     * @return
     */
    JdCResponse<IdentityContentEntity> recognise(String picUrl);


    /**
     * 身份证识别
     * @param recogniseRequest
     * @return
     */
    JdCResponse<IdentityContentEntity> recogniseWithSwitch(IdentityRecogniseRequest recogniseRequest);
}
