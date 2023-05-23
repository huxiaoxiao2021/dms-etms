package com.jd.bluedragon.distribution.jy.dto.collect;

import java.io.Serializable;

/**
 * @Author zhengchengfa
 * @Description //集齐报表单条统计数据
 * @date
 **/
public class CollectReportDto implements Serializable {

    private static final long serialVersionUID = -6963372061306635997L;

    /**
     * 命中的待集齐集合ID
     */
    private String collectionCode;

    private String waybillCode;
    /**
     * 已卸
     */
    private Integer scanDoNum;
    /**
     * 待卸
     */
    private Integer scanWaitNum;
    /**
     * 未到
     */
    private Integer scanNullNum;
    /**
     * 包裹总数
     */
    private Integer packageNum;
    /**
     * 非本车在库
     */
    private Integer otherInventoryNum;

    /**
     * 货区编码
     */
    private String goodsAreaCode;
    private Integer nextSiteCode;
    /**
     * 在库标识 true: 在库
     */
    private Boolean inventoryFlag;

    public String getCollectionCode() {
        return collectionCode;
    }

    public void setCollectionCode(String collectionCode) {
        this.collectionCode = collectionCode;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Integer getScanDoNum() {
        return scanDoNum;
    }

    public void setScanDoNum(Integer scanDoNum) {
        this.scanDoNum = scanDoNum;
    }

    public Integer getScanWaitNum() {
        return scanWaitNum;
    }

    public void setScanWaitNum(Integer scanWaitNum) {
        this.scanWaitNum = scanWaitNum;
    }

    public Integer getScanNullNum() {
        return scanNullNum;
    }

    public void setScanNullNum(Integer scanNullNum) {
        this.scanNullNum = scanNullNum;
    }

    public Integer getPackageNum() {
        return packageNum;
    }

    public void setPackageNum(Integer packageNum) {
        this.packageNum = packageNum;
    }

    public String getGoodsAreaCode() {
        return goodsAreaCode;
    }

    public void setGoodsAreaCode(String goodsAreaCode) {
        this.goodsAreaCode = goodsAreaCode;
    }

    public Integer getNextSiteCode() {
        return nextSiteCode;
    }

    public void setNextSiteCode(Integer nextSiteCode) {
        this.nextSiteCode = nextSiteCode;
    }

    public Integer getOtherInventoryNum() {
        return otherInventoryNum;
    }

    public void setOtherInventoryNum(Integer otherInventoryNum) {
        this.otherInventoryNum = otherInventoryNum;
    }

    public Boolean getInventoryFlag() {
        return inventoryFlag;
    }

    public void setInventoryFlag(Boolean inventoryFlag) {
        this.inventoryFlag = inventoryFlag;
    }
}
