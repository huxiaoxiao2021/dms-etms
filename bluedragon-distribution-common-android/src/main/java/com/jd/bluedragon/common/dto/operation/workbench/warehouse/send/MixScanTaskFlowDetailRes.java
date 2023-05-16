package com.jd.bluedragon.common.dto.operation.workbench.warehouse.send;

import java.util.List;

/**
 * @author liwenji
 * @description
 * @date 2023-05-16 14:30
 */
public class MixScanTaskFlowDetailRes {

    /**
     * 流向详情
     */
    private List<MixScanTaskFlowDto> mixScanTaskFlowDtoList;

    public List<MixScanTaskFlowDto> getMixScanTaskFlowDtoList() {
        return mixScanTaskFlowDtoList;
    }

    public void setMixScanTaskFlowDtoList(List<MixScanTaskFlowDto> mixScanTaskFlowDtoList) {
        this.mixScanTaskFlowDtoList = mixScanTaskFlowDtoList;
    }
}
