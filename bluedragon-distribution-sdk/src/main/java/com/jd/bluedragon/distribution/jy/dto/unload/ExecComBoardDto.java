package com.jd.bluedragon.distribution.jy.dto.unload;

public class ExecComBoardDto {
    private String boardCode;
    private String barCode;
    public ExecComBoardDto() {
    }
    public ExecComBoardDto(String boardCode, String barCode) {
        this.boardCode = boardCode;
        this.barCode = barCode;
    }

    public String getBoardCode() {
        return boardCode;
    }

    public void setBoardCode(String boardCode) {
        this.boardCode = boardCode;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }
}
