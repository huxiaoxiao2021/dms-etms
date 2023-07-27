package com.jd.bluedragon.common.dto.operation.workbench.warehouse.send;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;
import java.util.List;

/**
 * @author liwenji
 * @description 创建混扫任务
 * @date 2023-05-17 18:30
 */
public class CreateMixScanTaskReq extends BaseReq implements Serializable {

    /**
     * 混扫任务名称：【混扫01】
     */
    private String templateName;

    /**
     * 流向详情
     */
    private List<MixScanTaskDetailDto> sendFlowList;

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public List<MixScanTaskDetailDto> getSendFlowList() {
        return sendFlowList;
    }

    public void setSendFlowList(List<MixScanTaskDetailDto> sendFlowList) {
        this.sendFlowList = sendFlowList;
    }
}
