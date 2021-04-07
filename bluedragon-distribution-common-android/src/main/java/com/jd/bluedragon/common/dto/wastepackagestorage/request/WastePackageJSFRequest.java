package com.jd.bluedragon.common.dto.wastepackagestorage.request;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;

public class WastePackageJSFRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户
     */
    private User user;

    /**
     * 站点
     */
    private CurrentOperate currentOperate;

    /**
     *  运单号
     */
    private String waybillCode;

    /**
     * 状态  0 弃件暂存 1 弃件出库 2 已认领
     */
    private int status;

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

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
