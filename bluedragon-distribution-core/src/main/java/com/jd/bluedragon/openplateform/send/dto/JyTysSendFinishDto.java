package com.jd.bluedragon.openplateform.send.dto;

import com.jd.bluedragon.distribution.board.domain.User;
import com.jd.bluedragon.distribution.jy.dto.CurrentOperate;

import java.io.Serializable;
import java.util.Date;

/**
 * 装货完成发货请求入参
 */
public class JyTysSendFinishDto implements Serializable  {
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
     * 当前用户信息
     */
    private User user;

    /**
     * 任务下所有包裹发送完成时间
     */
    private Long packageSendCompleteTime;

    /**
     * 任务结束时间
     */
    private Date taskFinishTime;

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getPackageSendCompleteTime() {
        return packageSendCompleteTime;
    }

    public void setPackageSendCompleteTime(Long packageSendCompleteTime) {
        this.packageSendCompleteTime = packageSendCompleteTime;
    }

    public Date getTaskFinishTime() {
        return taskFinishTime;
    }

    public void setTaskFinishTime(Date taskFinishTime) {
        this.taskFinishTime = taskFinishTime;
    }
}
