package com.jd.bluedragon.core.jsf.position.impl;

import com.jd.bluedragon.core.jsf.position.PositionManager;
import com.jdl.basic.api.domain.position.PositionData;
import com.jdl.basic.api.domain.position.PositionDetailRecord;
import com.jdl.basic.api.response.JDResponse;
import com.jdl.basic.api.service.position.PositionQueryJsfService;
import com.jdl.basic.common.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2022/8/10 16:42
 * @Description:
 */
@Service
public class PositionManagerImpl implements PositionManager {

    @Autowired
    private PositionQueryJsfService positionQueryJsfService;

    @Override
    public Result<PositionDetailRecord> queryOneByPositionCode(String positionCode) {
        return positionQueryJsfService.queryOneByPositionCode(positionCode);
    }

    @Override
    public JDResponse<PositionData> queryPositionWithIsMatchAppFunc(String positionCode) {
        return positionQueryJsfService.queryPositionWithIsMatchAppFunc(positionCode);
    }

    @Override
    public JDResponse<PositionData> queryPositionInfo(String positionCode) {
        return positionQueryJsfService.queryPositionInfo(positionCode);
    }


}
