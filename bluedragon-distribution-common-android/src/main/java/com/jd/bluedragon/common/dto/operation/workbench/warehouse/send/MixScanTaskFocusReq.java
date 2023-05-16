package com.jd.bluedragon.common.dto.operation.workbench.warehouse.send;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;

/**
 * @author liwenji
 * @description 
 * @date 2023-05-16 18:19
 */
public class MixScanTaskFocusReq extends BaseReq implements Serializable {
    /**
     * 混扫任务编号
     */
    private String templateCode;

    /**
     * 派车明细任务
     */
    private String transWorkItemCode;

    /**
     * 目的地站点
     */
    private Integer endSiteId;

    /**
     * 0: 关注 1: 不关注
     */
    private Integer focus;

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public String getTransWorkItemCode() {
        return transWorkItemCode;
    }

    public void setTransWorkItemCode(String transWorkItemCode) {
        this.transWorkItemCode = transWorkItemCode;
    }

    public Integer getEndSiteId() {
        return endSiteId;
    }

    public void setEndSiteId(Integer endSiteId) {
        this.endSiteId = endSiteId;
    }

    public Integer getFocus() {
        return focus;
    }

    public void setFocus(Integer focus) {
        this.focus = focus;
    }
}
