package com.jd.bluedragon.common.dto.board.request;

import com.jd.bluedragon.common.dto.base.request.OperatorInfo;

import java.io.Serializable;

/**
 * 解绑板包关系
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-08-15 18:27:41 周日
 */
public class UnbindToVirtualBoardPo implements Serializable {
    private static final long serialVersionUID = -5922775950510369157L;

    /**
     * 操作人信息
     */
    private OperatorInfo operatorInfo;

    /**
     * 扫描条码
     */
    private String barCode;

    /**
     * 是否取消整个板
     */
    private Integer cancelWholeBoard;

    public OperatorInfo getOperatorInfo() {
        return operatorInfo;
    }

    public UnbindToVirtualBoardPo setOperatorInfo(OperatorInfo operatorInfo) {
        this.operatorInfo = operatorInfo;
        return this;
    }

    public String getBarCode() {
        return barCode;
    }

    public UnbindToVirtualBoardPo setBarCode(String barCode) {
        this.barCode = barCode;
        return this;
    }

    public Integer getCancelWholeBoard() {
        return cancelWholeBoard;
    }

    public UnbindToVirtualBoardPo setCancelWholeBoard(Integer cancelWholeBoard) {
        this.cancelWholeBoard = cancelWholeBoard;
        return this;
    }
}
