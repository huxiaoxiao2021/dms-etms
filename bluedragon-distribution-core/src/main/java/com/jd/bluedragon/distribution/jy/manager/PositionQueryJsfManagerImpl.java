package com.jd.bluedragon.distribution.jy.manager;

import com.jd.bluedragon.Constants;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.basic.api.domain.position.PositionDetailRecord;
import com.jdl.basic.api.service.position.PositionQueryJsfService;
import com.jdl.basic.common.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class PositionQueryJsfManagerImpl implements PositionQueryJsfManager {
    
    @Qualifier("positionQueryBasicJsfService")
    @Autowired
    private PositionQueryJsfService positionQueryBasicJsfService;

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.PositionQueryJsfManagerImpl.queryOneByPositionCode", mState = {JProEnum.TP, JProEnum.FunctionError})
    public Result<PositionDetailRecord> queryOneByPositionCode(String positionCode) {
        return positionQueryBasicJsfService.queryOneByPositionCode(positionCode);
    }
}
