package com.jd.bluedragon.common.dto.operation.workbench.unload.response;

import com.jd.bluedragon.common.dto.operation.workbench.unseal.response.VehicleBaseInfo;
import com.jd.bluedragon.common.dto.select.SelectOption;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @ClassName UnloadVehicleInfo
 * @Description
 * @Author wyh
 * @Date 2022/4/2 11:34
 **/
public class UnloadVehicleInfo extends VehicleBaseInfo implements Serializable {

    private static final long serialVersionUID = 7007964985586446559L;

    /**
     * 卸车进度
     */
    private BigDecimal unloadProgress;

    /**
     * 单号标签集合
     */
    private List<SelectOption> tags;

    /**
     * 无任务卸车
     */
    private Boolean manualCreatedTask;

    public BigDecimal getUnloadProgress() {
        return unloadProgress;
    }

    public void setUnloadProgress(BigDecimal unloadProgress) {
        this.unloadProgress = unloadProgress;
    }

    public List<SelectOption> getTags() {
        return tags;
    }

    public void setTags(List<SelectOption> tags) {
        this.tags = tags;
    }

    public Boolean getManualCreatedTask() {
        return manualCreatedTask;
    }

    public void setManualCreatedTask(Boolean manualCreatedTask) {
        this.manualCreatedTask = manualCreatedTask;
    }
}
