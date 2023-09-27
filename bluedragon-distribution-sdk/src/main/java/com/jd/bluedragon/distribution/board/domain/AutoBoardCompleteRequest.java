package com.jd.bluedragon.distribution.board.domain;

import java.io.Serializable;

import com.jd.bluedragon.distribution.api.domain.OperatorData;

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

	public OperatorData getOperatorData() {
		return operatorData;
	}

	public void setOperatorData(OperatorData operatorData) {
		this.operatorData = operatorData;
	}
}
