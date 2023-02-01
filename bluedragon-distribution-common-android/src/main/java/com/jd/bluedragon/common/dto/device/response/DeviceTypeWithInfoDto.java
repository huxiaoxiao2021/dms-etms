package com.jd.bluedragon.common.dto.device.response;

import java.io.Serializable;
import java.util.List;
/**
 * 设备类型返回实体
 * @author wuyoude
 *
 */
public class DeviceTypeWithInfoDto implements Serializable {

	private static final long serialVersionUID = -535865596164490703L;

    /**
     * 设备类型编号
     */
    private String deviceTypeCode;

    /**
     * 设备类型名称
     */
    private String deviceTypeName;
    /**
     * 设备列表
     */
    private List<DeviceInfoDto> deviceList;
    
	public String getDeviceTypeCode() {
		return deviceTypeCode;
	}
	public void setDeviceTypeCode(String deviceTypeCode) {
		this.deviceTypeCode = deviceTypeCode;
	}
	public String getDeviceTypeName() {
		return deviceTypeName;
	}
	public void setDeviceTypeName(String deviceTypeName) {
		this.deviceTypeName = deviceTypeName;
	}
	public List<DeviceInfoDto> getDeviceList() {
		return deviceList;
	}
	public void setDeviceList(List<DeviceInfoDto> deviceList) {
		this.deviceList = deviceList;
	}
    
}
