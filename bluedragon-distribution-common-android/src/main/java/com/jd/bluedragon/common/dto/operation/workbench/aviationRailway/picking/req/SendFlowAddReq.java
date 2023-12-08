package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.req;

import com.jd.bluedragon.common.dto.base.request.BaseReq;
import com.jd.bluedragon.common.dto.basedata.response.StreamlinedBasicSite;

import java.io.Serializable;
import java.util.List;

public class SendFlowAddReq extends BaseReq implements Serializable {
    private static final long serialVersionUID = 7829104524461334388L;

    /**
     * 流向目的地列表
     */
    private List<StreamlinedBasicSite> siteList;

    /**
     * 提货任务类型
     * @see com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums.PickingGoodTaskTypeEnum
     */
    private Integer taskType;

    public List<StreamlinedBasicSite> getSiteList() {
        return siteList;
    }

    public void setSiteList(List<StreamlinedBasicSite> siteList) {
        this.siteList = siteList;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }
}
