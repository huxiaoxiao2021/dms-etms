package com.jd.bluedragon.distribution.video.domain;

import java.io.Serializable;
import java.util.Date;

public class UserAndTime implements Serializable {
    private static final long serialVersionUID = 8964412561179001967L;
    private Integer userId;
    private String userName;
    private Date operateTime;

    public UserAndTime() {
    }

    public UserAndTime(Integer userId, String userName, Date operateTime) {
        this.userId = userId;
        this.userName = userName;
        this.operateTime = operateTime;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }
}
