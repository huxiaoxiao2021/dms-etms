package com.jd.bluedragon.common.dto.send.request;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;
import java.io.Serializable;

/**
 * 快运发货-按板号发货入参对象
 */
public class BoardCodeSendRequest implements Serializable {

    /**
     * 用户
     */
    private User user;

    /**
     * 当前站点
     */
    private CurrentOperate currentOperate;

    /** 收货单位编号 */
    private Integer receiveSiteCode;

    /** 板号 */
    private String boardCode;

    /** 批次号 */
    private String sendCode;

    /** 分拣业务类型 '10' 正向 '20' 逆向 '30' 三方 '40' POP */
    private Integer businessType;

    /**
     * 是否强制发货
     */
    private boolean forceSend;

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

    public Integer getReceiveSiteCode() {
        return receiveSiteCode;
    }

    public void setReceiveSiteCode(Integer receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }

    public String getBoardCode() {
        return boardCode;
    }

    public void setBoardCode(String boardCode) {
        this.boardCode = boardCode;
    }

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }

    public Integer getBusinessType() {
        return businessType;
    }

    public void setBusinessType(Integer businessType) {
        this.businessType = businessType;
    }

    public boolean getForceSend() {
        return forceSend;
    }

    public void setForceSend(boolean forceSend) {
        this.forceSend = forceSend;
    }
}
