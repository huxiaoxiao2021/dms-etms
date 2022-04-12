package com.jd.bluedragon.distribution.api.response;

import java.io.Serializable;
import java.util.List;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.api.response
 * @ClassName: SealCodesResponse
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2022/2/23 17:57
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public class SealCodesResponse implements Serializable {

    /**
     * 车牌号
     */
    private String vehicleNumber;

    /**
     * 待解任务号
     */
    private String billCode;

    /**
     * 待解封签号
     */
    private List<String> sealCodes;

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getBillCode() {
        return billCode;
    }

    public void setBillCode(String billCode) {
        this.billCode = billCode;
    }

    public List<String> getSealCodes() {
        return sealCodes;
    }

    public void setSealCodes(List<String> sealCodes) {
        this.sealCodes = sealCodes;
    }
}
