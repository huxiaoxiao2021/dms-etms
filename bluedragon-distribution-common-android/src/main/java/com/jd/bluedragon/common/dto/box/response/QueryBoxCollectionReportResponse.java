package com.jd.bluedragon.common.dto.box.response;

import java.io.Serializable;

/**
 * 集包异常举报数据
 *
 * @author fanggang7
 * @time 2020-09-23 19:50:40 周三
 */
public class QueryBoxCollectionReportResponse implements Serializable {
    private static final long serialVersionUID = 6623408351010611474L;


    /**
     * packageCode : JD42134231-1-1
     * siteCode : 324234
     * upstreamBoxCode : B23432
     * boxStartId : 1221
     * boxStartSiteName : 北京通州分拣中心
     * boxEndSiteId : 34343
     * boxEndSiteName : 北京顺义分拣中心
     * weight : 234.20
     * length : 12.3
     * width : 21.2
     * height : 32.43
     * reportType : 1
     * reportTypeName : 虚假集包
     */

    private String packageCode;
    private Integer siteCode;
    private String upstreamBoxCode;
    private Integer boxStartId;
    private String boxStartSiteName;
    private Integer boxEndSiteId;
    private String boxEndSiteName;
    private Double weight;
    private Double length;
    private Double width;
    private Double height;
    private Integer reportType;
    private String reportTypeName;

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public String getUpstreamBoxCode() {
        return upstreamBoxCode;
    }

    public void setUpstreamBoxCode(String upstreamBoxCode) {
        this.upstreamBoxCode = upstreamBoxCode;
    }

    public Integer getBoxStartId() {
        return boxStartId;
    }

    public void setBoxStartId(Integer boxStartId) {
        this.boxStartId = boxStartId;
    }

    public String getBoxStartSiteName() {
        return boxStartSiteName;
    }

    public void setBoxStartSiteName(String boxStartSiteName) {
        this.boxStartSiteName = boxStartSiteName;
    }

    public Integer getBoxEndSiteId() {
        return boxEndSiteId;
    }

    public void setBoxEndSiteId(Integer boxEndSiteId) {
        this.boxEndSiteId = boxEndSiteId;
    }

    public String getBoxEndSiteName() {
        return boxEndSiteName;
    }

    public void setBoxEndSiteName(String boxEndSiteName) {
        this.boxEndSiteName = boxEndSiteName;
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

    public Integer getReportType() {
        return reportType;
    }

    public void setReportType(Integer reportType) {
        this.reportType = reportType;
    }

    public String getReportTypeName() {
        return reportTypeName;
    }

    public void setReportTypeName(String reportTypeName) {
        this.reportTypeName = reportTypeName;
    }

    @Override
    public String toString() {
        return "QueryBoxCollectionReportResponse{" +
                "packageCode='" + packageCode + '\'' +
                ", siteCode=" + siteCode +
                ", upstreamBoxCode='" + upstreamBoxCode + '\'' +
                ", boxStartId=" + boxStartId +
                ", boxStartSiteName='" + boxStartSiteName + '\'' +
                ", boxEndSiteId=" + boxEndSiteId +
                ", boxEndSiteName='" + boxEndSiteName + '\'' +
                ", weight='" + weight + '\'' +
                ", length='" + length + '\'' +
                ", width='" + width + '\'' +
                ", height='" + height + '\'' +
                ", reportType=" + reportType +
                ", reportTypeName='" + reportTypeName + '\'' +
                '}';
    }
}
