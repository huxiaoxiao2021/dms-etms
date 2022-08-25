package com.jd.bluedragon.common.dto.operation.workbench.transport.request;

import java.io.Serializable;

/**
 * 运输任务请求体
 *
 * @author hujiping
 * @date 2022/7/18 5:30 PM
 */
public class TransportTaskRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    // 操作场地
    private Integer siteCode;
    // 派车单号
    private String transWorkCode;
    // 封车编码
    private String sealCarCode;
    // 明细简码
    private String simpleCode;
    // 车牌号
    private String vehicleNumber;

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public String getTransWorkCode() {
        return transWorkCode;
    }

    public void setTransWorkCode(String transWorkCode) {
        this.transWorkCode = transWorkCode;
    }

    public String getSealCarCode() {
        return sealCarCode;
    }

    public void setSealCarCode(String sealCarCode) {
        this.sealCarCode = sealCarCode;
    }

    public String getSimpleCode() {
        return simpleCode;
    }

    public void setSimpleCode(String simpleCode) {
        this.simpleCode = simpleCode;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }
}
