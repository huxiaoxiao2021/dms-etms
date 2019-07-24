package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.recyclematerial.request.RecycleMaterialRequest;
import com.jd.bluedragon.distribution.rest.recyclematerial.RecycleMaterialResource;
import com.jd.bluedragon.external.gateway.service.RecycleMaterialGatewayService;
import com.jd.etms.sdk.util.DateUtil;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * @author : xumigen
 * @date : 2019/6/14
 */
public class RecycleMaterialGatewayServiceImpl implements RecycleMaterialGatewayService {

    @Autowired
    @Qualifier("recycleMaterialResource")
    private RecycleMaterialResource recycleMaterialResource;

    @Override
    @JProfiler(jKey = "DMSWEB.RecycleMaterialGatewayServiceImpl.updateStatus",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<String> updateStatus(RecycleMaterialRequest request) {
        JSONObject vo = new JSONObject();
        vo.put("materialCode", request.getMaterialCode());
        vo.put("operateType", request.getOperateType());
        vo.put("businessType", request.getBusinessType());
        vo.put("operatorErp", request.getOperatorErp());
        vo.put("siteCode", request.getCurrentOperate() != null ? request.getCurrentOperate().getSiteCode() : null);
        vo.put("siteName", request.getCurrentOperate() != null ? request.getCurrentOperate().getSiteName() : null);
        vo.put("operateTime", request.getCurrentOperate() != null ?
                DateUtil.format(request.getCurrentOperate().getOperateTime(), DateUtil.FORMAT_DATE_TIME) : null);
        vo.put("orgId", request.getCurrentOperate() != null ? request.getCurrentOperate().getOrgId() : null);
        vo.put("orgName", request.getCurrentOperate() != null ? request.getCurrentOperate().getOrgName() : null);
        vo.put("destSiteCode", request.getDestSiteCode() != null ? request.getDestSiteCode() : null);
        vo.put("destSiteName", request.getDestSiteName() != null ? request.getDestSiteName() : null);

        JdCResponse<String> jdCResponse = new JdCResponse<>();
        JdResponse<String> response = recycleMaterialResource.updateStatus(vo);
        jdCResponse.setCode(response.getCode());
        jdCResponse.setMessage(response.getMessage());
        jdCResponse.setData(response.getData());
        return jdCResponse;
    }
}
