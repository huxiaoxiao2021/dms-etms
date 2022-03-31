package com.jd.bluedragon.common.dto.operation.workbench.unseal.response;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName SealCodeResponse
 * @Description
 * @Author wyh
 * @Date 2022/3/22 10:31
 **/
public class SealCodeResponse implements Serializable {

    private static final long serialVersionUID = -7639241901803444055L;

    /**
     * 车牌号
     */
    private String vehicleNumber;

    /**
     * 任务号
     */
    private String billCode;

    /**
     * 封签号集合
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
