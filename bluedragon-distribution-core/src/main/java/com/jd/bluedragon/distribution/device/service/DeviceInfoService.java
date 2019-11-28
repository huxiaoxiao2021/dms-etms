package com.jd.bluedragon.distribution.device.service;

import com.jd.bluedragon.distribution.device.dto.DeviceInfoDto;

import java.util.List;

/**
 * <p>
 *     自动化设备查询信息
 *
 * @author wuzuxiang
 * @since 2019/11/27
 **/
public interface DeviceInfoService {

    /**
     * 根据设备创建站点和设备类型查询设备信息
     * @param siteCode 创建站点
     * @param deviceType 设备类型
     * @return 返回满足条件的设备列表
     */
    List<DeviceInfoDto> queryDeviceConfigByTypeAndSiteCode(String siteCode, String deviceType);
}
