package com.jd.bluedragon.distribution.device.service;


import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.device.response.DeviceInfoDto;
import com.jd.bluedragon.common.dto.device.response.DeviceTypeWithInfoDto;
import com.jd.bluedragon.distribution.api.request.DeviceInfoRequest;
import com.jd.bluedragon.distribution.command.JdResult;

import java.util.List;

/**
 *     设备信息 处理逻辑
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

    /**
     * 上传设备信息，返回信任token
     */
    JdResult<String> deviceInfoUpload(DeviceInfoRequest request);
    /**
     * 根据站点查询设备信息
     * @param siteCode 站点
     * @return 返回满足条件的设备列表
     */
	JdCResponse<List<DeviceTypeWithInfoDto>> queryDeviceTypeWithInfoList(DeviceInfoDto deviceInfoDto);
	/**
	 * 根据设备编码和场地查询设备配置信息
	 * @param machineCode
	 * @param siteCode
	 * @return
	 */
	JdResult<DeviceInfoDto> queryDeviceConfigByMachineCode(String machineCode,Integer siteCode);
}
