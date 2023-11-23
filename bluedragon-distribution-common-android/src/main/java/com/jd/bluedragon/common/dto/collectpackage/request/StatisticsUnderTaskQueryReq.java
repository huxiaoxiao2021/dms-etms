package com.jd.bluedragon.common.dto.collectpackage.request;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;

public class StatisticsUnderTaskQueryReq extends BaseReq implements Serializable {
    private static final long serialVersionUID = 7592395440064409355L;

    /**
     * 箱号
     */
    private String boxCode;
    /**
     * 任务bizId
     */
    private String bizId;
    /**
     * 类型
     */
    private Integer type;

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
