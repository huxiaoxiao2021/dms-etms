package com.jd.bluedragon.common.dto.base.request;


import java.io.Serializable;

public class BaseReq implements Serializable {

    private static final long serialVersionUID = 301807412701085234L;
    private CurrentOperate currentOperate;
    private User user;
    private String groupCode;

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

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }
}
