package com.jd.bluedragon.common.dto.seal.response;

import java.io.Serializable;
import java.util.List;

public class SealCodeResp implements Serializable {
    private static final long serialVersionUID = -6877539559926058958L;
    /**
     * 车牌号
     */
    private String vehicleNumber;

    /**
     * 封签号集合
     */
    private List<String> sealCodeList;

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public List<String> getSealCodeList() {
        return sealCodeList;
    }

    public void setSealCodeList(List<String> sealCodeList) {
        this.sealCodeList = sealCodeList;
    }
}
