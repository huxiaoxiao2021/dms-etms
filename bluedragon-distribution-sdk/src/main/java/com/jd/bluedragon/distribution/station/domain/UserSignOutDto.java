package com.jd.bluedragon.distribution.station.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * @author pengchong28
 * @description 用户自动签退对象
 * @date 2024/1/22
 */
public class UserSignOutDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 更新人姓名
     */
    private String updateUserName;

    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 修改人ERP
     */
    private String updateUser;
    /**
     * 自动签退时间，小时
     */
    private Integer hour;

    private Date signOutTime;

    public String getUpdateUserName() {
        return updateUserName;
    }

    public void setUpdateUserName(String updateUserName) {
        this.updateUserName = updateUserName;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public Integer getHour() {
        return hour;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    public Date getSignOutTime() {
        return signOutTime;
    }

    public void setSignOutTime(Date signOutTime) {
        this.signOutTime = signOutTime;
    }
}
