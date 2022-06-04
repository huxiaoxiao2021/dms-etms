package com.jd.bluedragon.distribution.jy.dto.send;

import java.io.Serializable;

/**
 * @ClassName JyBizTaskSendCountDto
 * @Description
 * @Author wyh
 * @Date 2022/5/29 18:34
 **/
public class JyBizTaskSendCountDto implements Serializable {

    private static final long serialVersionUID = 4959005149526416723L;

    /**
     * 车辆状态
     */
    private Integer vehicleStatus;

    /**
     * 总数
     */
    private Long total;

    public Integer getVehicleStatus() {
        return vehicleStatus;
    }

    public void setVehicleStatus(Integer vehicleStatus) {
        this.vehicleStatus = vehicleStatus;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
