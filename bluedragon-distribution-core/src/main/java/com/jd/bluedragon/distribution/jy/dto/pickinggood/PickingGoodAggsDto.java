package com.jd.bluedragon.distribution.jy.dto.pickinggood;

import java.io.Serializable;

/**
 * @Author zhengchengfa
 * @Date 2023/12/14 15:01
 * @Description
 */
public class PickingGoodAggsDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 待提总数
     */
    private int waitPickingTotalNum;
    /**
     * 已提总数
     */
    private int realPickingTotalNum;
    /**
     * 多提总数
     */
    private int morePickingTotalNum;


    public int getWaitPickingTotalNum() {
        return waitPickingTotalNum;
    }

    public void setWaitPickingTotalNum(int waitPickingTotalNum) {
        this.waitPickingTotalNum = waitPickingTotalNum;
    }

    public int getRealPickingTotalNum() {
        return realPickingTotalNum;
    }

    public void setRealPickingTotalNum(int realPickingTotalNum) {
        this.realPickingTotalNum = realPickingTotalNum;
    }

    public int getMorePickingTotalNum() {
        return morePickingTotalNum;
    }

    public void setMorePickingTotalNum(int morePickingTotalNum) {
        this.morePickingTotalNum = morePickingTotalNum;
    }
}
