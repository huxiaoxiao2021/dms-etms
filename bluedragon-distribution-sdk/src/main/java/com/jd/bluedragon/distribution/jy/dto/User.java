package com.jd.bluedragon.distribution.jy.dto;

import java.io.Serializable;

/**
 * 操作用户信息
 */
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

    private String userErp;

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

    public String getUserErp() {
        return userErp;
    }

    public void setUserErp(String userErp) {
        this.userErp = userErp;
    }
}
