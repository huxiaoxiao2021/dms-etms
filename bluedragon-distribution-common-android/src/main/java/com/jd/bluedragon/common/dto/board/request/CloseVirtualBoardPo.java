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
public class CloseVirtualBoardPo  implements Serializable {
    private static final long serialVersionUID = -7084098561997305498L;

    /**
     * 操作人信息
     */
    private OperatorInfo operatorInfo;

    /**
     * 要完结的板号
     */
    private String boardCode;

    /**
     * 操作来源 1: 分拣pda 2:分拣机
     * @return
     */
    private Integer bizSource;

    /**
     * 版本：做数据隔离
     */
    private Integer version;

    public OperatorInfo getOperatorInfo() {
        return operatorInfo;
    }

    public CloseVirtualBoardPo setOperatorInfo(OperatorInfo operatorInfo) {
        this.operatorInfo = operatorInfo;
        return this;
    }

    public String getBoardCode() {
        return boardCode;
    }

    public CloseVirtualBoardPo setBoardCode(String boardCode) {
        this.boardCode = boardCode;
        return this;
    }

    public Integer getBizSource() {
        return bizSource;
    }

    public void setBizSource(Integer bizSource) {
        this.bizSource = bizSource;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
