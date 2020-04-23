package com.jd.bluedragon.common.dto.air.request;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;

/**
 * 获取运输配置数据 请求对象
 * @author : xumigen
 * @date : 2019/11/4
 */
public class TmsDictRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private CurrentOperate currentOperate;
    private User user;

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
}
