package com.jd.bluedragon.distribution.external.gateway.dto.request;

import java.io.Serializable;

/**
 * 取消分拣
 */
public class SortingCancelRequest implements Serializable {

    private static final long serialVersionUID = -1L;

    private User user;
    private CurrentOperate currentOperate;
    private String packageCode;
    private int businessType;

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

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public int getBusinessType() {
        return businessType;
    }

    public void setBusinessType(int businessType) {
        this.businessType = businessType;
    }


}
