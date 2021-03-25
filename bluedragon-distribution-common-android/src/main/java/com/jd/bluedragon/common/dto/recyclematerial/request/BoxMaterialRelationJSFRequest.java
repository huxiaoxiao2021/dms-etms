package com.jd.bluedragon.common.dto.recyclematerial.request;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;
import java.util.List;

public class BoxMaterialRelationJSFRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户
     */
    private User user;

    /**
     * 站点
     */
    private CurrentOperate currentOperate;

    /**
     *  箱号
     */
    private String boxCode;

    /**
     * 集包袋编号
     */
    private String materialCode;

    /**
     * 操作类型 1 绑定 2 解绑
     */
    private int bindFlag;

    /**
     * 是否查询一组箱号绑定情况
     * true 查询  false 不查询
     */
    private boolean groupSearch;

    public boolean getGroupSearch() {
        return groupSearch;
    }

    public void setGroupSearch(boolean groupSearch) {
        this.groupSearch = groupSearch;
    }

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

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }

    public int getBindFlag() {
        return bindFlag;
    }

    public void setBindFlag(int bindFlag) {
        this.bindFlag = bindFlag;
    }
}
