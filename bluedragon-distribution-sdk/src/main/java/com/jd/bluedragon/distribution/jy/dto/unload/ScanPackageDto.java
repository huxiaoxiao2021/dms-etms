package com.jd.bluedragon.distribution.jy.dto.unload;



import java.io.Serializable;

public class ScanPackageDto extends UnloadBaseDto implements Serializable {
    private static final long serialVersionUID = 1954736364227854974L;
    /**
     * 卸车任务
     */
    private String bizId;
    /**
     * 扫描的包裹/箱号
     */
    private String scanCode;

    private Integer type;

    /**
     * 进行中的板，基于这个板进行组板
     */
    private String boardCode;

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getScanCode() {
        return scanCode;
    }

    public void setScanCode(String scanCode) {
        this.scanCode = scanCode;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getBoardCode() {
        return boardCode;
    }

    public void setBoardCode(String boardCode) {
        this.boardCode = boardCode;
    }
}
