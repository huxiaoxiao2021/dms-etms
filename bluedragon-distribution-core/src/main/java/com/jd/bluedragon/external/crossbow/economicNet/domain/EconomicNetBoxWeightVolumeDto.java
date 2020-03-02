package com.jd.bluedragon.external.crossbow.economicNet.domain;

/**
 * <p>
 *     经济网按箱称重上传处理对象
 *
 * @author wuzuxiang
 * @since 2020/1/16
 **/
public class EconomicNetBoxWeightVolumeDto {

    /**
     * 记录编号(必须唯一，时间字符串（yyyyMMddHHmmss）+6位随机数
     * 为什么不自己生成呢...
     */
    private String id;

    /**
     * 箱号
     */
    private String bagCode;

    /**
     * 扫描类型（默认值：包裹称重扫描）
     */
    private String scanType;

    /**
     * 扫描时间(yyyy-mm-dd hh24:mi:ss)
     */
    private String scanDate;

    /**
     * 扫描网点
     */
    private String scanSite;

    /**
     * 扫描网点编号
     */
    private String scanSiteCode;

    /**
     * 扫描人
     */
    private String scanMan;

    /**
     * 重量 两位小数 单位KG
     */
    private String weight;

    /**
     * 长度 两位小数 单位CM
     */
    private String length;

    /**
     * 宽度 两位小数 单位CM
     */
    private String width;

    /**
     * 高度 两位小数 单位CM
     */
    private String height;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBagCode() {
        return bagCode;
    }

    public void setBagCode(String bagCode) {
        this.bagCode = bagCode;
    }

    public String getScanType() {
        return scanType;
    }

    public void setScanType(String scanType) {
        this.scanType = scanType;
    }

    public String getScanDate() {
        return scanDate;
    }

    public void setScanDate(String scanDate) {
        this.scanDate = scanDate;
    }

    public String getScanSite() {
        return scanSite;
    }

    public void setScanSite(String scanSite) {
        this.scanSite = scanSite;
    }

    public String getScanSiteCode() {
        return scanSiteCode;
    }

    public void setScanSiteCode(String scanSiteCode) {
        this.scanSiteCode = scanSiteCode;
    }

    public String getScanMan() {
        return scanMan;
    }

    public void setScanMan(String scanMan) {
        this.scanMan = scanMan;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }
}
