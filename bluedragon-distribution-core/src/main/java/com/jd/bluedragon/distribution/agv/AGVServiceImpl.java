package com.jd.bluedragon.distribution.agv;


import com.jd.bluedragon.common.dto.agv.AGVPDARequest;
import com.jd.bluedragon.distribution.sdk.common.domain.InvokeResult;
import com.jd.bluedragon.distribution.sdk.modules.agv.AGVJSFService;
import com.jd.bluedragon.distribution.sdk.modules.agv.model.AGVRequest;
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

    @Override
    public InvokeResult<Boolean> sortByAGV(AGVPDARequest pdaRequest) {
        try{
            return agvjsfService.sortByAGV(makeAGVRequest(pdaRequest));
        }catch (Exception e) {
            log.error("调用AGV出错:{}",e);
        }
        return new InvokeResult<Boolean>().toFail("调用AGV出错");
    }

    private AGVRequest makeAGVRequest(AGVPDARequest pdaRequest) {
        AGVRequest request = new AGVRequest();
        BeanUtils.copyProperties(pdaRequest, request);
        request.setProvinceAgencyCode(pdaRequest.getCurrentOperate().getProvinceAgencyCode());
        request.setProvinceAgencyName(pdaRequest.getCurrentOperate().getProvinceAgencyName());
        request.setAreaHubCode(pdaRequest.getCurrentOperate().getAreaHubCode());
        request.setAreaHubName(pdaRequest.getCurrentOperate().getAreaHubName());
        request.setSiteCode(String.valueOf(pdaRequest.getCurrentOperate().getSiteCode()));
        request.setPositionCode(pdaRequest.getCurrentOperate().getOperatorData().getPositionCode());
        request.setSiteName(pdaRequest.getCurrentOperate().getSiteName());
        request.setOperateTime(pdaRequest.getCurrentOperate().getOperateTime());
        return request;
    }


}
