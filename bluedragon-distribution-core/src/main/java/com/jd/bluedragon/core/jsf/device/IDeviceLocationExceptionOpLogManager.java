package com.jd.bluedragon.core.jsf.device;

import com.jd.bluedragon.sdk.modules.device.domain.DeviceLocationExceptionOpLog;
import com.jd.bluedragon.sdk.modules.device.dto.DeviceLocationExceptionOpLogQo;
import com.jd.bluedragon.sdk.modules.device.dto.DeviceLocationExceptionOpLogVo;
import com.jd.dms.java.utils.sdk.base.PageData;
import com.jd.dms.java.utils.sdk.base.Result;

/**
 * 设备异常操作位置记录
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2022-12-05 00:53:57 周一
 */
public interface IDeviceLocationExceptionOpLogManager {

    /**
     * 查询统计设备位置总数
     * @param deviceLocationExceptionOpLogQo 查询入参
     * @return 处理结果
     */
    Result<Long> queryCount(DeviceLocationExceptionOpLogQo deviceLocationExceptionOpLogQo);

    /**
     * 分页查询设备位置记录
     * @param deviceLocationExceptionOpLogQo 查询入参
     * @return 处理结果
     */
    Result<PageData<DeviceLocationExceptionOpLogVo>> queryPageList(DeviceLocationExceptionOpLogQo deviceLocationExceptionOpLogQo);

    /**
     * 添加一条设备位置记录
     * @param deviceLocationExceptionOpLog 设备异常位置操作记录
     * @return 处理结果
     */
    Result<Long> add(DeviceLocationExceptionOpLog deviceLocationExceptionOpLog);

    /**
     * 根据ID更新
     * @param deviceLocationExceptionOpLog 设备位置异常记录
     * @return 处理结果
     */
    Result<Boolean> updateById(DeviceLocationExceptionOpLog deviceLocationExceptionOpLog);

    /**
     * 根据ID逻辑删除
     * @param deviceLocationExceptionOpLog 设备位置异常记录
     * @return 处理结果
     */
    Result<Boolean> logicDeleteById(DeviceLocationExceptionOpLog deviceLocationExceptionOpLog);

}
