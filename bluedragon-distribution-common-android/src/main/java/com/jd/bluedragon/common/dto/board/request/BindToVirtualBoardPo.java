package com.jd.bluedragon.common.dto.board.request;


import com.jd.bluedragon.common.dto.base.request.OperatorInfo;

import java.io.Serializable;
import java.util.List;

/**
 * 组板请求
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-08-15 17:44:33 周日
 */
public class BindToVirtualBoardPo implements Serializable {

    private static final long serialVersionUID = -376483646169084509L;

    /**
     * 操作人信息
     */
    private OperatorInfo operatorInfo;

    private String barCode;

    /**
     * 目标板号
     */
    private List<String> boardCodeList;

    public OperatorInfo getOperatorInfo() {
        return operatorInfo;
    }

    public BindToVirtualBoardPo setOperatorInfo(OperatorInfo operatorInfo) {
        this.operatorInfo = operatorInfo;
        return this;
    }

    public String getBarCode() {
        return barCode;
    }

    public BindToVirtualBoardPo setBarCode(String barCode) {
        this.barCode = barCode;
        return this;
    }

    public List<String> getBoardCodeList() {
        return boardCodeList;
    }

    public BindToVirtualBoardPo setBoardCodeList(List<String> boardCodeList) {
        this.boardCodeList = boardCodeList;
        return this;
    }
}
