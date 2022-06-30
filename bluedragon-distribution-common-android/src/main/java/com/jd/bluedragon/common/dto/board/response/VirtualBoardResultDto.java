package com.jd.bluedragon.common.dto.board.response;

import java.io.Serializable;

/**
 * 组板结果
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-08-15 15:06:51 周日
 */
public class VirtualBoardResultDto implements Serializable {
    private static final long serialVersionUID = -7653571957294904842L;

    /**
     * 板号
     */
    private String boardCode;

    /**
     * 是否创建了新板
     */
    private Boolean newBoardIsCreated;

    /**
     * 板状态
     */
    private Integer boardStatus;

    /**
     * 目的地ID
     */
    private Integer destinationId;

    /**
     * 目的地名称
     */
    private String destinationName;

    /**
     * 包裹总数
     */
    private Integer packageTotal;

    /***
     * 箱号总数
     */
    private Integer boxTotal;

    /**
     * 运单总数
     */
    private Integer waybillTotal;

    private Long createTimeMillSeconds;

    private String warnMsg;


    public String getBoardCode() {
        return boardCode;
    }

    public VirtualBoardResultDto setBoardCode(String boardCode) {
        this.boardCode = boardCode;
        return this;
    }

    public Boolean getNewBoardIsCreated() {
        return newBoardIsCreated;
    }

    public VirtualBoardResultDto setNewBoardIsCreated(Boolean newBoardIsCreated) {
        this.newBoardIsCreated = newBoardIsCreated;
        return this;
    }

    public Integer getBoardStatus() {
        return boardStatus;
    }

    public VirtualBoardResultDto setBoardStatus(Integer boardStatus) {
        this.boardStatus = boardStatus;
        return this;
    }

    public Integer getDestinationId() {
        return destinationId;
    }

    public VirtualBoardResultDto setDestinationId(Integer destinationId) {
        this.destinationId = destinationId;
        return this;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public VirtualBoardResultDto setDestinationName(String destinationName) {
        this.destinationName = destinationName;
        return this;
    }

    public Integer getPackageTotal() {
        return packageTotal;
    }

    public VirtualBoardResultDto setPackageTotal(Integer packageTotal) {
        this.packageTotal = packageTotal;
        return this;
    }

    public Integer getBoxTotal() {
        return boxTotal;
    }

    public VirtualBoardResultDto setBoxTotal(Integer boxTotal) {
        this.boxTotal = boxTotal;
        return this;
    }

    public Integer getWaybillTotal() {
        return waybillTotal;
    }

    public VirtualBoardResultDto setWaybillTotal(Integer waybillTotal) {
        this.waybillTotal = waybillTotal;
        return this;
    }

    public Long getCreateTimeMillSeconds() {
        return createTimeMillSeconds;
    }

    public VirtualBoardResultDto setCreateTimeMillSeconds(Long createTimeMillSeconds) {
        this.createTimeMillSeconds = createTimeMillSeconds;
        return this;
    }

    public String getWarnMsg() {
        return warnMsg;
    }

    public void setWarnMsg(String warnMsg) {
        this.warnMsg = warnMsg;
    }
}
