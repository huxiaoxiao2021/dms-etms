package com.jd.bluedragon.distribution.jy.comboard;

import java.util.Date;

public class JyComboardAggsEntity {
    private Long id;

    private Integer operateSiteId;

    private Integer receiveSiteId;

    private String sendFlow;

    private String bizId;

    private String boardCode;

    private String productType;

    private Integer scanType;

    private Integer scannedCount;

    private Integer boardCount;

    private Integer moreScannedCount;

    private Integer interceptCount;

    private Integer waitScanCount;

    private Date createTime;

    private Date updateTime;

    private Boolean yn;

    private Date ts;

    private Integer packageScannedCount;

    private Integer boxScannedCount;

    private Double weight;

    private Double volume;

    private Integer packageTotalScannedCount;

    public Integer getPackageTotalScannedCount() {
        return packageTotalScannedCount;
    }

    public void setPackageTotalScannedCount(Integer packageTotalScannedCount) {
        this.packageTotalScannedCount = packageTotalScannedCount;
    }

    public Integer getOperateSiteId() {
        return operateSiteId;
    }

    public void setOperateSiteId(Integer operateSiteId) {
        this.operateSiteId = operateSiteId;
    }

    public Integer getReceiveSiteId() {
        return receiveSiteId;
    }

    public void setReceiveSiteId(Integer receiveSiteId) {
        this.receiveSiteId = receiveSiteId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSendFlow() {
        return sendFlow;
    }

    public void setSendFlow(String sendFlow) {
        this.sendFlow = sendFlow;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getBoardCode() {
        return boardCode;
    }

    public void setBoardCode(String boardCode) {
        this.boardCode = boardCode;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public Integer getScanType() {
        return scanType;
    }

    public void setScanType(Integer scanType) {
        this.scanType = scanType;
    }

    public Integer getScannedCount() {
        return scannedCount;
    }

    public void setScannedCount(Integer scannedCount) {
        this.scannedCount = scannedCount;
    }

    public Integer getBoardCount() {
        return boardCount;
    }

    public void setBoardCount(Integer boardCount) {
        this.boardCount = boardCount;
    }

    public Integer getMoreScannedCount() {
        return moreScannedCount;
    }

    public void setMoreScannedCount(Integer moreScannedCount) {
        this.moreScannedCount = moreScannedCount;
    }

    public Integer getInterceptCount() {
        return interceptCount;
    }

    public void setInterceptCount(Integer interceptCount) {
        this.interceptCount = interceptCount;
    }

    public Integer getWaitScanCount() {
        return waitScanCount;
    }

    public void setWaitScanCount(Integer waitScanCount) {
        this.waitScanCount = waitScanCount;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Boolean getYn() {
        return yn;
    }

    public void setYn(Boolean yn) {
        this.yn = yn;
    }

    public Date getTs() {
        return ts;
    }

    public void setTs(Date ts) {
        this.ts = ts;
    }

    public Integer getPackageScannedCount() {
        return packageScannedCount;
    }

    public void setPackageScannedCount(Integer packageScannedCount) {
        this.packageScannedCount = packageScannedCount;
    }

    public Integer getBoxScannedCount() {
        return boxScannedCount;
    }

    public void setBoxScannedCount(Integer boxScannedCount) {
        this.boxScannedCount = boxScannedCount;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }
}
