package com.jd.bluedragon.common.dto.base.request;


import java.io.Serializable;
import java.util.UUID;

public class BaseReq implements Serializable {

    private static final long serialVersionUID = 301807412701085234L;
    private CurrentOperate currentOperate;
    private User user;
    private String groupCode;
    private String requestId = UUID.randomUUID().toString().replace("-","");

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

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

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

}
