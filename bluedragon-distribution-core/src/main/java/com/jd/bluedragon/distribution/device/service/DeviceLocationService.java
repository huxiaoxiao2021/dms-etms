package com.jd.bluedragon.distribution.device.service;

import com.jd.bluedragon.distribution.api.request.client.DeviceLocationUploadPo;
import com.jd.dms.java.utils.sdk.base.Result;

/**
 * 设备位置网关服务
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2022-11-20 19:59:10 周日
 */
public interface DeviceLocationService {

    /**
     * 上传设备位置信息
     * @param deviceLocationUploadPo 位置信息
     * @return 上传结果
     * @author fanggang7
     * @time 2022-11-23 18:21:59 周三
     */
    Result<Boolean> uploadLocationInfo(DeviceLocationUploadPo deviceLocationUploadPo);

    /**
     * 检查用户位置是否与其所属站点匹配
     * @param deviceLocationUploadPo 上传信息
     * @return 匹配结果
     * @author fanggang7
     * @time 2022-11-23 18:21:59 周三
     */
    Result<Boolean> checkLocationMatchUserSite(DeviceLocationUploadPo deviceLocationUploadPo);

    /**
     * 上传异常位置操作数据
     * @param deviceLocationUploadPo 上传信息
     * @return 匹配结果
     * @author fanggang7
     * @time 2022-11-23 18:21:59 周三
     */
    Result<Boolean> uploadLocationExceptionOpInfo(DeviceLocationUploadPo deviceLocationUploadPo);

    /**
     * 设备位置上传消息处理
     * @param deviceLocationUploadPo 上传信息
     * @return 匹配结果
     * @author fanggang7
     * @time 2022-11-23 18:21:59 周三
     */
    Result<Boolean> handleDmsUploadDeviceLocationConsume(DeviceLocationUploadPo deviceLocationUploadPo);

    /**
     * 设备操作位置异常上传消息处理
     * @param deviceLocationUploadPo 上传信息
     * @return 匹配结果
     * @author fanggang7
     * @time 2022-11-23 18:21:59 周三
     */
    Result<Boolean> handleDmsUploadDeviceLocationExceptionOpConsume(DeviceLocationUploadPo deviceLocationUploadPo);
}
