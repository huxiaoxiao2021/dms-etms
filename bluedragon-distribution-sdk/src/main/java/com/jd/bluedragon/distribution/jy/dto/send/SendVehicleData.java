package com.jd.bluedragon.distribution.jy.dto.send;

import com.jd.bluedragon.distribution.jy.dto.JyLineTypeDto;

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

    /**
     * 车辆类型统计
     */
    private List<JyLineTypeDto> jyLineTypeDtos;

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

    public List<JyLineTypeDto> getJyLineTypeDtos() {
        return jyLineTypeDtos;
    }

    public void setJyLineTypeDtos(List<JyLineTypeDto> jyLineTypeDtos) {
        this.jyLineTypeDtos = jyLineTypeDtos;
    }
}
