package com.jd.bluedragon.common.dto.base.request;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    /*
    操作人编号
     */
    private int userCode;
    /*
    操作人姓名
     */
    private String userName;

    public User() {
    }

    public User(int userCode, String userName) {
        this.userCode = userCode;
        this.userName = userName;
    }

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

}
