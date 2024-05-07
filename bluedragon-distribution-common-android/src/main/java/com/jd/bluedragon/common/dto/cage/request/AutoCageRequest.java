package com.jd.bluedragon.common.dto.cage.request;

import com.jd.bluedragon.common.dto.base.request.OperatorData;

import java.io.Serializable;

public class AutoCageRequest implements Serializable {
    /**
     * 分拣机编码
     */
    private String machineCode;
    /**
     * 包裹号或箱号
     */
    private String barcode;
    /**
     * 操作人erp
     */
    private String operatorErp;
    /**
     * 操作人姓名
     */
    private String operatorName;
    /**
     * 操作人所属场地
     */
    private Integer siteCode;

    /**
     * 笼车号
     */
    private String cageCode;
    /**
     * 笼车箱号
     */
    private String cageBoxCode;

    /**
     * 操作信息对象
     */
    private OperatorData operatorData;

    public String getMachineCode() {
        return machineCode;
    }

    public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getOperatorErp() {
        return operatorErp;
    }

    public void setOperatorErp(String operatorErp) {
        this.operatorErp = operatorErp;
    }

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public String getCageCode() {
        return cageCode;
    }

    public void setCageCode(String cageCode) {
        this.cageCode = cageCode;
    }

    public String getCageBoxCode() {
        return cageBoxCode;
    }

    public void setCageBoxCode(String cageBoxCode) {
        this.cageBoxCode = cageBoxCode;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public OperatorData getOperatorData() {
        return operatorData;
    }

    public void setOperatorData(OperatorData operatorData) {
        this.operatorData = operatorData;
    }
}
