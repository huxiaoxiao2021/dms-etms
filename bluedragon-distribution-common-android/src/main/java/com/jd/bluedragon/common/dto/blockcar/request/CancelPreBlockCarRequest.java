package com.jd.bluedragon.common.dto.blockcar.request;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;

/**
 * @author zhangzhongkai8
 * @date 2020/11/18  18:06
 */
public class CancelPreBlockCarRequest implements Serializable {

    /**
     * 用户
     */
    private User user;

    /**
     * 站点
     */
    private CurrentOperate currentOperate;

    /**
     * 车牌号
     */
    private String carNum;


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public CurrentOperate getCurrentOperate() {
        return currentOperate;
    }

    public void setCurrentOperate(CurrentOperate currentOperate) {
        this.currentOperate = currentOperate;
    }

    public String getCarNum() {
        return carNum;
    }

    public void setCarNum(String carNum) {
        this.carNum = carNum;
    }
}
