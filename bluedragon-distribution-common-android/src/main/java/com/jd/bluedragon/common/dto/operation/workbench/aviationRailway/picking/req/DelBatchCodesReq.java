package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.req;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;
import java.util.List;

/**
 * @Author zhengchengfa
 * @Date 2024/4/2 20:33
 * @Description
 */
public class DelBatchCodesReq extends BaseReq implements Serializable {

    private static final long serialVersionUID = -2083002886333203311L;

    /**
     * 任务类型
     * com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums.PickingGoodTaskTypeEnum
     */
    private Integer taskType;
    /**
     * 下一流向场地Id
     */
    private Integer nextSiteId;
    /**
     * 批次号
     */
    private List<String> sendCodeList;

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public Integer getNextSiteId() {
        return nextSiteId;
    }

    public void setNextSiteId(Integer nextSiteId) {
        this.nextSiteId = nextSiteId;
    }

    public List<String> getSendCodeList() {
        return sendCodeList;
    }

    public void setSendCodeList(List<String> sendCodeList) {
        this.sendCodeList = sendCodeList;
    }
}
