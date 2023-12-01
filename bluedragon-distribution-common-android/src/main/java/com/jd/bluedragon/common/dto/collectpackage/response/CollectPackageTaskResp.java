package com.jd.bluedragon.common.dto.collectpackage.response;

import java.io.Serializable;
import java.util.List;

public class CollectPackageTaskResp implements Serializable {

    /**
     * 集包任务推荐
     */
    private List<CollectPackStatusCount> collectPackStatusCountList;

    /**
     * 集包任务
     */
    private List<CollectPackageTaskDto> collectPackTaskDtoList;

    public CollectPackageTaskResp() {
    }


    public List<CollectPackageTaskDto> getCollectPackTaskDtoList() {
        return collectPackTaskDtoList;
    }

    public void setCollectPackTaskDtoList(List<CollectPackageTaskDto> collectPackTaskDtoList) {
        this.collectPackTaskDtoList = collectPackTaskDtoList;
    }

    public List<CollectPackStatusCount> getCollectPackStatusCountList() {
        return collectPackStatusCountList;
    }

    public void setCollectPackStatusCountList(List<CollectPackStatusCount> collectPackStatusCountList) {
        this.collectPackStatusCountList = collectPackStatusCountList;
    }
}
