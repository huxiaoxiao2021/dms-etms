package com.jd.bluedragon.common.dto.operation.workbench.warehouse.send;

import java.io.Serializable;
import java.util.List;

/**
 * @author liwenji
 * @description
 * @date 2023-05-16 14:30
 */
public class MixScanTaskFlowDetailRes implements Serializable {

    private static final long serialVersionUID = 4784612639942744950L;
    
    /**
     * 流向详情
     */
    private List<MixScanTaskFlowAgg> mixScanTaskFlowDtoList;

    public List<MixScanTaskFlowAgg> getMixScanTaskFlowDtoList() {
        return mixScanTaskFlowDtoList;
    }

    public void setMixScanTaskFlowDtoList(List<MixScanTaskFlowAgg> mixScanTaskFlowDtoList) {
        this.mixScanTaskFlowDtoList = mixScanTaskFlowDtoList;
    }
}
