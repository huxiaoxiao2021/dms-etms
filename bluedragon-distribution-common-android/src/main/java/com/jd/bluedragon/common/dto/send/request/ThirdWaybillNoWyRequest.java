package com.jd.bluedragon.common.dto.send.request;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;

public class ThirdWaybillNoWyRequest implements Serializable {

    private static final long serialVersionUID = -7339348136491469940L;

    private String thirdWaybill;

    private User user;

    /**
     * 当前站点
     */
    private CurrentOperate currentOperate;

    public String getThirdWaybill() {
        return thirdWaybill;
    }

    public void setThirdWaybill(String thirdWaybill) {
        this.thirdWaybill = thirdWaybill;
    }

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

    @Override
    public String toString() {
        return "ThirdWaybillNoAndroidRequest{" +
                "thirdWaybill='" + thirdWaybill + '\'' +
                ", user=" + user +
                ", currentOperate=" + currentOperate +
                '}';
    }
}
