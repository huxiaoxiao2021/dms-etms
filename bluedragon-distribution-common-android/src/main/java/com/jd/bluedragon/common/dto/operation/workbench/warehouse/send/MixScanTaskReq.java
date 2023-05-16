package com.jd.bluedragon.common.dto.operation.workbench.warehouse.send;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;
import java.util.List;

public class MixScanTaskReq extends BaseReq implements Serializable {

    private static final long serialVersionUID = -5809332610524693231L;

    /**
     * 混扫任务编码
     */
    private String templateCode;

    /**
     * 派车任务明细bizid
     */
    private List<String> bizIdList;

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public List<String> getBizIdList() {
        return bizIdList;
    }

    public void setBizIdList(List<String> bizIdList) {
        this.bizIdList = bizIdList;
    }
}
