package com.jd.bluedragon.common.dto.operation.workbench.warehouse.send;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;
import java.util.List;

/**
 * 追加混扫任务列表查询
 */
public class AppendSendVehicleTaskQueryRes extends BaseReq implements Serializable {

    private static final long serialVersionUID = -5809332610524693231L;

    /**
     * 混扫任务内最大流向数量
     */
    private Integer mixScanTaskSiteFlowMaxNum;
    /**
     * 发货任务（待发货、发货中）
     */
    private List<SendVehicleDto> sendVehicleDtoList;


    public Integer getMixScanTaskSiteFlowMaxNum() {
        return mixScanTaskSiteFlowMaxNum;
    }

    public void setMixScanTaskSiteFlowMaxNum(Integer mixScanTaskSiteFlowMaxNum) {
        this.mixScanTaskSiteFlowMaxNum = mixScanTaskSiteFlowMaxNum;
    }

    public List<SendVehicleDto> getSendVehicleDtoList() {
        return sendVehicleDtoList;
    }

    public void setSendVehicleDtoList(List<SendVehicleDto> sendVehicleDtoList) {
        this.sendVehicleDtoList = sendVehicleDtoList;
    }
}
