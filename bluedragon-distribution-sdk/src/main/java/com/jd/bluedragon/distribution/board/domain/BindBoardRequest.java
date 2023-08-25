package com.jd.bluedragon.distribution.board.domain;

import java.io.Serializable;

public class BindBoardRequest implements Serializable {
    private static final long serialVersionUID = -1390506536718685368L;
    //包裹号或箱号
    private String barcode;
    /**
     * 操作人信息
     */
    private OperatorInfo operatorInfo;
    /**
     * 板信息
     */
    private Board board;

    /**
     * 操作来源 1:自动分拣机
     */
    private Integer bizSource;

    /**
     * 是否需要补发货
     */
    private Boolean needReplenishDelivery;

    /**自动化设备编码*/
    private String machineCode;

    private boolean cancelLast;
    
    /** 自动化分拣设备格口**/
    private String chuteCode;

    /** 网格唯一编码 **/
    private String gridBusinessKey;


    public boolean getCancelLast() {
        return cancelLast;
    }

    public void setCancelLast(boolean cancelLast) {
        this.cancelLast = cancelLast;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public OperatorInfo getOperatorInfo() {
        return operatorInfo;
    }

    public void setOperatorInfo(OperatorInfo operatorInfo) {
        this.operatorInfo = operatorInfo;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public Integer getBizSource() {
        return bizSource;
    }

    public void setBizSource(Integer bizSource) {
        this.bizSource = bizSource;
    }

    public Boolean getNeedReplenishDelivery() {
        return needReplenishDelivery;
    }

    public void setNeedReplenishDelivery(Boolean needReplenishDelivery) {
        this.needReplenishDelivery = needReplenishDelivery;
    }

	public String getMachineCode() {
		return machineCode;
	}

	public void setMachineCode(String machineCode) {
		this.machineCode = machineCode;
	}

    public String getChuteCode() {
        return chuteCode;
    }

    public void setChuteCode(String chuteCode) {
        this.chuteCode = chuteCode;
    }

    public String getGridBusinessKey() {
        return gridBusinessKey;
    }

    public void setGridBusinessKey(String gridBusinessKey) {
        this.gridBusinessKey = gridBusinessKey;
    }
}
