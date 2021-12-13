package com.jd.bluedragon.common.dto.board.request;

import java.io.Serializable;

public class AutoBoardCompleteRequest implements Serializable {
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
     * 操作人所属场地
     */
    private Integer siteCode;

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
}
