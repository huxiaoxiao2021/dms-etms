package com.jd.bluedragon.common.dto.operation.workbench.unload.response;

import com.jd.bluedragon.common.dto.operation.workbench.unseal.response.LineTypeStatis;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName UnloadVehicleData
 * @Description
 * @Author wyh
 * @Date 2022/4/2 11:22
 **/
public class UnloadVehicleData<T> implements Serializable {

    private static final long serialVersionUID = -3630137990394812580L;

    /**
     * 车辆状态
     */
    private Integer vehicleStatus;

    /**
     * 车辆数据
     */
    private List<T> data;

    /**
     * 车辆类型统计
     */
    private List<LineTypeStatis> lineStatistics;

    public Integer getVehicleStatus() {
        return vehicleStatus;
    }

    public void setVehicleStatus(Integer vehicleStatus) {
        this.vehicleStatus = vehicleStatus;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public List<LineTypeStatis> getLineStatistics() {
        return lineStatistics;
    }

    public void setLineStatistics(List<LineTypeStatis> lineStatistics) {
        this.lineStatistics = lineStatistics;
    }
}
