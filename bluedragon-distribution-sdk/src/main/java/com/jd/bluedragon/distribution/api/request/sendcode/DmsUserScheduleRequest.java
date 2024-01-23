package com.jd.bluedragon.distribution.api.request.sendcode;

import java.io.Serializable;

public class DmsUserScheduleRequest implements Serializable {
    private static final long serialVersionUID = 3390742740990387L;
    /**
     * erp/身份证
     */
    private String userCode;

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }
}
