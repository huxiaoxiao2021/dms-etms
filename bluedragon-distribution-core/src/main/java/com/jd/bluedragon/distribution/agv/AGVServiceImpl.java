package com.jd.bluedragon.distribution.agv;


import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.agv.AGVPDARequest;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.sdk.common.domain.InvokeResult;
import com.jd.bluedragon.distribution.sdk.modules.agv.AGVJSFService;
import com.jd.bluedragon.distribution.sdk.modules.agv.model.AGVRequest;
import com.jd.fastjson.JSON;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;


@Service("agvService")
public class AGVServiceImpl implements AGVService {
    private final Logger log = LoggerFactory.getLogger(AGVServiceImpl.class);

    @Autowired
    private AGVJSFService agvjsfService;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Override
    @JProfiler(jKey = "DMSWEB.AGVServiceImpl.sortByAGV", jAppName = Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<Boolean> sortByAGV(AGVPDARequest pdaRequest) {
        try{
            log.info("AGVPDARequest:{}", JSON.toJSON(pdaRequest));
            return agvjsfService.sortByAGV(makeAGVRequest(pdaRequest));
        }catch (Exception e) {
            log.error("调用AGV出错:{}",e);
        }
        return new InvokeResult<Boolean>().toFail("调用AGV出错");
    }

    private AGVRequest makeAGVRequest(AGVPDARequest pdaRequest) {
        BaseStaffSiteOrgDto siteOrgDto = baseMajorManager.getBaseSiteBySiteId(pdaRequest.getCurrentOperate().getSiteCode());
        AGVRequest request = new AGVRequest();
        BeanUtils.copyProperties(pdaRequest, request);
        request.setAGVNumber(pdaRequest.getAgvNumber());
        request.setOperateType(pdaRequest.getAgvOperateType());
        request.setProvinceAgencyCode(Objects.isNull(siteOrgDto) ? null : siteOrgDto.getProvinceAgencyCode());
        request.setProvinceAgencyName(Objects.isNull(siteOrgDto) ? null : siteOrgDto.getProvinceAgencyName());
        request.setAreaHubCode(Objects.isNull(siteOrgDto) ? null : siteOrgDto.getAreaCode());
        request.setAreaHubName(Objects.isNull(siteOrgDto) ? null : siteOrgDto.getAreaName());
        request.setSiteCode(String.valueOf(pdaRequest.getCurrentOperate().getSiteCode()));
        request.setPositionCode(pdaRequest.getCurrentOperate().getOperatorData().getPositionCode());
        request.setSiteName(pdaRequest.getCurrentOperate().getSiteName());
        request.setOperateTime(pdaRequest.getCurrentOperate().getOperateTime());
        log.info("agvrequest:{}", JSON.toJSON(request));
        return request;
    }


}
