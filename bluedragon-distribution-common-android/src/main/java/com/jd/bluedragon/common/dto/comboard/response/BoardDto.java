package com.jd.bluedragon.common.dto.comboard.response;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class BoardDto implements Serializable {
    private static final long serialVersionUID = -7996926947828471326L;
    /**
     * 板号
     */
    private String boardCode;

    private Date boardCreateTime;
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
     * 板内货物重量
     */
    private String weight;
    /**
     * 板内货物体积
     */
    private String volume;
    /**
     * 该板拦截数量
     */
    private Integer interceptCount;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 状态
     */
    private String statusDesc;
    /**
     * 板内件数
     */
    private Integer count;

    private boolean bulkFlag;

    private String bizId;

    private String sendCode;

    /**
     * 组板入口类型
     */
    private String comboardSource;

    List<GoodsCategoryDto> goodsCategoryDtos;


    /**
     * 选中状态
     */
    private boolean selectedFlag;

    /**
     * 展开状态
     */
    private boolean extendFlag;

    public Date getBoardCreateTime() {
        return boardCreateTime;
    }

    public void setBoardCreateTime(Date boardCreateTime) {
        this.boardCreateTime = boardCreateTime;
    }

    public List<GoodsCategoryDto> getGoodsCategoryDtos() {
        return goodsCategoryDtos;
    }

    public void setGoodsCategoryDtos(
        List<GoodsCategoryDto> goodsCategoryDtos) {
        this.goodsCategoryDtos = goodsCategoryDtos;
    }

    /**
     * 板扫描上线
     */
    private Integer boardScanLimit;

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

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public boolean getBulkFlag() {
        return bulkFlag;
    }

    public void setBulkFlag(boolean bulkFlag) {
        this.bulkFlag = bulkFlag;
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }

    public Integer getBoardScanLimit() {
        return boardScanLimit;
    }

    public void setBoardScanLimit(Integer boardScanLimit) {
        this.boardScanLimit = boardScanLimit;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getComboardSource() {
        return comboardSource;
    }

    public void setComboardSource(String comboardSource) {
        this.comboardSource = comboardSource;
    }

    public boolean getSelectedFlag() {
        return selectedFlag;
    }

    public void setSelectedFlag(boolean selectedFlag) {
        this.selectedFlag = selectedFlag;
    }

    public boolean isExtendFlag() {
        return extendFlag;
    }

    public void setExtendFlag(boolean extendFlag) {
        this.extendFlag = extendFlag;
    }
}
