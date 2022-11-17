package com.jd.bluedragon.distribution.jy.dto.unload;


import java.io.Serializable;

public class QueryBoardDto extends UnloadBaseDto implements Serializable {
    private static final long serialVersionUID = -1686143507207719039L;
    private String boardCode;

    public String getBoardCode() {
        return boardCode;
    }

    public void setBoardCode(String boardCode) {
        this.boardCode = boardCode;
    }
}
