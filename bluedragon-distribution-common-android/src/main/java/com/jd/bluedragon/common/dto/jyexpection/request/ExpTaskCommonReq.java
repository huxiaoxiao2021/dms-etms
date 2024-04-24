package com.jd.bluedragon.common.dto.jyexpection.request;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;

/**
 * 异常任务公共入参
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2024-01-17 18:02:37 周三
 */
public class ExpTaskCommonReq implements Serializable {
    private static final long serialVersionUID = -8559039237997249961L;

    private CurrentOperate currentOperate;

    private User user;

    private String bizId;

    public ExpTaskCommonReq() {
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

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }
}
