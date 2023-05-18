package com.jd.bluedragon.common.dto.operation.workbench.warehouse.send;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;
import java.util.List;

/**
 * @author liwenji
 * @description 
 * @date 2023-05-16 16:29
 */
public class AppendMixScanTaskFlowReq extends BaseReq implements Serializable {

    /**
     * 混扫任务名称：【混扫01】
     */
    private String templateCode;

    /**
     * 混扫任务名称
     */
    private String templateName;

    /**
     * 流向详情
     */
    private List<MixScanTaskDetailDto> sendFlowList;

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public List<MixScanTaskDetailDto> getSendFlowList() {
        return sendFlowList;
    }

    public void setSendFlowList(List<MixScanTaskDetailDto> sendFlowList) {
        this.sendFlowList = sendFlowList;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }
}
