package com.jd.bluedragon.distribution.jy.manager;

import com.jd.bluedragon.Constants;
import com.jd.tms.basic.dto.BasicDictDto;
import com.jd.tms.basic.dto.BasicVehicleTypeDto;
import com.jd.tms.basic.dto.CommonDto;
import com.jd.tms.basic.ws.BasicQueryWS;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service("jyTransportManager")
public class JyTransportManagerImpl implements JyTransportManager{
    @Autowired
    BasicQueryWS basicQueryWS;

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMSWEB.JyTransportManagerImpl.getVehicleTypeByType", mState = {JProEnum.TP, JProEnum.FunctionError})
    public CommonDto<List<BasicDictDto>> getVehicleTypeByType(String owner, Integer type) {
        return basicQueryWS.getVehicleTypeByType(null,0);
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMSWEB.JyTransportManagerImpl.getVehicleTypeByVehicleNum", mState = {JProEnum.TP, JProEnum.FunctionError})
    public CommonDto<BasicVehicleTypeDto> getVehicleTypeByVehicleNum(String vehicleNum) throws Exception{
        return basicQueryWS.getVehicleTypeByVehicleNum(vehicleNum);
    }
}
