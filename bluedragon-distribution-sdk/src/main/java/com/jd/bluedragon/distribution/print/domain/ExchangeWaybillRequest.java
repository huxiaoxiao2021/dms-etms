package com.jd.bluedragon.distribution.print.domain;

import com.jd.bluedragon.distribution.jy.dto.CurrentOperate;
import com.jd.bluedragon.distribution.jy.dto.User;

import java.io.Serializable;

public class ExchangeWaybillRequest implements Serializable {

    private static final long serialVersionUID = -6355765431543262865L;

    /**
     * 运单号
     */
    private String waybillCode;

    /**
     * 请求唯一ID
     */
    private String traceId;

    /**
     * 操作人信息
     */
    private User user;

    /**
     * 操作站点信息
     */
    private CurrentOperate currentOperate;

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
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
}
