package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.identity.IdentityContentEntity;
import com.jd.bluedragon.distribution.jss.JssService;
import com.jd.bluedragon.external.gateway.service.IdentityScanGatewayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sun.misc.BASE64Decoder;

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
@Service
@Slf4j
public class IdentityScanGatewayServiceImpl implements IdentityScanGatewayService {

    @Autowired
    private JssService jssService;

    @Value("${jss.pda.image.bucket}")
    private String bucket;

    @Override
    public JdCResponse<IdentityContentEntity> scanAndDistinguishIdentity(String IdentityBase64) {

        String url = null;
        try {
            byte[] in2b = new BASE64Decoder().decodeBuffer(IdentityBase64);
            url = jssService.uploadFile(bucket, in2b, "ID" + UUID.randomUUID().toString());
            log.info("[身份证识别]scanAndDistinguishIdentity上传成功 : url[{}]", url);
        } catch (Exception e) {
            log.error("[身份证识别]scanAndDistinguishIdentity图片上传时发生异常", e);
        }


        return null;
    }
}
