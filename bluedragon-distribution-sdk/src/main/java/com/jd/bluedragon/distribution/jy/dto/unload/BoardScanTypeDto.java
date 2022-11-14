package com.jd.bluedragon.distribution.jy.dto.unload;

import java.io.Serializable;

/**
 * @Author zhengchengfa
 * @Description //TODO
 * @date
 **/
public class BoardScanTypeDto implements Serializable {
    private static final long serialVersionUID = 8158324710994501051L;

    private String board;

    private String firstScanCode;

    private Long firstScanTime;
    // package  box
    private String boardType;

    private Integer siteCode;

    private String BizId;


    public String getBoard() {
        return board;
    }

    public void setBoard(String board) {
        this.board = board;
    }

    public String getFirstScanCode() {
        return firstScanCode;
    }

    public void setFirstScanCode(String firstScanCode) {
        this.firstScanCode = firstScanCode;
    }

    public Long getFirstScanTime() {
        return firstScanTime;
    }

    public void setFirstScanTime(Long firstScanTime) {
        this.firstScanTime = firstScanTime;
    }

    public String getBoardType() {
        return boardType;
    }

    public void setBoardType(String boardType) {
        this.boardType = boardType;
    }

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public String getBizId() {
        return BizId;
    }

    public void setBizId(String bizId) {
        BizId = bizId;
    }
}
