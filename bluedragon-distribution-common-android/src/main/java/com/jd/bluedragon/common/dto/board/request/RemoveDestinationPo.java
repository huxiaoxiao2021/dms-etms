package com.jd.bluedragon.common.dto.board.request;

import com.jd.bluedragon.common.dto.base.request.OperatorInfo;

import java.io.Serializable;


/**
 * 关闭流向
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-08-15 18:24:02 周日
 */
public class RemoveDestinationPo implements Serializable {
    private static final long serialVersionUID = -7522226816306423811L;

    /**
     * 操作人信息
     */
    private OperatorInfo operatorInfo;

    private Integer destinationId;

    /**
     * 板号
     */
    private String boardCode;

    /**
     * 版本：做数据隔离
     */
    private Integer version;

    public OperatorInfo getOperatorInfo() {
        return operatorInfo;
    }

    public RemoveDestinationPo setOperatorInfo(OperatorInfo operatorInfo) {
        this.operatorInfo = operatorInfo;
        return this;
    }

    public Integer getDestinationId() {
        return destinationId;
    }

    public RemoveDestinationPo setDestinationId(Integer destinationId) {
        this.destinationId = destinationId;
        return this;
    }

    public String getBoardCode() {
        return boardCode;
    }

    public RemoveDestinationPo setBoardCode(String boardCode) {
        this.boardCode = boardCode;
        return this;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
