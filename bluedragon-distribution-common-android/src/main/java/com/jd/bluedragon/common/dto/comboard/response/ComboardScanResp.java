package com.jd.bluedragon.common.dto.comboard.response;

import java.io.Serializable;

public class ComboardScanResp implements Serializable {

    /**
     * 目的id
     */
    private Integer endSiteId;
    /**
     * 目的名称--营业部
     */
    private Integer endSiteName;
    /**
     * 始发id
     */
    private Integer startSiteId;
    /**
     * 始发明细-分拣中心
     */
    private Integer startSiteName;
    /**
     * 扫描单号
     */
    private String barCode;
    /**
     *单号类型，参见BarCodeType
     */
    private Integer barCodeType;

    /**
     * 扫描件数（包裹或者箱子，扫描运单要返回包裹数量）
     */
    private Integer scanDetailCount;
    /**
     * 混扫任务编号
     */
    private String templateCode;
    /**
     * 板号
     */
    private String boardCode;


    public Integer getEndSiteId() {
        return endSiteId;
    }

    public void setEndSiteId(Integer endSiteId) {
        this.endSiteId = endSiteId;
    }

    public Integer getEndSiteName() {
        return endSiteName;
    }

    public void setEndSiteName(Integer endSiteName) {
        this.endSiteName = endSiteName;
    }

    public Integer getStartSiteId() {
        return startSiteId;
    }

    public void setStartSiteId(Integer startSiteId) {
        this.startSiteId = startSiteId;
    }

    public Integer getStartSiteName() {
        return startSiteName;
    }

    public void setStartSiteName(Integer startSiteName) {
        this.startSiteName = startSiteName;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public String getBoardCode() {
        return boardCode;
    }

    public void setBoardCode(String boardCode) {
        this.boardCode = boardCode;
    }

    public Integer getBarCodeType() {
        return barCodeType;
    }

    public void setBarCodeType(Integer barCodeType) {
        this.barCodeType = barCodeType;
    }

    public Integer getScanDetailCount() {
        return scanDetailCount;
    }

    public void setScanDetailCount(Integer scanDetailCount) {
        this.scanDetailCount = scanDetailCount;
    }
}
