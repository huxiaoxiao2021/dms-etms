package com.jd.bluedragon.common.dto.operation.workbench.warehouse.send;

import java.io.Serializable;
import java.util.List;

public class MixScanTaskDetailRes implements Serializable {

    private static final long serialVersionUID = -5809332610524693231L;
    /**
     * 混扫任务下流向信息
     */
    private List<MixScanTaskDetailDto> mixScanTaskDetailDtoList;

    public List<MixScanTaskDetailDto> getMixScanTaskDetailDtoList() {
        return mixScanTaskDetailDtoList;
    }

    public void setMixScanTaskDetailDtoList(List<MixScanTaskDetailDto> mixScanTaskDetailDtoList) {
        this.mixScanTaskDetailDtoList = mixScanTaskDetailDtoList;
    }
}
