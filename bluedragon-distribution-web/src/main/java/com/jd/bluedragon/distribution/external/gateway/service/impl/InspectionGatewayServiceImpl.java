package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.distribution.external.gateway.service.InspectionGatewayService;
import com.jd.bluedragon.distribution.inspection.domain.InspectionResult;
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
    public JdCResponse getStorageCode(String packageBarOrWaybillCode, Integer siteCode) {
        JdCResponse<InspectionResult> jdCResponse = new JdCResponse<>();
        jdCResponse.toSucceed();
        if(StringUtils.isEmpty(packageBarOrWaybillCode)){
            jdCResponse.toFail("单号不能为空");
            return jdCResponse;
        }
        if(siteCode == null){
            jdCResponse.toFail("站点不能为空");
            return jdCResponse;
        }
        JdResponse<InspectionResult> response = inspectionResource.getStorageCode(packageBarOrWaybillCode,siteCode);
        jdCResponse.setCode(response.getCode());
        jdCResponse.setMessage(response.getMessage());
        jdCResponse.setData(response.getData());
        return jdCResponse;
    }
}
