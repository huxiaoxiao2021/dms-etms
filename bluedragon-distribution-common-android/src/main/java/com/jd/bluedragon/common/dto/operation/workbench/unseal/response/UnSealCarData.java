package com.jd.bluedragon.common.dto.operation.workbench.unseal.response;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName UnSealCarData
 * @Description
 * @Author wyh
 * @Date 2022/3/3 13:57
 **/
public class UnSealCarData<T> implements Serializable {

    private static final long serialVersionUID = 8453028878992044574L;
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
