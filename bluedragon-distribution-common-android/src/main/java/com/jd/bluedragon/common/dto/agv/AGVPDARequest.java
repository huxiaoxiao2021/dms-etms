package com.jd.bluedragon.common.dto.agv;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;

import java.io.Serializable;



public class AGVPDARequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private CurrentOperate currentOperate;

    private Integer agvOperateType;

    private String agvNumber;

    private String machineCode;

    private String groundCode;

    private String packageCode;

    private String operatorErp;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public CurrentOperate getCurrentOperate() {
        return currentOperate;
    }

    public void setCurrentOperate(CurrentOperate currentOperate) {
        this.currentOperate = currentOperate;
    }

    public Integer getAgvOperateType() {
        return agvOperateType;
    }

    public void setAgvOperateType(Integer agvOperateType) {
        this.agvOperateType = agvOperateType;
    }

    public String getAgvNumber() {
        return agvNumber;
    }

    public void setAgvNumber(String agvNumber) {
        this.agvNumber = agvNumber;
    }

    public String getMachineCode() {
        return machineCode;
    }

    public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
    }

    public String getGroundCode() {
        return groundCode;
    }

    public void setGroundCode(String groundCode) {
        this.groundCode = groundCode;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public String getOperatorErp() {
        return operatorErp;
    }

    public void setOperatorErp(String operatorErp) {
        this.operatorErp = operatorErp;
    }
}
