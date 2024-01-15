package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.res;
//todo laoqingchang
public class SendFlowDto {

    /**
     * 提货场地id
     */
    private Integer pickingSiteId;

    /**
     * 提货场地名称
     */
    private String pickingSiteName;

    /**
     * 流向场地id
     */
    private Integer nextSiteId;

    /**
     * 流向场地名称
     */
    private String nextSiteName;

    /**
     * 批次号
     */
    private String sendCode;

    /**
     * 查询统计数据标识
     */
    private Boolean countFlag;

    /**
     * 待扫
     */
    private Integer waitScanNum;

    /**
     * 已扫
     */
    private Integer haveScannedNum;

    /**
     * 多扫
     */
    private Integer multipleScanNum;

    public Integer getPickingSiteId() {
        return pickingSiteId;
    }

    public void setPickingSiteId(Integer pickingSiteId) {
        this.pickingSiteId = pickingSiteId;
    }

    public String getPickingSiteName() {
        return pickingSiteName;
    }

    public void setPickingSiteName(String pickingSiteName) {
        this.pickingSiteName = pickingSiteName;
    }

    public Integer getNextSiteId() {
        return nextSiteId;
    }

    public void setNextSiteId(Integer nextSiteId) {
        this.nextSiteId = nextSiteId;
    }

    public String getNextSiteName() {
        return nextSiteName;
    }

    public void setNextSiteName(String nextSiteName) {
        this.nextSiteName = nextSiteName;
    }

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }

    public Boolean getCountFlag() {
        return countFlag;
    }

    public void setCountFlag(Boolean countFlag) {
        this.countFlag = countFlag;
    }

    public Integer getWaitScanNum() {
        return waitScanNum;
    }

    public void setWaitScanNum(Integer waitScanNum) {
        this.waitScanNum = waitScanNum;
    }

    public Integer getHaveScannedNum() {
        return haveScannedNum;
    }

    public void setHaveScannedNum(Integer haveScannedNum) {
        this.haveScannedNum = haveScannedNum;
    }

    public Integer getMultipleScanNum() {
        return multipleScanNum;
    }

    public void setMultipleScanNum(Integer multipleScanNum) {
        this.multipleScanNum = multipleScanNum;
    }
}
