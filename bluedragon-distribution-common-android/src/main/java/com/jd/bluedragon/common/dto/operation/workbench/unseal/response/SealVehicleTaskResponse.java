package com.jd.bluedragon.common.dto.operation.workbench.unseal.response;

import com.jd.bluedragon.common.dto.operation.workbench.config.dto.ClientAutoRefreshConfig;

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

    /**
     * 自动刷新配置
     */
    private ClientAutoRefreshConfig clientAutoRefreshConfig;

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

    /**
     * 判断返回任务数据为空
     * @return
     */
    public Boolean responseDataIsNull() {
        return (this.toSealCarData == null || this.toSealCarData.getData() == null || this.toSealCarData.getData().size() == 0)
                && (this.toUnloadCarData == null || this.toUnloadCarData.getData() == null || this.toUnloadCarData.getData().size() == 0)
                && (this.unloadCarData == null || this.unloadCarData.getData() == null || this.unloadCarData.getData().size() == 0)
                && (this.drivingData == null || this.drivingData.getData() == null || this.drivingData.getData().size() == 0);
    }

    public ClientAutoRefreshConfig getClientAutoRefreshConfig() {
        return clientAutoRefreshConfig;
    }

    public void setClientAutoRefreshConfig(ClientAutoRefreshConfig clientAutoRefreshConfig) {
        this.clientAutoRefreshConfig = clientAutoRefreshConfig;
    }
}
