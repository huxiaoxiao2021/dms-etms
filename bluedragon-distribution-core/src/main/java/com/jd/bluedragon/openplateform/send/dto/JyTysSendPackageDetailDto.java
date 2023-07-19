package com.jd.bluedragon.openplateform.send.dto;

import com.jd.bluedragon.distribution.jy.dto.CurrentOperate;
import com.jd.bluedragon.distribution.jy.dto.User;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zhengchengfa
 * @time 2021-01-22 0:20
 * 装车发货包裹信息
 */
public class JyTysSendPackageDetailDto implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 任务号
     */
    private Long taskId;

    /**
     * 发货交接单号-发货批次号
     */
    private String sendCode;

    /**
     * 收货单位编码
     */
    private Integer receiveSiteCode;

    /**
     * 当前分拣中心
     */
    private CurrentOperate currentOperate;

    /**
     * 包裹号
     */
    private String packageCode;

    /**
     * 包裹更新时间
     */
    private Date updateTime;

    /*
     * 操作人编号
     */
    private int userCode;
    /*
     * 操作人姓名
     */
    private String userName;

    /**
     * 当前用户信息
     */
    private User user;

    /**
     * 任务结束时间
     */
    private Date taskFinishTime;

    /**
     * 操作场地编码
     */
    private Integer createSiteCode;

    /* ---------get  set-------------------------- */

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }

    public Integer getReceiveSiteCode() {
        return receiveSiteCode;
    }

    public void setReceiveSiteCode(Integer receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }

    public CurrentOperate getCurrentOperate() {
        return currentOperate;
    }

    public void setCurrentOperate(CurrentOperate currentOperate) {
        this.currentOperate = currentOperate;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public int getUserCode() {
        return userCode;
    }

    public void setUserCode(int userCode) {
        this.userCode = userCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getTaskFinishTime() {
        return taskFinishTime;
    }

    public void setTaskFinishTime(Date taskFinishTime) {
        this.taskFinishTime = taskFinishTime;
    }

    public Integer getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
    }
}
