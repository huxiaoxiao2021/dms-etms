package com.jd.bluedragon.distribution.jy.service.transport.impl;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.jsf.workStation.WorkStationGridManager;
import com.jd.bluedragon.distribution.jy.transport.api.JyTransportSendVehicleService;
import com.jd.bluedragon.distribution.jy.transport.dto.*;
import com.jd.dms.java.utils.sdk.base.Result;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 拣运运输车辆服务
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2023-05-10 17:56:46 周三
 */
@Slf4j
@Service("jyTransportSendVehicleService")
public class JyTransportSendVehicleServiceImpl implements JyTransportSendVehicleService {

    @Autowired
    private WorkStationGridManager workStationGridManager;

    /**
     * 验证运输发货车辆到达月台合法性
     *
     * @param requestDto 入参
     * @return 校验结果
     * @author fanggang7
     * @time 2023-05-08 21:05:43 周一
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JyTransportSendVehicleServiceImpl.validateVehicleArriveDock", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public Result<VehicleArriveDockResponseDto> validateVehicleArriveDock(VehicleArriveDockRequestDto requestDto) {
        log.info("JyTransportSendVehicleServiceImpl.validateVehicleArriveDock param {}", JSON.toJSONString(requestDto));
        Result<VehicleArriveDockResponseDto> result = Result.success();
        try {
            ;
        } catch (Exception e) {
            log.error("JyTransportSendVehicleServiceImpl.validateVehicleArriveDock exception {}", JSON.toJSONString(requestDto), e);
            result.toFail("系统异常");
        }
        return result;
    }

    /**
     * 获取运输车辆基础数据
     *
     * @param qo 查询入参
     * @return 查询结果
     * @author fanggang7
     * @time 2023-05-09 10:55:11 周二
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JyTransportSendVehicleServiceImpl.getVehicleArriveDockBaseData", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public Result<VehicleArriveDockBaseDataDto> getVehicleArriveDockBaseData(VehicleArriveDockBaseDataQo qo) {
        log.info("JyTransportSendVehicleServiceImpl.getVehicleArriveDockBaseData param {}", JSON.toJSONString(qo));
        Result<VehicleArriveDockBaseDataDto> result = Result.success();
        try {
            // 查询场地所有月台
        } catch (Exception e) {
            log.error("JyTransportSendVehicleServiceImpl.getVehicleArriveDockBaseData exception {}", JSON.toJSONString(qo), e);
            result.toFail("系统异常");
        }
        return result;
    }

    /**
     * 获取运输车辆靠台数据
     *
     * @param qo 查询入参
     * @return 查询结果
     * @author fanggang7
     * @time 2023-05-09 11:04:49 周二
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JyTransportSendVehicleServiceImpl.getVehicleArriveDockData", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public Result<VehicleArriveDockDataDto> getVehicleArriveDockData(VehicleArriveDockDataQo qo) {
        log.info("JyTransportSendVehicleServiceImpl.getVehicleArriveDockData param {}", JSON.toJSONString(qo));
        Result<VehicleArriveDockDataDto> result = Result.success();
        try {
            ;
        } catch (Exception e) {
            log.error("JyTransportSendVehicleServiceImpl.getVehicleArriveDockData exception {}", JSON.toJSONString(qo), e);
            result.toFail("系统异常");
        }
        return result;
    }
}
