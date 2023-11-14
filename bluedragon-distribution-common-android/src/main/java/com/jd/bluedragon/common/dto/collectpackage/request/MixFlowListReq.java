package com.jd.bluedragon.common.dto.collectpackage.request;


import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;

public class MixFlowListReq extends BaseReq implements Serializable {

    private String bizId;

    private String boxCode;

    /**
     * 检索条件 目的地Id或目的地名称检索（精确查询）
     */
    private String searchCondition;

    public String getSearchCondition() {
        return searchCondition;
    }

    public void setSearchCondition(String searchCondition) {
        this.searchCondition = searchCondition;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

}
