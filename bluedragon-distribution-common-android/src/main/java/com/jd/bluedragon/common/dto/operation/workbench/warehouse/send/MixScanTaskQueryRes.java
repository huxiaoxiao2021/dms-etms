package com.jd.bluedragon.common.dto.operation.workbench.warehouse.send;

import java.io.Serializable;
import java.util.List;

public class MixScanTaskQueryRes implements Serializable {

    private static final long serialVersionUID = -5809332610524693231L;
    
    private List<MixScanTaskDto> mixScanTaskDtoList;

    public List<MixScanTaskDto> getMixScanTaskDtoList() {
        return mixScanTaskDtoList;
    }

    public void setMixScanTaskDtoList(List<MixScanTaskDto> mixScanTaskDtoList) {
        this.mixScanTaskDtoList = mixScanTaskDtoList;
    }
}
