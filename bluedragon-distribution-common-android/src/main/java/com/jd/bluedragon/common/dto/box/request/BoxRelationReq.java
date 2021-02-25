package com.jd.bluedragon.common.dto.box.request;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;

/**
 * @ClassName BoxRelationReq
 * @Description
 * @Author wyh
 * @Date 2020/12/22 21:07
 **/
public class BoxRelationReq implements Serializable {

    private static final long serialVersionUID = -5238410054748760234L;

    private User user;

    private CurrentOperate currentOperate;

    /**
     * 箱号
     */
    private String boxCode;

    /**
     * 关联箱号
     */
    private String relationBoxCode;

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

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getRelationBoxCode() {
        return relationBoxCode;
    }

    public void setRelationBoxCode(String relationBoxCode) {
        this.relationBoxCode = relationBoxCode;
    }
}
