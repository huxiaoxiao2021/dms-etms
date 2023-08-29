package com.jd.bluedragon.distribution.workStation.domain;

import java.io.Serializable;

public class UserNameAndPhone implements Serializable {

    private static final long serialVersionUID = -5082148340929540785L;

    private String UserName;

    private String phone;

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
