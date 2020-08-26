package com.jd.bluedragon.common.dto.waybill.request;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;

/**
 * @ClassName ThirdWaybillReq
 * @Description
 * @Author wyh
 * @Date 2020/7/22 21:23
 **/
public class ThirdWaybillReq implements Serializable {

    private static final long serialVersionUID = 8903457143394317165L;

    private User user;

    private CurrentOperate currentOperate;

    /**
     * 三方运单号
     */
    private String thirdWaybillCode;

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

    public String getThirdWaybillCode() {
        return thirdWaybillCode;
    }

    public void setThirdWaybillCode(String thirdWaybillCode) {
        this.thirdWaybillCode = thirdWaybillCode;
    }
}
