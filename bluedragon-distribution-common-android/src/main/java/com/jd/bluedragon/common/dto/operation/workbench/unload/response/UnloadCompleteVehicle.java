package com.jd.bluedragon.common.dto.operation.workbench.unload.response;

import com.jd.bluedragon.common.dto.operation.workbench.unseal.response.VehicleBaseInfo;

import java.io.Serializable;

/**
 * @ClassName UnloadCompleteVehicle
 * @Description
 * @Author wyh
 * @Date 2022/4/2 13:57
 **/
public class UnloadCompleteVehicle extends VehicleBaseInfo implements Serializable {

    private static final long serialVersionUID = -7591490278976762291L;

    /**
     * 少扫件数
     */
    private Long lessCount;

    /**
     * 多扫件数
     */
    private Long moreCount;

    /**
     * 无任务卸车
     */
    private Boolean manualCreatedTask;

    public Long getLessCount() {
        return lessCount;
    }

    public void setLessCount(Long lessCount) {
        this.lessCount = lessCount;
    }

    public Long getMoreCount() {
        return moreCount;
    }

    public void setMoreCount(Long moreCount) {
        this.moreCount = moreCount;
    }

    public Boolean getManualCreatedTask() {
        return manualCreatedTask;
    }

    public void setManualCreatedTask(Boolean manualCreatedTask) {
        this.manualCreatedTask = manualCreatedTask;
    }
}
