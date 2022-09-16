package com.jd.bluedragon.distribution.jy.dto.unload;

import java.io.Serializable;
import java.util.Map;

public class ScanPackageRespDto implements Serializable {
    private static final long serialVersionUID = -6963372061306635997L;
    private String bizId;
    /**
     * 子阶段卸车任务
     */
    private String stageBizId;
    /**
     * 货区编码
     */
    private String goodsAreaCode;
    /**
     * 板号
     */
    private String boardCode;
    private String endSiteName;
    private Long endSiteId;
    private Integer comBoardCount;

    private String prevSiteName;
    private Long prevSiteId;

    /**
     * 本次扫描成功的包裹数
     */
    private Integer packageAmount;

    /**
     * 本次扫描成功的运单数
     */
    private Integer waybillAmount;

    /**
     * 扫描的号码
     */
    private String barCode;

    /**
     * 补扫标识
     */
    private boolean supplementary;

    /**
     * 是否是首次扫描
     */
    private boolean firstScan;

    /**
     * 是否是子阶段首次扫描
     */
    private boolean stageFirstScan;

    /**
     * 卸车扫描非弹框消息提示  K-优先级  V-话术
     * 见：UnloadCarWarnEnum
     */
    Map<String, String> warnMsg;

    Map<String, String> confirmMsg;

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getStageBizId() {
        return stageBizId;
    }

    public void setStageBizId(String stageBizId) {
        this.stageBizId = stageBizId;
    }

    public String getGoodsAreaCode() {
        return goodsAreaCode;
    }

    public void setGoodsAreaCode(String goodsAreaCode) {
        this.goodsAreaCode = goodsAreaCode;
    }

    public String getBoardCode() {
        return boardCode;
    }

    public void setBoardCode(String boardCode) {
        this.boardCode = boardCode;
    }

    public String getEndSiteName() {
        return endSiteName;
    }

    public void setEndSiteName(String endSiteName) {
        this.endSiteName = endSiteName;
    }

    public Long getEndSiteId() {
        return endSiteId;
    }

    public void setEndSiteId(Long endSiteId) {
        this.endSiteId = endSiteId;
    }

    public Integer getComBoardCount() {
        return comBoardCount;
    }

    public void setComBoardCount(Integer comBoardCount) {
        this.comBoardCount = comBoardCount;
    }

    public Map<String, String> getWarnMsg() {
        return warnMsg;
    }

    public void setWarnMsg(Map<String, String> warnMsg) {
        this.warnMsg = warnMsg;
    }

    public Map<String, String> getConfirmMsg() {
        return confirmMsg;
    }

    public void setConfirmMsg(Map<String, String> confirmMsg) {
        this.confirmMsg = confirmMsg;
    }

    public String getPrevSiteName() {
        return prevSiteName;
    }

    public void setPrevSiteName(String prevSiteName) {
        this.prevSiteName = prevSiteName;
    }

    public Long getPrevSiteId() {
        return prevSiteId;
    }

    public void setPrevSiteId(Long prevSiteId) {
        this.prevSiteId = prevSiteId;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public boolean isSupplementary() {
        return supplementary;
    }

    public void setSupplementary(boolean supplementary) {
        this.supplementary = supplementary;
    }

    public boolean isFirstScan() {
        return firstScan;
    }

    public void setFirstScan(boolean firstScan) {
        this.firstScan = firstScan;
    }

    public boolean isStageFirstScan() {
        return stageFirstScan;
    }

    public void setStageFirstScan(boolean stageFirstScan) {
        this.stageFirstScan = stageFirstScan;
    }

    public Integer getPackageAmount() {
        return packageAmount;
    }

    public void setPackageAmount(Integer packageAmount) {
        this.packageAmount = packageAmount;
    }

    public Integer getWaybillAmount() {
        return waybillAmount;
    }

    public void setWaybillAmount(Integer waybillAmount) {
        this.waybillAmount = waybillAmount;
    }
}
