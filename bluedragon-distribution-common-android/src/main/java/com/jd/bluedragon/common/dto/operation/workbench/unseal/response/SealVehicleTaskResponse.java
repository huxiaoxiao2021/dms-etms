package com.jd.bluedragon.common.dto.operation.workbench.unseal.response;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName SealVehicleTaskResponse
 * @Description
 * @Author wyh
 * @Date 2022/3/2 16:34
 **/
public class SealVehicleTaskResponse implements Serializable {

    private static final long serialVersionUID = -8512216975520015977L;

    /**
     * 封车状态数量统计
     */
    private List<VehicleStatusStatis> statusStatis;

    /**
     * 待解封数据
     */
    private UnSealCarData<ToSealCarInfo> toSealCarData;

    /**
     * 待卸车数据
     */
    private UnSealCarData<ToUnloadCarInfo> toUnloadCarData;

    /**
     * 卸车数据
     */
    private UnSealCarData<UnloadCarInfo> unloadCarData;

    /**
     * 车辆在途数据
     */
    private UnSealCarData<DrivingCarInfo> drivingData;

    public List<VehicleStatusStatis> getStatusStatis() {
        return statusStatis;
    }

    public void setStatusStatis(List<VehicleStatusStatis> statusStatis) {
        this.statusStatis = statusStatis;
    }

    public UnSealCarData<ToSealCarInfo> getToSealCarData() {
        return toSealCarData;
    }

    public void setToSealCarData(UnSealCarData<ToSealCarInfo> toSealCarData) {
        this.toSealCarData = toSealCarData;
    }

    public UnSealCarData<ToUnloadCarInfo> getToUnloadCarData() {
        return toUnloadCarData;
    }

    public void setToUnloadCarData(UnSealCarData<ToUnloadCarInfo> toUnloadCarData) {
        this.toUnloadCarData = toUnloadCarData;
    }

    public UnSealCarData<UnloadCarInfo> getUnloadCarData() {
        return unloadCarData;
    }

    public void setUnloadCarData(UnSealCarData<UnloadCarInfo> unloadCarData) {
        this.unloadCarData = unloadCarData;
    }

    public UnSealCarData<DrivingCarInfo> getDrivingData() {
        return drivingData;
    }

    public void setDrivingData(UnSealCarData<DrivingCarInfo> drivingData) {
        this.drivingData = drivingData;
    }
}
