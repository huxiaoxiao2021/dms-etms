package com.jd.bluedragon.distribution.api.response;

import com.jd.bluedragon.distribution.api.JdResponse;

/**
 * @author tangchunqing
 * @Description: 类描述信息
 * @date 2018年11月30日 15时:25分
 */
public class ArAbnormalResponse extends JdResponse {
    private int sendCodeNum;
    private int boxCodeNum;
    private int waybillCodeNum;
    private int packageCodeNum;

    public int getSendCodeNum() {
        return sendCodeNum;
    }

    public void setSendCodeNum(int sendCodeNum) {
        this.sendCodeNum = sendCodeNum;
    }

    public int getBoxCodeNum() {
        return boxCodeNum;
    }

    public void setBoxCodeNum(int boxCodeNum) {
        this.boxCodeNum = boxCodeNum;
    }

    public int getWaybillCodeNum() {
        return waybillCodeNum;
    }

    public void setWaybillCodeNum(int waybillCodeNum) {
        this.waybillCodeNum = waybillCodeNum;
    }

    public int getPackageCodeNum() {
        return packageCodeNum;
    }

    public void setPackageCodeNum(int packageCodeNum) {
        this.packageCodeNum = packageCodeNum;
    }
}
