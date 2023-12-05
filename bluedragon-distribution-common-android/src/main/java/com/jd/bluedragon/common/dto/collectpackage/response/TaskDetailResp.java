package com.jd.bluedragon.common.dto.collectpackage.response;

import java.io.Serializable;

public class TaskDetailResp implements Serializable {
    
    private CollectPackageTaskDto collectPackageTaskDto;

    public CollectPackageTaskDto getCollectPackageTaskDto() {
        return collectPackageTaskDto;
    }

    public void setCollectPackageTaskDto(CollectPackageTaskDto collectPackageTaskDto) {
        this.collectPackageTaskDto = collectPackageTaskDto;
    }
}
