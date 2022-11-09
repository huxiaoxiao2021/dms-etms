package com.jd.bluedragon.common.dto.comboard.response;

import java.io.Serializable;

public class BoardDto implements Serializable {
    private static final long serialVersionUID = -7996926947828471326L;
    /**
     * 板号
     */
    private String boardCode;
    /**
     * 进度
     */
    private String progress;
    /**
     * 该板已扫包裹数量
     */
    private Integer packageHaveScanCount;
    /**
     * 该板已扫箱子数量
     */
    private Integer boxHaveScanCount;
    /**
     * 该板拦截数量
     */
    private Integer interceptCount;
    /**
     * 状态
     */
    private Integer status;

    public String getBoardCode() {
        return boardCode;
    }

    public void setBoardCode(String boardCode) {
        this.boardCode = boardCode;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public Integer getPackageHaveScanCount() {
        return packageHaveScanCount;
    }

    public void setPackageHaveScanCount(Integer packageHaveScanCount) {
        this.packageHaveScanCount = packageHaveScanCount;
    }

    public Integer getBoxHaveScanCount() {
        return boxHaveScanCount;
    }

    public void setBoxHaveScanCount(Integer boxHaveScanCount) {
        this.boxHaveScanCount = boxHaveScanCount;
    }

    public Integer getInterceptCount() {
        return interceptCount;
    }

    public void setInterceptCount(Integer interceptCount) {
        this.interceptCount = interceptCount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
