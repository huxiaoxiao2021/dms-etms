package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.recyclematerial.request.BoxMaterialRelationJSFRequest;
import com.jd.bluedragon.common.dto.recyclematerial.request.RecycleMaterialRequest;
import com.jd.bluedragon.distribution.api.request.BoxMaterialRelationRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.rest.cyclebox.CycleBoxResource;
import com.jd.bluedragon.distribution.rest.recyclematerial.RecycleMaterialResource;
import com.jd.bluedragon.external.gateway.service.RecycleMaterialGatewayService;
import com.jd.dms.logger.annotation.BusinessLog;
import com.jd.etms.sdk.util.DateUtil;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
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

    @Autowired
    @Qualifier("cycleBoxResource")
    private CycleBoxResource cycleBoxResource;

    @Override
    @BusinessLog(sourceSys = 1,bizType = 2004,operateType = 20041)
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

    @Override
    @JProfiler(jKey = "DMSWEB.RecycleMaterialGatewayServiceImpl.getBoxMaterialRelation",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<String> getBoxMaterialRelation( String boxCode){
        JdCResponse<String> res = new JdCResponse<>();
        res.toSucceed();

        if (StringUtils.isBlank(boxCode)) {
            res.toFail("入参不能为空");
            return res;
        }

        InvokeResult<String> result=cycleBoxResource.getBoxMaterialRelation(boxCode);
        if (result.getCode()==InvokeResult.RESULT_SUCCESS_CODE){
            res.setCode(JdCResponse.CODE_SUCCESS);
        }
        else {
            res.setCode(result.getCode());
        }
        res.setMessage(result.getMessage());
        if (!StringUtils.isBlank(result.getData())){
            res.setData(result.getData());
        }

        return res;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.RecycleMaterialGatewayServiceImpl.getBoxMaterialRelation",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<Boolean> boxMaterialRelationAlter(BoxMaterialRelationJSFRequest request){
        JdCResponse<Boolean> res = new JdCResponse<>();
        res.toSucceed();

        if (null == request) {
            res.toFail("入参不能为空");
            return res;
        }

        BoxMaterialRelationRequest req=new BoxMaterialRelationRequest();
        if (null != request.getUser()) {
            req.setUserCode(request.getUser().getUserCode());
            req.setUserName(request.getUser().getUserName());
            req.setOperatorERP(request.getUser().getUserErp());
        }
        if (null != request.getCurrentOperate()) {
            req.setSiteCode(request.getCurrentOperate().getSiteCode());
            req.setSiteName(request.getCurrentOperate().getSiteName());
        }

        req.setBoxCode(request.getBoxCode());
        req.setMaterialCode(request.getMaterialCode());
        req.setBindFlag(request.getBindFlag());

        InvokeResult result=cycleBoxResource.boxMaterialRelationAlter(req);
        if (result.getCode()==InvokeResult.RESULT_SUCCESS_CODE){
            res.setCode(JdCResponse.CODE_SUCCESS);
        }
        else {
            res.setCode(result.getCode());
        }
        res.setMessage(result.getMessage());

        return res;
    }

}
