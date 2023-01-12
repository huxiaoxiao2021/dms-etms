package com.jd.bluedragon.core.jsf.device.impl;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.sdk.modules.device.domain.DeviceLocationExceptionOpLog;
import com.jd.bluedragon.sdk.modules.device.dto.DeviceLocationExceptionOpLogQo;
import com.jd.bluedragon.sdk.modules.device.dto.DeviceLocationExceptionOpLogVo;
import com.jd.bluedragon.sdk.modules.device.service.DeviceLocationExceptionOpLogApi;
import com.jd.dms.java.utils.sdk.base.PageData;
import com.jd.dms.java.utils.sdk.base.Result;
import com.jd.bluedragon.core.jsf.device.IDeviceLocationExceptionOpLogManager;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 设备异常操作位置记录
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2022-12-05 00:53:57 周一
 */
@Slf4j
@Component
public class IDeviceLocationExceptionOpLogManagerImpl implements IDeviceLocationExceptionOpLogManager {

    @Resource
    private DeviceLocationExceptionOpLogApi deviceLocationExceptionOpLogApi;

    /**
     * 查询统计设备位置总数
     *
     * @param deviceLocationExceptionOpLogQo 查询入参
     * @return 处理结果
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "IDeviceLocationExceptionOpLogManagerImpl.queryCount", mState = {JProEnum.TP, JProEnum.FunctionError})
    public Result<Long> queryCount(DeviceLocationExceptionOpLogQo deviceLocationExceptionOpLogQo) {
        log.info("IDeviceLocationExceptionOpLogManagerImpl.queryCount param {}", JSON.toJSONString(deviceLocationExceptionOpLogQo));
        Result<Long> result = Result.success();
        try {
            final Result<Long> remoteResult = deviceLocationExceptionOpLogApi.queryCount(deviceLocationExceptionOpLogQo);
            if (remoteResult == null || remoteResult.isFail()) {
                result.toFail(String.format("查询失败, %s", remoteResult != null ? remoteResult.getMessage() : "未知异常"));
            } else {
                result.setData(remoteResult.getData());
            }
        } catch (Exception e) {
            log.error("IDeviceLocationExceptionOpLogManagerImpl.queryCount exception {}", JSON.toJSONString(deviceLocationExceptionOpLogQo), e);
            result.toFail("系统异常");
        }
        return result;
    }

    /**
     * 分页查询设备位置记录
     *
     * @param deviceLocationExceptionOpLogQo 查询入参
     * @return 处理结果
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "IDeviceLocationExceptionOpLogManagerImpl.queryPageList", mState = {JProEnum.TP, JProEnum.FunctionError})
    public Result<PageData<DeviceLocationExceptionOpLogVo>> queryPageList(DeviceLocationExceptionOpLogQo deviceLocationExceptionOpLogQo) {
        log.info("IDeviceLocationExceptionOpLogManagerImpl.queryPageList param {}", JSON.toJSONString(deviceLocationExceptionOpLogQo));
        Result<PageData<DeviceLocationExceptionOpLogVo>> result = Result.success();
        try {
            final Result<PageData<DeviceLocationExceptionOpLogVo>> remoteResult = deviceLocationExceptionOpLogApi.queryPageList(deviceLocationExceptionOpLogQo);
            if (remoteResult == null || remoteResult.isFail()) {
                result.toFail(String.format("查询失败, %s", remoteResult != null ? remoteResult.getMessage() : "未知异常"));
            } else {
                result.setData(remoteResult.getData());
            }
        } catch (Exception e) {
            log.error("IDeviceLocationExceptionOpLogManagerImpl.add exception {}", JSON.toJSONString(deviceLocationExceptionOpLogQo), e);
            result.toFail("系统异常");
        }
        return result;
    }

    /**
     * 添加一条设备位置记录
     *
     * @param deviceLocationExceptionOpLog 设备异常位置操作记录
     * @return 处理结果
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "IDeviceLocationExceptionOpLogManagerImpl.add", mState = {JProEnum.TP, JProEnum.FunctionError})
    public Result<Long> add(DeviceLocationExceptionOpLog deviceLocationExceptionOpLog) {
        log.info("IDeviceLocationExceptionOpLogManagerImpl.add param {}", JSON.toJSONString(deviceLocationExceptionOpLog));
        Result<Long> result = Result.success();
        try {
            final Result<Long> remoteResult = deviceLocationExceptionOpLogApi.add(deviceLocationExceptionOpLog);
            if (remoteResult == null || remoteResult.isFail()) {
                result.toFail(String.format("添加失败, %s", remoteResult != null ? remoteResult.getMessage() : "未知异常"));
            } else {
                result.setData(remoteResult.getData());
            }
        } catch (Exception e) {
            log.error("IDeviceLocationExceptionOpLogManagerImpl.add exception {}", JSON.toJSONString(deviceLocationExceptionOpLog), e);
            result.toFail("系统异常");
        }
        return result;
    }

    /**
     * 根据ID更新
     *
     * @param deviceLocationExceptionOpLog 设备位置异常记录
     * @return 处理结果
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "IDeviceLocationExceptionOpLogManagerImpl.updateById", mState = {JProEnum.TP, JProEnum.FunctionError})
    public Result<Boolean> updateById(DeviceLocationExceptionOpLog deviceLocationExceptionOpLog) {
        log.info("IDeviceLocationExceptionOpLogManagerImpl.updateById param {}", JSON.toJSONString(deviceLocationExceptionOpLog));
        Result<Boolean> result = Result.success();
        try {
            final Result<Boolean> remoteResult = deviceLocationExceptionOpLogApi.updateById(deviceLocationExceptionOpLog);
            if (remoteResult == null || remoteResult.isFail()) {
                result.toFail(String.format("更新失败, %s", remoteResult != null ? remoteResult.getMessage() : "未知异常"));
            } else {
                result.setData(remoteResult.getData());
            }
        } catch (Exception e) {
            log.error("IDeviceLocationExceptionOpLogManagerImpl.updateById exception {}", JSON.toJSONString(deviceLocationExceptionOpLog), e);
            result.toFail("系统异常");
        }
        return result;
    }

    /**
     * 根据ID逻辑删除
     *
     * @param deviceLocationExceptionOpLog 设备位置异常记录
     * @return 处理结果
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "IDeviceLocationExceptionOpLogManagerImpl.logicDeleteById", mState = {JProEnum.TP, JProEnum.FunctionError})
    public Result<Boolean> logicDeleteById(DeviceLocationExceptionOpLog deviceLocationExceptionOpLog) {
        log.info("IDeviceLocationExceptionOpLogManagerImpl.logicDeleteById param {}", JSON.toJSONString(deviceLocationExceptionOpLog));
        Result<Boolean> result = Result.success();
        try {
            final Result<Boolean> remoteResult = deviceLocationExceptionOpLogApi.logicDeleteById(deviceLocationExceptionOpLog);
            if (remoteResult == null || remoteResult.isFail()) {
                result.toFail(String.format("删除失败, %s", remoteResult != null ? remoteResult.getMessage() : "未知异常"));
            } else {
                result.setData(remoteResult.getData());
            }
        } catch (Exception e) {
            log.error("IDeviceLocationExceptionOpLogManagerImpl.logicDeleteById exception {}", JSON.toJSONString(deviceLocationExceptionOpLog), e);
            result.toFail("系统异常");
        }
        return result;
    }

}
