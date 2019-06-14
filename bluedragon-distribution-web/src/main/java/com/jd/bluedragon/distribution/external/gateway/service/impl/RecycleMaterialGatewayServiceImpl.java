package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.distribution.external.gateway.dto.request.RecycleMaterialRequest;
import com.jd.bluedragon.distribution.external.gateway.service.RecycleMaterialGatewayService;
import com.jd.bluedragon.distribution.rest.recyclematerial.RecycleMaterialResource;
import com.jd.etms.sdk.util.DateUtil;
import com.jd.ql.dms.common.domain.JdResponse;
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
    public JdResponse<String> updateStatus(RecycleMaterialRequest request) {
        JSONObject vo = new JSONObject();
        vo.put("materialCode",request.getMaterialCode());
        vo.put("operateType",request.getOperateType());
        vo.put("businessType",request.getBusinessType());
        vo.put("operatorErp",request.getOperatorErp());
        vo.put("siteCode",request.getCurrentOperate() != null?request.getCurrentOperate().getSiteCode():null);
        vo.put("siteName",request.getCurrentOperate() != null?request.getCurrentOperate().getSiteName():null);
        vo.put("operateTime",request.getCurrentOperate() != null?
                DateUtil.format(request.getCurrentOperate().getOperateTime(),DateUtil.FORMAT_DATE_TIME):null);
        vo.put("orgId",request.getCurrentOperate() != null?request.getCurrentOperate().getOrgId():null);
        vo.put("orgName",request.getCurrentOperate() != null?request.getCurrentOperate().getOrgName():null);
        return recycleMaterialResource.updateStatus(vo);
    }
}
