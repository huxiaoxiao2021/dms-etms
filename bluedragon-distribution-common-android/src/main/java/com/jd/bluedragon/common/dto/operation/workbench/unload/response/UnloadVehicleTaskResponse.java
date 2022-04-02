package com.jd.bluedragon.common.dto.operation.workbench.unload.response;

import com.jd.bluedragon.common.dto.operation.workbench.unseal.response.*;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName UnloadVehicleTaskResponse
 * @Description
 * @Author wyh
 * @Date 2022/4/2 11:11
 **/
public class UnloadVehicleTaskResponse implements Serializable {

    private static final long serialVersionUID = -5913639108262323459L;

    /**
     * 封车状态数量统计
     */
    private List<VehicleStatusStatis> statusAgg;

    /**
     * 待卸车数据
     */
    private UnloadVehicleData<ToUnloadVehicle> toUnloadVehicleData;

    /**
     * 卸车数据
     */
    private UnloadVehicleData<UnloadVehicleInfo> unloadVehicleData;

    /**
     * 卸车完成数据
     */
    private UnloadVehicleData<UnloadCompleteVehicle> unloadCompletedData;

    public List<VehicleStatusStatis> getStatusAgg() {
        return statusAgg;
    }

    public void setStatusAgg(List<VehicleStatusStatis> statusAgg) {
        this.statusAgg = statusAgg;
    }

    public UnloadVehicleData<ToUnloadVehicle> getToUnloadVehicleData() {
        return toUnloadVehicleData;
    }

    public void setToUnloadVehicleData(UnloadVehicleData<ToUnloadVehicle> toUnloadVehicleData) {
        this.toUnloadVehicleData = toUnloadVehicleData;
    }

    public UnloadVehicleData<UnloadVehicleInfo> getUnloadVehicleData() {
        return unloadVehicleData;
    }

    public void setUnloadVehicleData(UnloadVehicleData<UnloadVehicleInfo> unloadVehicleData) {
        this.unloadVehicleData = unloadVehicleData;
    }

    public UnloadVehicleData<UnloadCompleteVehicle> getUnloadCompletedData() {
        return unloadCompletedData;
    }

    public void setUnloadCompletedData(UnloadVehicleData<UnloadCompleteVehicle> unloadCompletedData) {
        this.unloadCompletedData = unloadCompletedData;
    }
}
