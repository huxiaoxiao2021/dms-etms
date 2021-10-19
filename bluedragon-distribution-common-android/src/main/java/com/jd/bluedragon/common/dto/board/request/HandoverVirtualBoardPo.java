package com.jd.bluedragon.common.dto.board.request;

import com.jd.bluedragon.common.dto.base.request.OperatorInfo;

import java.io.Serializable;

/**
 * 完结板号请求参数
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-08-15 18:17:36 周日
 */
public class HandoverVirtualBoardPo implements Serializable {
    private static final long serialVersionUID = -7084098561997305498L;

    /**
     * 操作人信息
     */
    private OperatorInfo operatorInfo;

    /**
     * 要完结的板号
     */
    private String boardCode;

    public OperatorInfo getOperatorInfo() {
        return operatorInfo;
    }

    public HandoverVirtualBoardPo setOperatorInfo(OperatorInfo operatorInfo) {
        this.operatorInfo = operatorInfo;
        return this;
    }

    public String getBoardCode() {
        return boardCode;
    }

    public HandoverVirtualBoardPo setBoardCode(String boardCode) {
        this.boardCode = boardCode;
        return this;
    }
}
