package com.jd.bluedragon.common.dto.collectpackage.response;

import java.io.Serializable;
import java.util.List;

public class CollectPackageTaskResp implements Serializable {

    /**
     * 待集包
     */
    private Integer toCollectPackCount;

    /**
     * 集包中
     */
    private Integer collectPackCount;

    /**
     * 已完成
     */
    private Integer completeCollectPackCount;

    /**
     * 集包任务
     */
    private List<CollectPackageTaskDto> collectPackTaskDtoList;

    public Integer getToCollectPackCount() {
        return toCollectPackCount;
    }

    public void setToCollectPackCount(Integer toCollectPackCount) {
        this.toCollectPackCount = toCollectPackCount;
    }

    public Integer getCollectPackCount() {
        return collectPackCount;
    }

    public void setCollectPackCount(Integer collectPackCount) {
        this.collectPackCount = collectPackCount;
    }

    public Integer getCompleteCollectPackCount() {
        return completeCollectPackCount;
    }

    public void setCompleteCollectPackCount(Integer completeCollectPackCount) {
        this.completeCollectPackCount = completeCollectPackCount;
    }

    public List<CollectPackageTaskDto> getCollectPackTaskDtoList() {
        return collectPackTaskDtoList;
    }

    public void setCollectPackTaskDtoList(List<CollectPackageTaskDto> collectPackTaskDtoList) {
        this.collectPackTaskDtoList = collectPackTaskDtoList;
    }
}
