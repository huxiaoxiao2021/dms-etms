package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.req;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;

/**
 * @Author zhengchengfa
 * @Date 2023/8/4 16:15
 * @Description
 */
public class TransportCodeQueryReq extends BaseReq implements Serializable {

    private static final long serialVersionUID = 4784612639942744950L;

    private String bizId;

    private String detailBizId;

    private Integer nextSiteCode;

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getDetailBizId() {
        return detailBizId;
    }

    public void setDetailBizId(String detailBizId) {
        this.detailBizId = detailBizId;
    }

    public Integer getNextSiteCode() {
        return nextSiteCode;
    }

    public void setNextSiteCode(Integer nextSiteCode) {
        this.nextSiteCode = nextSiteCode;
    }
}
