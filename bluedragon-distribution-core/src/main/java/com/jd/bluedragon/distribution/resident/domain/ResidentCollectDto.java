package com.jd.bluedragon.distribution.resident.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 驻厂揽收
 *
 * @author hujiping
 * @date 2021/10/20 7:58 下午
 */
public class ResidentCollectDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 站点ID
     */
    private Integer siteCode;
    /**
     * 站点名称
     */
    private String siteName;
    /**
     * 操作人ID
     */
    private Integer operateUserId;
    /**
     * 操作人ERP
     */
    private String operateUserCode;
    /**
     * 操作人名称
     */
    private String operateUserName;
    /**
     * 箱号
     */
    private String boxCode;
    /**
     * 单号：运单号/包裹号
     */
    private String barCode;
    /**
     * 重量：kg
     */
    private Double weight;
    /**
     * 长：cm
     */
    private Double length;
    /**
     * 宽：cm
     */
    private Double width;
    /**
     * 高：cm
     */
    private Double height;
    /**
     * 体积
     */
    private Double volume;
    /**
     * 托寄物
     */
    private String consignments;
    /**
     * 打印包裹号
     */
    private String printPackageCode;
    /**
     * 包裹打印时间
     */
    private Long printPackTime;

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public Integer getOperateUserId() {
        return operateUserId;
    }

    public void setOperateUserId(Integer operateUserId) {
        this.operateUserId = operateUserId;
    }

    public String getOperateUserCode() {
        return operateUserCode;
    }

    public void setOperateUserCode(String operateUserCode) {
        this.operateUserCode = operateUserCode;
    }

    public String getOperateUserName() {
        return operateUserName;
    }

    public void setOperateUserName(String operateUserName) {
        this.operateUserName = operateUserName;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }

    public String getConsignments() {
        return consignments;
    }

    public void setConsignments(String consignments) {
        this.consignments = consignments;
    }

    public String getPrintPackageCode() {
        return printPackageCode;
    }

    public void setPrintPackageCode(String printPackageCode) {
        this.printPackageCode = printPackageCode;
    }

    public Long getPrintPackTime() {
        return printPackTime;
    }

    public void setPrintPackTime(Long printPackTime) {
        this.printPackTime = printPackTime;
    }
}
