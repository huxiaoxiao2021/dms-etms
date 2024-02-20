package com.jd.bluedragon.distribution.jy.dto.pickinggood;

import java.io.Serializable;

/**
 * @Author zhengchengfa
 * @Date 2023/12/15 18:40
 * @Description
 */
public class PickingGoodTaskStatisticsDto  implements Serializable {

    private static final long serialVersionUID = 1L;

    //待提
    private Integer waitPickingTotalNum;
    //已提
    private Integer realPickingTotalNum;
    //多提
    private Integer morePickingTotalNum;

    //待提待发
    private Integer waitSendTotalNum;
    //已发
    private Integer realSendTotalNum;
    //多发
    private Integer moreSendTotalNum;
    //强发
    private Integer forceSendTotalNum;

    public Integer getWaitPickingTotalNum() {
        return waitPickingTotalNum;
    }

    public void setWaitPickingTotalNum(Integer waitPickingTotalNum) {
        this.waitPickingTotalNum = waitPickingTotalNum;
    }

    public Integer getRealPickingTotalNum() {
        return realPickingTotalNum;
    }

    public void setRealPickingTotalNum(Integer realPickingTotalNum) {
        this.realPickingTotalNum = realPickingTotalNum;
    }

    public Integer getMorePickingTotalNum() {
        return morePickingTotalNum;
    }

    public void setMorePickingTotalNum(Integer morePickingTotalNum) {
        this.morePickingTotalNum = morePickingTotalNum;
    }

    public Integer getWaitSendTotalNum() {
        return waitSendTotalNum;
    }

    public void setWaitSendTotalNum(Integer waitSendTotalNum) {
        this.waitSendTotalNum = waitSendTotalNum;
    }

    public Integer getRealSendTotalNum() {
        return realSendTotalNum;
    }

    public void setRealSendTotalNum(Integer realSendTotalNum) {
        this.realSendTotalNum = realSendTotalNum;
    }

    public Integer getMoreSendTotalNum() {
        return moreSendTotalNum;
    }

    public void setMoreSendTotalNum(Integer moreSendTotalNum) {
        this.moreSendTotalNum = moreSendTotalNum;
    }

    public Integer getForceSendTotalNum() {
        return forceSendTotalNum;
    }

    public void setForceSendTotalNum(Integer forceSendTotalNum) {
        this.forceSendTotalNum = forceSendTotalNum;
    }
}
