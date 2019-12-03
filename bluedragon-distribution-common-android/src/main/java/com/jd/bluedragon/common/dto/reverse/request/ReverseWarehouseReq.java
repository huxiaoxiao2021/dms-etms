package com.jd.bluedragon.common.dto.reverse.request;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;

/**
 * @author : xumigen
 * @date : 2019/9/21
 */
public class ReverseWarehouseReq implements Serializable {
    private static final long serialVersionUID = -1L;


    /**
     * 用户
     */
    private User user;

    /**
     * 站点
     */
    private CurrentOperate currentOperate;

    /** 包裹号或运单号 */
    private String packageOrWaybillCode;

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

    public String getPackageOrWaybillCode() {
        return packageOrWaybillCode;
    }

    public void setPackageOrWaybillCode(String packageOrWaybillCode) {
        this.packageOrWaybillCode = packageOrWaybillCode;
    }
}
