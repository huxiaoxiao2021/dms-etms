package com.jd.bluedragon.common.dto.goodsLoadingScanning.request;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

import java.util.Date;

/**
 * 装货完成发货请求入参
 */
public class GoodsLoadingReq {
    private static final long serialVersionUID = 1L;

    /**
     * 任务号
     */
    private Long taskId;

    /**
     * 发货单位编码
     */
    private Integer createSiteCode;

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

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Integer getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
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

}
