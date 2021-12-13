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
}
