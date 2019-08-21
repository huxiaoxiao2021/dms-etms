package com.jd.bluedragon.common.dto.send.request;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;

/**
 * @author : xumigen
 * @date : 2019/7/20
 */
public class SinglePackageSendRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户
     */
    private User user;

    /**
     * 当前站点
     */
    private CurrentOperate currentOperate;
    private String sendCode;
    private String boxCode;
    private Integer businessType;
    private Integer receiveSiteCode;
    private String turnoverBoxCode;
    private boolean forceSend;
    private boolean cancelLastSend;
    private String bizSource;
    /*
    调用次数;前端有递归调用的场景
     */
    private Integer callTimes;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public CurrentOperate getCurrentOperate() {
        return currentOperate;
    }

    public void setCurrentOperate(CurrentOperate currentOperate) {
        this.currentOperate = currentOperate;
    }

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public Integer getBusinessType() {
        return businessType;
    }

    public void setBusinessType(Integer businessType) {
        this.businessType = businessType;
    }

    public Integer getReceiveSiteCode() {
        return receiveSiteCode;
    }

    public void setReceiveSiteCode(Integer receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }

    public String getTurnoverBoxCode() {
        return turnoverBoxCode;
    }

    public void setTurnoverBoxCode(String turnoverBoxCode) {
        this.turnoverBoxCode = turnoverBoxCode;
    }

    public boolean isForceSend() {
        return forceSend;
    }

    public void setForceSend(boolean forceSend) {
        this.forceSend = forceSend;
    }

    public boolean isCancelLastSend() {
        return cancelLastSend;
    }

    public void setCancelLastSend(boolean cancelLastSend) {
        this.cancelLastSend = cancelLastSend;
    }

    public String getBizSource() {
        return bizSource;
    }

    public void setBizSource(String bizSource) {
        this.bizSource = bizSource;
    }

    public Integer getCallTimes() {
        return callTimes;
    }

    public void setCallTimes(Integer callTimes) {
        this.callTimes = callTimes;
    }
}
