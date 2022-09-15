package com.jd.bluedragon.distribution.jy.dto.unload;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author zhengchengfa
 * @Description //TODO
 * @date
 **/
public class UnloadChildTaskDto implements Serializable {

    private static final long serialVersionUID = 2419641078080000602L;

    private String bizId;
    /**
     * 父任务BizID
     */
    private String unloadVehicleBizId;
    /**
     * 子任务类型： 1 补扫任务 2...
      */
    private Integer type;
    /**
     * 子任务状态： 1 进行中 2完
     */
    private Integer status;
    /**
     * 子任务开始时间
     */
    private Date startTime;
    /**
     * 子任务结束时间
     */
    private Date endTime;

    private String createUserErp;
    private String createUserName;
    private String updateUserErp;
    private String updateUserName;
    private Date createTime;
    private Date updateTime;

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getUnloadVehicleBizId() {
        return unloadVehicleBizId;
    }

    public void setUnloadVehicleBizId(String unloadVehicleBizId) {
        this.unloadVehicleBizId = unloadVehicleBizId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getCreateUserErp() {
        return createUserErp;
    }

    public void setCreateUserErp(String createUserErp) {
        this.createUserErp = createUserErp;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public String getUpdateUserErp() {
        return updateUserErp;
    }

    public void setUpdateUserErp(String updateUserErp) {
        this.updateUserErp = updateUserErp;
    }

    public String getUpdateUserName() {
        return updateUserName;
    }

    public void setUpdateUserName(String updateUserName) {
        this.updateUserName = updateUserName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
