package com.jd.bluedragon.common.dto.operation.workbench.warehouse.send;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;

public class MixScanTaskQueryReq extends BaseReq implements Serializable {

    private static final long serialVersionUID = -5809332610524693231L;
    /**
     * 混扫任务编码
     */
    private String templateCode;

    /**
     * 查询条件 包裹号|箱号|目的地ID|滑道笼车号
     */
    private String barCode;

    private Integer pageNo;
    private Integer pageSize;

    public String getBarCode() {
        return barCode;
    }
    public String getTemplateCode() {
        return templateCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
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
    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }
}
