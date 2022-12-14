package com.jd.bluedragon.distribution.api.request.client;

import com.jd.bluedragon.distribution.api.request.base.OperateUser;

import java.io.Serializable;

/**
 * 客户端位置信息 param object
 * @author fanggang7
 * @time 2022-11-10 15:50:29 周四
 */
public class DeviceLocationUploadPo implements Serializable {

    private static final long serialVersionUID = -54592365644078267L;

    /**
     * 设备信息
     */
    private DeviceInfo deviceInfo;

    /**
     * 设备位置信息
     */
    private DeviceLocationInfo deviceLocationInfo;

    /**
     * 操作人信息
     */
    private OperateUser operateUser;

    /**
     * 上传成功后的记录主键ID
     */
    private Long refLogId;

    /**
     * 操作类型
     */
    private Integer operateType;

    /**
     * 操作时间
     */
    private Long operateTime;

    public DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public DeviceLocationInfo getDeviceLocationInfo() {
        return deviceLocationInfo;
    }

    public void setDeviceLocationInfo(DeviceLocationInfo deviceLocationInfo) {
        this.deviceLocationInfo = deviceLocationInfo;
    }

    public OperateUser getOperateUser() {
        return operateUser;
    }

    public void setOperateUser(OperateUser operateUser) {
        this.operateUser = operateUser;
    }

    public Long getRefLogId() {
        return refLogId;
    }

    public void setRefLogId(Long refLogId) {
        this.refLogId = refLogId;
    }

    public Integer getOperateType() {
        return operateType;
    }

    public void setOperateType(Integer operateType) {
        this.operateType = operateType;
    }

    public Long getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Long operateTime) {
        this.operateTime = operateTime;
    }
}
