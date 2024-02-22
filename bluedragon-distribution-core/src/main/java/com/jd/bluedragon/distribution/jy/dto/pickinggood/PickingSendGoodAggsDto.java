package com.jd.bluedragon.distribution.jy.dto.pickinggood;

import java.io.Serializable;

/**
 * @Author zhengchengfa
 * @Date 2023/12/14 15:01
 * @Description
 */
public class PickingSendGoodAggsDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String bizId;

    /**
     * 待提总数
     */
    private int waitSendTotalNum;
    /**
     * 已提总数
     */
    private int realSendTotalNum;
    /**
     * 多提总数
     */
    private int moreSendTotalNum;
    /**
     * 强发
     */
    private int forceSendTotalNum;

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public int getWaitSendTotalNum() {
        return waitSendTotalNum;
    }

    public void setWaitSendTotalNum(int waitSendTotalNum) {
        this.waitSendTotalNum = waitSendTotalNum;
    }

    public int getRealSendTotalNum() {
        return realSendTotalNum;
    }

    public void setRealSendTotalNum(int realSendTotalNum) {
        this.realSendTotalNum = realSendTotalNum;
    }

    public int getMoreSendTotalNum() {
        return moreSendTotalNum;
    }

    public void setMoreSendTotalNum(int moreSendTotalNum) {
        this.moreSendTotalNum = moreSendTotalNum;
    }

    public int getForceSendTotalNum() {
        return forceSendTotalNum;
    }

    public void setForceSendTotalNum(int forceSendTotalNum) {
        this.forceSendTotalNum = forceSendTotalNum;
    }
}
