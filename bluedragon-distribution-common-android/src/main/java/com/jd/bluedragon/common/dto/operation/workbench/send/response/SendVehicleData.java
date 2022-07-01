package com.jd.bluedragon.common.dto.operation.workbench.send.response;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName SendVehicleData
 * @Description
 * @Author wyh
 * @Date 2022/5/17 21:29
 **/
public class SendVehicleData<T> implements Serializable {

    private static final long serialVersionUID = -9147679847630229665L;

    /**
     * 车辆状态
     */
    private Integer vehicleStatus;

    /**
     * 车辆数据
     */
    private List<T> data;

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
}
