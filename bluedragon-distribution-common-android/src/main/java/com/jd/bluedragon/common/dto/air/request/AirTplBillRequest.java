package com.jd.bluedragon.common.dto.air.request;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;

/**
 * 查询主运单详情 参数
 * @author : xumigen
 * @date : 2019/11/4
 */
public class AirTplBillRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private CurrentOperate currentOperate;
    private User user;

    /**
     * 主运单号
     */
    private String billCode;

    public CurrentOperate getCurrentOperate() {
        return currentOperate;
    }

    public void setCurrentOperate(CurrentOperate currentOperate) {
        this.currentOperate = currentOperate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getBillCode() {
        return billCode;
    }

    public void setBillCode(String billCode) {
        this.billCode = billCode;
    }
}
