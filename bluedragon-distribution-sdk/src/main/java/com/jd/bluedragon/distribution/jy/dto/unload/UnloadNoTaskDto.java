package com.jd.bluedragon.distribution.jy.dto.unload;

import java.io.Serializable;

/**
 * 无任务卸车请求
 */
public class UnloadNoTaskDto extends UnloadBaseDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 操作人场地ID
     */
    private Integer operateSiteId;
    /**
     * 操作人场地名称
      */
    private String operateSiteName;
    /**
     * 车牌号
      */
    private String vehicleNumber;

    public Integer getOperateSiteId() {
        return operateSiteId;
    }

    public void setOperateSiteId(Integer operateSiteId) {
        this.operateSiteId = operateSiteId;
    }

    public String getOperateSiteName() {
        return operateSiteName;
    }

    public void setOperateSiteName(String operateSiteName) {
        this.operateSiteName = operateSiteName;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }
}
