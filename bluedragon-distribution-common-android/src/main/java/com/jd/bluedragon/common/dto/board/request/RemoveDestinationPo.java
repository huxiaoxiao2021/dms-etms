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
     * 板状态变更开关： null:首次操作流向删除      true：确认流向删除，同时完结板状态；   false: 确认流向删除，但不改变板状态
     */
    private Boolean boardStatusEndSwitch;

    /**
     * 版本：做数据隔离
     */
    private Integer version;

    /**
     * 模式： 1单流向模式   2：多流向模式   初始化：null(默认多流向模式)
     */
    private Integer flowFlag;

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

    public Boolean getBoardStatusEndSwitch() {
        return boardStatusEndSwitch;
    }

    public void setBoardStatusEndSwitch(Boolean boardStatusEndSwitch) {
        this.boardStatusEndSwitch = boardStatusEndSwitch;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getFlowFlag() {
        return flowFlag;
    }

    public void setFlowFlag(Integer flowFlag) {
        this.flowFlag = flowFlag;
    }
}
