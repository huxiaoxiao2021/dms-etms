package com.jd.bluedragon.distribution.jy.dto.unload;

import java.io.Serializable;

/**
 * 无任务卸车创建结果
 */
public class UnloadNoTaskRespDto implements Serializable {

    private static final long serialVersionUID = 1L;

    // 操作场地ID
    private Integer operateSiteId;
    // 操作场地名称
    private String operateSiteName;
    // 业务主键
    private String bizId;
    // 任务编码
    private String taskId;
    // 封车编码
    private String sealCarCode;
    // 车牌号
    private String vehicleNumber;

    public Integer getOperateSiteId() {
        return operateSiteId;
    }

    public void setOperateSiteId(Integer operateSiteId) {
        this.operateSiteId = operateSiteId;
    }

    public String getOperateSiteName() {
        return operateSiteName;
    }

    public void setOperateSiteName(String operateSiteName) {
        this.operateSiteName = operateSiteName;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getSealCarCode() {
        return sealCarCode;
    }

    public void setSealCarCode(String sealCarCode) {
        this.sealCarCode = sealCarCode;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }
}
