package com.jd.bluedragon.common.dto.blockcar.request;

import java.io.Serializable;

/**
 * SealCarTaskInfoRequest
 * 封车任务信息请求
 * @author jiaowenqiang
 * @date 2019/6/25
 */
public class SealCarTaskInfoRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 车牌号
     */
    private String vehicleNumber;

    /**
     * 任务编码
     */
    private String transWorkItemCode;

    /**
     * erp账号
     */
    private String erp;

    /**
     * 分拣中心编号
     */
    private String dmsCode;

    // 分拣中心ID
    private Integer dmsSiteId;

    @Override
    public String toString() {
        return "SealCarTaskInfoRequest{" +
                "vehicleNumber='" + vehicleNumber + '\'' +
                ", transWorkItemCode='" + transWorkItemCode + '\'' +
                ", erp='" + erp + '\'' +
                ", dmsCode='" + dmsCode + '\'' +
                '}';
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getTransWorkItemCode() {
        return transWorkItemCode;
    }

    public void setTransWorkItemCode(String transWorkItemCode) {
        this.transWorkItemCode = transWorkItemCode;
    }

    public String getErp() {
        return erp;
    }

    public void setErp(String erp) {
        this.erp = erp;
    }

    public String getDmsCode() {
        return dmsCode;
    }

    public void setDmsCode(String dmsCode) {
        this.dmsCode = dmsCode;
    }

    public Integer getDmsSiteId() {
        return dmsSiteId;
    }

    public void setDmsSiteId(Integer dmsSiteId) {
        this.dmsSiteId = dmsSiteId;
    }
}
