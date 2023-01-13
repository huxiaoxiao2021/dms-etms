package com.jd.bluedragon.distribution.jy.task;

import java.io.Serializable;
import java.util.Date;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2022/4/5
 * @Description:
 */
public class JyBizTaskUnloadDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 调度任务ID
     */
    private String taskId;

    /**
     * 业务主键 = 封车编码
     */
    private String bizId;
    /**
     * 封车编码
     */
    private String sealCarCode;

    /**
     * 车牌号
     */
    private String vehicleNumber;

    /**
     * 是否无任务卸车；1：是：0：否
     */
    private Integer manualCreatedFlag;

    /**
     * 操作人ERP
     */
    private String operateUserErp;
    /**
     * 操作人姓名
     */
    private String operateUserName;
    /**
     * 操作人场地编码
     */
    private Integer operateSiteId;
    /**
     * 操作人场地名称
     */
    private String operateSiteName;
    /**
     * 操作时间
     */
    private Date operateTime;

    /**
     * 人员组编码
     */
    private String groupCode;

    /**
     * 任务状态；0-等待初始，1-在途，2-待解，3-待卸，4-卸车，5-卸车完成，6-取消
     */
    private Integer vehicleStatus;

    /**
     * 任务类型
     */
    private Integer taskType;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
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

    public Integer getManualCreatedFlag() {
        return manualCreatedFlag;
    }

    public void setManualCreatedFlag(Integer manualCreatedFlag) {
        this.manualCreatedFlag = manualCreatedFlag;
    }

    public String getOperateUserErp() {
        return operateUserErp;
    }

    public void setOperateUserErp(String operateUserErp) {
        this.operateUserErp = operateUserErp;
    }

    public String getOperateUserName() {
        return operateUserName;
    }

    public void setOperateUserName(String operateUserName) {
        this.operateUserName = operateUserName;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

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

    public Integer getVehicleStatus() {
        return vehicleStatus;
    }

    public void setVehicleStatus(Integer vehicleStatus) {
        this.vehicleStatus = vehicleStatus;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }
}
