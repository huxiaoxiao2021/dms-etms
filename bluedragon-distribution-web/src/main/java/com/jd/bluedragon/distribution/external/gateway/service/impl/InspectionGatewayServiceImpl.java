package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.distribution.external.gateway.service.InspectionGatewayService;
import com.jd.bluedragon.distribution.rest.inspection.InspectionResource;
import com.jd.ql.dms.common.domain.JdResponse;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * 验货相关接口 发布物流网关
 * @author : xumigen
 * @date : 2019/6/14
 */
public class InspectionGatewayServiceImpl implements InspectionGatewayService {

    @Autowired
    @Qualifier("inspectionResource")
    private InspectionResource inspectionResource;

    @Override
    public JdResponse getStorageCode(String packageBarOrWaybillCode, Integer siteCode) {
        JdResponse jdResponse = new JdResponse();
        jdResponse.toSucceed();
        if(StringUtils.isEmpty(packageBarOrWaybillCode)){
            jdResponse.toFail("单号不能为空");
            return jdResponse;
        }
        if(siteCode == null){
            jdResponse.toFail("站点不能为空");
            return jdResponse;
        }
        jdResponse = inspectionResource.getStorageCode(packageBarOrWaybillCode,siteCode);
        return jdResponse;
    }
}
