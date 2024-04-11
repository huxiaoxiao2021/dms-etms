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
     * 评价目标业务主键
     */
    private String targetBizId;

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

    /**
     * 是否校验评价超时开关
     */
    private Boolean checkOverTimeFlag;

    public String getSourceBizId() {
        return sourceBizId;
    }

    public void setSourceBizId(String sourceBizId) {
        this.sourceBizId = sourceBizId;
    }

    public String getTargetBizId() {
        return targetBizId;
    }

    public void setTargetBizId(String targetBizId) {
        this.targetBizId = targetBizId;
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

    public Boolean getCheckOverTimeFlag() {
        return checkOverTimeFlag;
    }

    public void setCheckOverTimeFlag(Boolean checkOverTimeFlag) {
        this.checkOverTimeFlag = checkOverTimeFlag;
    }
}
