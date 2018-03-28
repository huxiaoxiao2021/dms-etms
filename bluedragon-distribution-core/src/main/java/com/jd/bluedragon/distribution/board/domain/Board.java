package com.jd.bluedragon.distribution.board.domain;

import java.util.Date;

/**
 * Created by xumei3 on 2018/3/28.
*/
public class Board {
    /**
     * 板号
     */
    private String boxCode;

    /**
     * 目的地
     */
    private String destination;

    /**
     * 板状态
     */
    private Integer boardStatus;

    /**
     * 开板时间
     */
    private Date createDate;


    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Integer getBoardStatus() {
        return boardStatus;
    }

    public void setBoardStatus(Integer boardStatus) {
        this.boardStatus = boardStatus;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}
