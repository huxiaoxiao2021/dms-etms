package com.jd.bluedragon.distribution.api.request;

import java.io.Serializable;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.api.request
 * @ClassName: SealCarRequest
 * @Description: 查询待解封的封签方法
 * @Author： wuzuxiang
 * @CreateDate 2022/2/23 17:43
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public class SealCodeRequest implements Serializable {

    /**
     * 车牌号
     */
    private String vehicleNumber;

    /**
     * 操作站点Id
     */
    private Integer deSealSiteId;

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public Integer getDeSealSiteId() {
        return deSealSiteId;
    }

    public void setDeSealSiteId(Integer deSealSiteId) {
        this.deSealSiteId = deSealSiteId;
    }
}
