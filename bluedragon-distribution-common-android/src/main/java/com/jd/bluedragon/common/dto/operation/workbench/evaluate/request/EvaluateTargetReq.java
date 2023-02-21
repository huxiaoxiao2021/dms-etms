package com.jd.bluedragon.common.dto.operation.workbench.evaluate.request;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;
import java.util.List;

public class EvaluateTargetReq implements Serializable {

    private static final long serialVersionUID = -2377545211064774385L;

    /**
     * 评价来源业务主键
     */
    private String sourceBizId;

    /**
     * 评价状态：1-满意，0-不满意
     */
    private Integer status;

    /**
     * 评价维度详情列表
     */
    private List<EvaluateDimensionReq> dimensionList;

    /**
     * 用户信息
     */
    private User user;

    /**
     * 操作信息
     */
    private CurrentOperate currentOperate;

    public String getSourceBizId() {
        return sourceBizId;
    }

    public void setSourceBizId(String sourceBizId) {
        this.sourceBizId = sourceBizId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<EvaluateDimensionReq> getDimensionList() {
        return dimensionList;
    }

    public void setDimensionList(List<EvaluateDimensionReq> dimensionList) {
        this.dimensionList = dimensionList;
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
}
