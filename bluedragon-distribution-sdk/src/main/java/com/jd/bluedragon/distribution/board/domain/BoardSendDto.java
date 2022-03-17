package com.jd.bluedragon.distribution.board.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 板发货状态
 */
public class BoardSendDto implements Serializable {
    private static final long serialVersionUID = 328671719132840528L;
    private Date sendTime;
    private String boardSendEnum;
    private String boardCode;
    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public String getBoardSendEnum() {
        return boardSendEnum;
    }

    public void setBoardSendEnum(String boardSendEnum) {
        this.boardSendEnum = boardSendEnum;
    }

    public String getBoardCode() {
        return boardCode;
    }

    public void setBoardCode(String boardCode) {
        this.boardCode = boardCode;
    }
}
