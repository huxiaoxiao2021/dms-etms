package com.jd.bluedragon.distribution.consumable.domain;


import com.jd.bluedragon.distribution.jy.dto.User;

import java.io.Serializable;

/**
 * 包装确认请求
 */
public class WaybillConsumablePackConfirmRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 业务单号： 运单号或包裹号
     */
    private String businessCode;

    private User user;

    /**
     * 确认体积
     */
    private Double confirmVolume;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getBusinessCode() {
        return businessCode;
    }

    public void setBusinessCode(String businessCode) {
        this.businessCode = businessCode;
    }

    public Double getConfirmVolume() {
        return confirmVolume;
    }

    public void setConfirmVolume(Double confirmVolume) {
        this.confirmVolume = confirmVolume;
    }
}
