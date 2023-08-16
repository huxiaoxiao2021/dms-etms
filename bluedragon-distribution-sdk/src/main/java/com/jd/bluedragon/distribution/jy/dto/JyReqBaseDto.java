package com.jd.bluedragon.distribution.jy.dto;

import java.io.Serializable;
import java.util.UUID;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2022/8/17
 * @Description:
 */
public class JyReqBaseDto implements Serializable {

    private static final long serialVersionUID = -1L;

    private CurrentOperate currentOperate;

    private User user;

    private String requestId = UUID.randomUUID().toString().replace("-","");

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
