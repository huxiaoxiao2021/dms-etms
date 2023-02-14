package com.jd.bluedragon.common.dto.comboard.request;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;

public class TableTrolleyReq extends BaseReq implements Serializable {
    private static final long serialVersionUID = 2913336393003471073L;
    /**
     * 滑道编号
     */
    private String crossCode;
    private boolean needStatistics;
    private Integer pageNo;
    private Integer pageSize;

    /**
     * 是否需要展示某个混扫任务的流向
     */
    private boolean needMatchGroupCTT;
    /**
     * 混扫任务编号
     */
    private String templateCode;


    /**
     * 是否需要加载流向的统计数据(拦截、组板数量等)
     */
    private boolean needSendFlowStatistics;

    public String getCrossCode() {
        return crossCode;
    }

    public void setCrossCode(String crossCode) {
        this.crossCode = crossCode;
    }

    public boolean isNeedStatistics() {
        return needStatistics;
    }

    public void setNeedStatistics(boolean needStatistics) {
        this.needStatistics = needStatistics;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public boolean getNeedMatchGroupCTT() {
        return needMatchGroupCTT;
    }

    public void setNeedMatchGroupCTT(boolean needMatchGroupCTT) {
        this.needMatchGroupCTT = needMatchGroupCTT;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public boolean getNeedSendFlowStatistics() {
        return needSendFlowStatistics;
    }

    public void setNeedSendFlowStatistics(boolean needSendFlowStatistics) {
        this.needSendFlowStatistics = needSendFlowStatistics;
    }
}
