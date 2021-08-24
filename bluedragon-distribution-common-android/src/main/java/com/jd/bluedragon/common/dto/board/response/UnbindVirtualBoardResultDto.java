package com.jd.bluedragon.common.dto.board.response;

import java.io.Serializable;

/**
 * 取消组板结果
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-08-15 15:06:51 周日
 */
public class UnbindVirtualBoardResultDto implements Serializable {
    private static final long serialVersionUID = -7653571957294904842L;

    /**
     * 板号
     */
    private String boardCode;

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

    /**
     * 运单取消总数
     */
    private Integer packageCancelTotal;

    public String getBoardCode() {
        return boardCode;
    }

    public UnbindVirtualBoardResultDto setBoardCode(String boardCode) {
        this.boardCode = boardCode;
        return this;
    }

    public Integer getBoardStatus() {
        return boardStatus;
    }

    public UnbindVirtualBoardResultDto setBoardStatus(Integer boardStatus) {
        this.boardStatus = boardStatus;
        return this;
    }

    public Integer getDestinationId() {
        return destinationId;
    }

    public UnbindVirtualBoardResultDto setDestinationId(Integer destinationId) {
        this.destinationId = destinationId;
        return this;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public UnbindVirtualBoardResultDto setDestinationName(String destinationName) {
        this.destinationName = destinationName;
        return this;
    }

    public Integer getPackageTotal() {
        return packageTotal;
    }

    public UnbindVirtualBoardResultDto setPackageTotal(Integer packageTotal) {
        this.packageTotal = packageTotal;
        return this;
    }

    public Integer getBoxTotal() {
        return boxTotal;
    }

    public UnbindVirtualBoardResultDto setBoxTotal(Integer boxTotal) {
        this.boxTotal = boxTotal;
        return this;
    }

    public Integer getWaybillTotal() {
        return waybillTotal;
    }

    public UnbindVirtualBoardResultDto setWaybillTotal(Integer waybillTotal) {
        this.waybillTotal = waybillTotal;
        return this;
    }

    public Integer getPackageCancelTotal() {
        return packageCancelTotal;
    }

    public UnbindVirtualBoardResultDto setPackageCancelTotal(Integer packageCancelTotal) {
        this.packageCancelTotal = packageCancelTotal;
        return this;
    }
}
