package com.jd.bluedragon.core.jsf.device.impl;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.jsf.device.IDeviceLocationLogManager;
import com.jd.bluedragon.sdk.modules.device.domain.DeviceLocationLog;
import com.jd.bluedragon.sdk.modules.device.dto.DeviceLocationLogQo;
import com.jd.bluedragon.sdk.modules.device.dto.DeviceLocationLogVo;
import com.jd.bluedragon.sdk.modules.device.service.DeviceLocationLogApi;
import com.jd.dms.java.utils.sdk.base.PageData;
import com.jd.dms.java.utils.sdk.base.Result;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 设备位置记录
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2022-12-05 00:53:57 周一
 */
@Slf4j
@Component
public class IDeviceLocationLogManagerImpl implements IDeviceLocationLogManager {

    @Resource
    private DeviceLocationLogApi deviceLocationLogApi;

    /**
     * 查询统计设备位置记录个数
     *
     * @param deviceLocationLogQo 查询入参
     * @return 处理结果
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "IDeviceLocationLogManagerImpl.getLimitNums", mState = {JProEnum.TP, JProEnum.FunctionError})
    public Result<Long> queryCount(DeviceLocationLogQo deviceLocationLogQo) {
        log.info("IDeviceLocationLogManagerImpl.queryCount param {}", JSON.toJSONString(deviceLocationLogQo));
        Result<Long> result = Result.success();
        try {
            final Result<Long> remoteResult = deviceLocationLogApi.queryCount(deviceLocationLogQo);
            if (remoteResult == null || remoteResult.isFail()) {
                result.toFail(String.format("查询失败, %s", remoteResult != null ? remoteResult.getMessage() : "未知异常"));
            } else {
                result.setData(remoteResult.getData());
            }
        } catch (Exception e) {
            log.error("IDeviceLocationLogManagerImpl.queryCount exception {}", JSON.toJSONString(deviceLocationLogQo), e);
            result.toFail("系统异常");
        }
        return result;
    }

    /**
     * 分页查询设备位置记录
     *
     * @param deviceLocationLogQo 查询入参
     * @return 处理结果
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "IDeviceLocationLogManagerImpl.queryPageList", mState = {JProEnum.TP, JProEnum.FunctionError})
    public Result<PageData<DeviceLocationLogVo>> queryPageList(DeviceLocationLogQo deviceLocationLogQo) {
        log.info("IDeviceLocationLogManagerImpl.queryPageList param {}", JSON.toJSONString(deviceLocationLogQo));
        Result<PageData<DeviceLocationLogVo>> result = Result.success();
        try {
            final Result<PageData<DeviceLocationLogVo>> remoteResult = deviceLocationLogApi.queryPageList(deviceLocationLogQo);
            if (remoteResult == null || remoteResult.isFail()) {
                result.toFail(String.format("查询失败, %s", remoteResult != null ? remoteResult.getMessage() : "未知异常"));
            } else {
                result.setData(remoteResult.getData());
            }
        } catch (Exception e) {
            log.error("IDeviceLocationLogManagerImpl.queryPageList exception {}", JSON.toJSONString(deviceLocationLogQo), e);
            result.toFail("系统异常");
        }
        return result;
    }

    /**
     * 添加一条设备位置记录
     *
     * @param deviceLocationLog 设备位置记录
     * @return 处理结果
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "IDeviceLocationLogManagerImpl.add", mState = {JProEnum.TP, JProEnum.FunctionError})
    public Result<Long> add(DeviceLocationLog deviceLocationLog) {
        log.info("IDeviceLocationLogManagerImpl.add param {}", JSON.toJSONString(deviceLocationLog));
        Result<Long> result = Result.success();
        try {
            final Result<Long> remoteResult = deviceLocationLogApi.add(deviceLocationLog);
            if (remoteResult == null || remoteResult.isFail()) {
                result.toFail(String.format("添加失败, %s", remoteResult != null ? remoteResult.getMessage() : "未知异常"));
            } else {
                result.setData(remoteResult.getData());
            }
        } catch (Exception e) {
            log.error("IDeviceLocationLogManagerImpl.add exception {}", JSON.toJSONString(deviceLocationLog), e);
            result.toFail("系统异常");
        }
        return result;
    }

    /**
     * 根据ID更新
     *
     * @param deviceLocationLog 设备位置记录
     * @return 处理结果
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "IDeviceLocationLogManagerImpl.updateById", mState = {JProEnum.TP, JProEnum.FunctionError})
    public Result<Boolean> updateById(DeviceLocationLog deviceLocationLog) {
        log.info("IDeviceLocationLogManagerImpl.updateById param {}", JSON.toJSONString(deviceLocationLog));
        Result<Boolean> result = Result.success();
        try {
            final Result<Boolean> remoteResult = deviceLocationLogApi.updateById(deviceLocationLog);
            if (remoteResult == null || remoteResult.isFail()) {
                result.toFail(String.format("更新失败, %s", remoteResult != null ? remoteResult.getMessage() : "未知异常"));
            } else {
                result.setData(remoteResult.getData());
            }
        } catch (Exception e) {
            log.error("IDeviceLocationLogManagerImpl.updateById exception {}", JSON.toJSONString(deviceLocationLog), e);
            result.toFail("系统异常");
        }
        return result;
    }

    /**
     * 根据ID逻辑删除
     *
     * @param deviceLocationLog 设备位置记录
     * @return 处理结果
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "IDeviceLocationLogManagerImpl.logicDeleteById", mState = {JProEnum.TP, JProEnum.FunctionError})
    public Result<Boolean> logicDeleteById(DeviceLocationLog deviceLocationLog) {
        log.info("IDeviceLocationLogManagerImpl.logicDeleteById param {}", JSON.toJSONString(deviceLocationLog));
        Result<Boolean> result = Result.success();
        try {
            final Result<Boolean> remoteResult = deviceLocationLogApi.logicDeleteById(deviceLocationLog);
            if (remoteResult == null || remoteResult.isFail()) {
                result.toFail(String.format("删除失败, %s", remoteResult != null ? remoteResult.getMessage() : "未知异常"));
            } else {
                result.setData(remoteResult.getData());
            }
        } catch (Exception e) {
            log.error("IDeviceLocationLogManagerImpl.logicDeleteById exception {}", JSON.toJSONString(deviceLocationLog), e);
            result.toFail("系统异常");
        }
        return result;
    }

}
