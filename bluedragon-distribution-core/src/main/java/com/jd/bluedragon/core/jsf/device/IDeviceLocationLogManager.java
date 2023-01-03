package com.jd.bluedragon.core.jsf.device;

import com.jd.bluedragon.sdk.modules.device.domain.DeviceLocationLog;
import com.jd.bluedragon.sdk.modules.device.dto.DeviceLocationLogQo;
import com.jd.bluedragon.sdk.modules.device.dto.DeviceLocationLogVo;
import com.jd.dms.java.utils.sdk.base.PageData;
import com.jd.dms.java.utils.sdk.base.Result;

/**
 * 设备位置记录
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2022-12-05 00:53:57 周一
 */
public interface IDeviceLocationLogManager {

    /**
     * 查询统计设备位置记录个数
     * @param deviceLocationLogQo 查询入参
     * @return 处理结果
     */
    Result<Long> queryCount(DeviceLocationLogQo deviceLocationLogQo);

    /**
     * 分页查询设备位置记录
     * @param deviceLocationLogQo 查询入参
     * @return 处理结果
     */
    Result<PageData<DeviceLocationLogVo>> queryPageList(DeviceLocationLogQo deviceLocationLogQo);

    /**
     * 添加一条设备位置记录
     * @param deviceLocationLog 设备位置记录
     * @return 处理结果
     */
    Result<Long> add(DeviceLocationLog deviceLocationLog);

    /**
     * 根据ID更新
     * @param deviceLocationLog 设备位置记录
     * @return 处理结果
     */
    Result<Boolean> updateById(DeviceLocationLog deviceLocationLog);

    /**
     * 根据ID逻辑删除
     * @param deviceLocationLog 设备位置记录
     * @return 处理结果
     */
    Result<Boolean> logicDeleteById(DeviceLocationLog deviceLocationLog);

}
