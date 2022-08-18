package com.jd.bluedragon.distribution.jy.manager;

import com.jd.bluedragon.Constants;
import com.jd.etms.framework.utils.cache.annotation.Cache;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.basic.api.domain.position.PositionDetailRecord;
import com.jdl.basic.api.service.position.PositionQueryJsfService;
import com.jdl.basic.common.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class PositionQueryJsfManagerImpl implements PositionQueryJsfManager {
    @Autowired
    private PositionQueryJsfService positionQueryJsfService;

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.PositionQueryJsfManagerImpl.queryOneByPositionCode", mState = {JProEnum.TP, JProEnum.FunctionError})
    @Cache(key = "BasicQueryWSManagerImpl.getDictList@args0@args1@args2", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000, redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
    public Result<PositionDetailRecord> queryOneByPositionCode(String positionCode) {
        return positionQueryJsfService.queryOneByPositionCode(positionCode);
    }
}
