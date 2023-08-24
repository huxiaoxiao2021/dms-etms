package com.jd.bluedragon.distribution.transport.dto;

import java.io.Serializable;

/**
 * 经停站点查询入参
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2023-08-18 11:16:30 周五
 */
public class StopoverQueryDto implements Serializable {

    private static final long serialVersionUID = -1011237781185709418L;
    private Integer siteCode;
    private String transWorkCode;
    private String sealCarCode;
    private String simpleCode;
    private String userCode;
    private String userName;
    private String vehicleNumber;

    public StopoverQueryDto() {
    }

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

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }
}
