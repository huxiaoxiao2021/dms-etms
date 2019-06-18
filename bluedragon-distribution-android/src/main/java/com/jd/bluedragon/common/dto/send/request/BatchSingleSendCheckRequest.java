package com.jd.bluedragon.common.dto.send.request;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;
import java.util.List;

/**
 * BatchSingleSendCheckRequest
 * 批量一车一单发货校验请求 *
 * @author jiaowenqiang
 * @date 2019/6/14
 */
public class BatchSingleSendCheckRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 用户
     */
    private User user;

    /**
     * 当前站点
     */
    private CurrentOperate currentOperate;

    /**
     * 批量号集合
     */
    private List<String> batchCodeList;

    /**
     * 包裹号或箱号
     */
    private String packageOrBoxCode;

    /**
     * 操作类型（2种）
     * 1：按路由配置发货
     * 2：按龙门架配置发货
     */
    private int operateType;

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

    public List<String> getBatchCodeList() {
        return batchCodeList;
    }

    public void setBatchCodeList(List<String> batchCodeList) {
        this.batchCodeList = batchCodeList;
    }

    public String getPackageOrBoxCode() {
        return packageOrBoxCode;
    }

    public void setPackageOrBoxCode(String packageOrBoxCode) {
        this.packageOrBoxCode = packageOrBoxCode;
    }

    public int getOperateType() {
        return operateType;
    }

    public void setOperateType(int operateType) {
        this.operateType = operateType;
    }
}
