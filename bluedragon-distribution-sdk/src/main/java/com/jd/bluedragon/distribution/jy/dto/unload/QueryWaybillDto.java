package com.jd.bluedragon.distribution.jy.dto.unload;


import java.io.Serializable;

public class QueryWaybillDto extends UnloadBaseDto implements Serializable {
    private static final long serialVersionUID = -3164990371491992850L;
    private String boardCode;
    private String waybillCode;

    public String getBoardCode() {
        return boardCode;
    }

    public void setBoardCode(String boardCode) {
        this.boardCode = boardCode;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }
}
