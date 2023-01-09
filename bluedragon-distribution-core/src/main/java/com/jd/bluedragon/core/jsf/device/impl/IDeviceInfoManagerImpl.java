package com.jd.bluedragon.core.jsf.device.impl;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.jsf.device.IDeviceInfoManager;
import com.jd.dms.java.utils.sdk.base.PageData;
import com.jd.dms.java.utils.sdk.base.Result;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.basic.api.domain.device.DeviceInfo;
import com.jdl.basic.api.dto.device.qo.DeviceInfoQo;
import com.jdl.basic.api.dto.device.vo.DeviceInfoVo;
import com.jdl.basic.api.service.device.DeviceInfoApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 设备管理接口
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2022-12-05 00:53:05 周一
 */
@Slf4j
@Component
public class IDeviceInfoManagerImpl implements IDeviceInfoManager {

    @Resource
    private DeviceInfoApi deviceInfoApi;

    /**
     * 查询设备信息
     *
     * @return 分页数据
     * @author fanggang7
     * @time 2022-12-04 10:40:36 周日
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "IDeviceInfoManagerImpl.queryCount", mState = {JProEnum.TP, JProEnum.FunctionError})
    public Result<Long> queryCount(DeviceInfoQo deviceInfoQo) {
        log.info("IDeviceInfoManagerImpl.queryCount param {}", JSON.toJSONString(deviceInfoQo));
        Result<Long> result = Result.success();
        try {
            final Result<Long> remoteResult = deviceInfoApi.queryCount(deviceInfoQo);
            if (remoteResult == null || remoteResult.isFail()) {
                result.toFail(String.format("查询失败, %s", remoteResult != null ? remoteResult.getMessage() : "未知异常"));
            } else {
                result.setData(remoteResult.getData());
            }
        } catch (Exception e) {
            log.error("IDeviceInfoManagerImpl.queryCount exception {}", JSON.toJSONString(deviceInfoQo), e);
            result.toFail("系统异常");
        }
        return result;
    }

    /**
     * 查询设备信息分页列表
     *
     * @return 分页数据
     * @author fanggang7
     * @time 2022-12-04 10:40:36 周日
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "IDeviceInfoManagerImpl.queryPageList", mState = {JProEnum.TP, JProEnum.FunctionError})
    public Result<PageData<DeviceInfoVo>> queryPageList(DeviceInfoQo deviceInfoQo) {
        log.info("IDeviceInfoManagerImpl.queryPageList param {}", JSON.toJSONString(deviceInfoQo));
        Result<PageData<DeviceInfoVo>> result = Result.success();
        try {
            final Result<PageData<DeviceInfoVo>> remoteResult = deviceInfoApi.queryPageList(deviceInfoQo);
            if (remoteResult == null || remoteResult.isFail()) {
                result.toFail(String.format("查询失败, %s", remoteResult != null ? remoteResult.getMessage() : "未知异常"));
            } else {
                result.setData(remoteResult.getData());
            }
        } catch (Exception e) {
            log.error("IDeviceInfoManagerImpl.add exception {}", JSON.toJSONString(deviceInfoQo), e);
            result.toFail("系统异常");
        }
        return result;
    }

    /**
     * 添加一条设备记录
     *
     * @param deviceInfo 设备记录
     * @return 处理结果
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "IDeviceInfoManagerImpl.add", mState = {JProEnum.TP, JProEnum.FunctionError})
    public Result<Long> add(DeviceInfo deviceInfo) {
        log.info("IDeviceInfoManagerImpl.add param {}", JSON.toJSONString(deviceInfo));
        Result<Long> result = Result.success();
        try {
            final Result<Long> remoteResult = deviceInfoApi.add(deviceInfo);
            if (remoteResult == null || remoteResult.isFail()) {
                result.toFail(String.format("添加失败, %s", remoteResult != null ? remoteResult.getMessage() : "未知异常"));
            } else {
                result.setData(remoteResult.getData());
            }
        } catch (Exception e) {
            log.error("IDeviceInfoManagerImpl.add exception {}", JSON.toJSONString(deviceInfo), e);
            result.toFail("系统异常");
        }
        return result;
    }

    /**
     * 根据ID更新
     *
     * @param deviceInfo 设备记录
     * @return 处理结果
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "IDeviceInfoManagerImpl.updateById", mState = {JProEnum.TP, JProEnum.FunctionError})
    public Result<Boolean> updateById(DeviceInfo deviceInfo) {
        log.info("IDeviceInfoManagerImpl.updateById param {}", JSON.toJSONString(deviceInfo));
        Result<Boolean> result = Result.success();
        try {
            final Result<Boolean> remoteResult = deviceInfoApi.updateById(deviceInfo);
            if (remoteResult == null || remoteResult.isFail()) {
                result.toFail(String.format("更新失败, %s", remoteResult != null ? remoteResult.getMessage() : "未知异常"));
            } else {
                result.setData(remoteResult.getData());
            }
        } catch (Exception e) {
            log.error("IDeviceInfoManagerImpl.updateById exception {}", JSON.toJSONString(deviceInfo), e);
            result.toFail("系统异常");
        }
        return result;
    }

    /**
     * 根据ID逻辑删除
     *
     * @param deviceInfo 设备记录
     * @return 处理结果
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "IDeviceInfoManagerImpl.logicDeleteById", mState = {JProEnum.TP, JProEnum.FunctionError})
    public Result<Boolean> logicDeleteById(DeviceInfo deviceInfo) {
        log.info("IDeviceInfoManagerImpl.logicDeleteById param {}", JSON.toJSONString(deviceInfo));
        Result<Boolean> result = Result.success();
        try {
            final Result<Boolean> remoteResult = deviceInfoApi.logicDeleteById(deviceInfo);
            if (remoteResult == null || remoteResult.isFail()) {
                result.toFail(String.format("删除失败, %s", remoteResult != null ? remoteResult.getMessage() : "未知异常"));
            } else {
                result.setData(remoteResult.getData());
            }
        } catch (Exception e) {
            log.error("IDeviceInfoManagerImpl.logicDeleteById exception {}", JSON.toJSONString(deviceInfo), e);
            result.toFail("系统异常");
        }
        return result;
    }
}
