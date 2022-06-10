package com.jd.bluedragon.common.dto.send.response;

import java.io.Serializable;
import java.util.List;

public class VehicleTaskResp implements Serializable {
    private static final long serialVersionUID = -6947165328541242840L;
    //主任务列表
    private List<VehicleTaskDto> vehicleTaskDtoList;
    //同流向任务数量
    private Integer count;

    public List<VehicleTaskDto> getVehicleTaskDtoList() {
        return vehicleTaskDtoList;
    }

    public void setVehicleTaskDtoList(List<VehicleTaskDto> vehicleTaskDtoList) {
        this.vehicleTaskDtoList = vehicleTaskDtoList;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
