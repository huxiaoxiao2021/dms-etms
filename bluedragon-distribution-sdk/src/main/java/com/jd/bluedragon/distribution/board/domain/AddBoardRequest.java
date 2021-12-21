package com.jd.bluedragon.distribution.board.domain;

/**
 * 新建板请求类
 */
public class AddBoardRequest extends OperatorInfo{

    private static final long serialVersionUID = 5463686883554622340L;

    /**
     * 目的地名称
     */
    private String destination;
    /**
     * 目的地编号
     */
    Integer destinationId;

    /**
     * 新建板数量
     */
    Integer boardCount;

    /**
     * 操作来源 1: 分拣pda 2:分拣机
     * @return
     */
    Integer bizSource;

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Integer getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(Integer destinationId) {
        this.destinationId = destinationId;
    }

    public Integer getBoardCount() {
        return boardCount;
    }

    public void setBoardCount(Integer boardCount) {
        this.boardCount = boardCount;
    }

    public Integer getBizSource() {
        return bizSource;
    }

    public void setBizSource(Integer bizSource) {
        this.bizSource = bizSource;
    }
}
