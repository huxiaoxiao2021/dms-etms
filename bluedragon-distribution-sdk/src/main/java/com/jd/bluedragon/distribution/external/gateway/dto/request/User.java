package com.jd.bluedragon.distribution.external.gateway.dto.request;

public class User {
    /*
    操作人编号
     */
    private int userCode;
    /*
    操作人姓名
     */
    private String userName;

    public int getUserCode() {
        return userCode;
    }

    public void setUserCode(int userCode) {
        this.userCode = userCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public User(int userCode, String userName) {
        this.userCode = userCode;
        this.userName = userName;
    }
}
