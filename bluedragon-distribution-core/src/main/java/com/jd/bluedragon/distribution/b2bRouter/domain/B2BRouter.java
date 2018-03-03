package com.jd.bluedragon.distribution.b2bRouter.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by xumei3 on 2018/2/26.
 */
public class B2BRouter implements Serializable{
    private static final long serialVersionUID = 1L;

    private Integer id;

    /**
     * 始发网点类型
     */
    private Integer originalSiteType;

    /**
     * 始发网点编码
     */
    private Integer originalSiteCode;

    /**
     * 始发网点名称
     */
    private String  originalSiteName;

    /**
     * 目的网点类型
     */
    private Integer destinationSiteType;

    /**
     * 目的网点编码
     */
    private Integer destinationSiteCode;

    /**
     * 目的网点名称
     */
    private String  destinationSiteName;

    /**
     * 完整路由（网点编码链，格式如 始发网点id-中转网点1id-目的网点id)
     */
    private String siteIdFullLine;

    /**
     * 完整路由（网点名称链，格式如 始发网点名称-中转网点1名称-目的网点名称）
     */
    private String siteNameFullLine;

    /**
     * 操作人erp账号
     */
    private String operatorUserErp;

    /**
     * 操作人姓名
     */
    private String operatorUserName;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 有效标识
     */
    private Integer yn;

    /**
     * 以下字段不存储到数据库（用于方便导入功能实现）
     */
    //中转网点1编码
    private Integer transferOneSiteCode;
    //中转网点1名称
    private String transferOneSiteName;

    //中转网点2编码
    private Integer transferTwoSiteCode;
    //中转网点2名称
    private String transferTwoSiteName;

    //中转网点3编码
    private Integer transferThreeSiteCode;
    //中转网点3名称
    private String transferThreeSiteName;

    //中转网点4编码
    private Integer transferFourSiteCode;
    //中转网点4名称
    private String transferFourSiteName;

    //中转网点5编码
    private Integer transferFiveSiteCode;
    //中转网点5名称
    private String transferFiveSiteName;

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getDestinationSiteCode() {
        return destinationSiteCode;
    }

    public void setDestinationSiteCode(Integer destinationSiteCode) {
        this.destinationSiteCode = destinationSiteCode;
    }

    public String getDestinationSiteName() {
        return destinationSiteName;
    }

    public void setDestinationSiteName(String destinationSiteName) {
        this.destinationSiteName = destinationSiteName;
    }

    public Integer getDestinationSiteType() {
        return destinationSiteType;
    }

    public void setDestinationSiteType(Integer destinationSiteType) {
        this.destinationSiteType = destinationSiteType;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOperatorUserErp() {
        return operatorUserErp;
    }

    public void setOperatorUserErp(String operatorUserErp) {
        this.operatorUserErp = operatorUserErp;
    }

    public String getOperatorUserName() {
        return operatorUserName;
    }

    public void setOperatorUserName(String operatorUserName) {
        this.operatorUserName = operatorUserName;
    }

    public Integer getOriginalSiteCode() {
        return originalSiteCode;
    }

    public void setOriginalSiteCode(Integer originalSiteCode) {
        this.originalSiteCode = originalSiteCode;
    }

    public String getOriginalSiteName() {
        return originalSiteName;
    }

    public void setOriginalSiteName(String originalSiteName) {
        this.originalSiteName = originalSiteName;
    }

    public Integer getOriginalSiteType() {
        return originalSiteType;
    }

    public void setOriginalSiteType(Integer originalSiteType) {
        this.originalSiteType = originalSiteType;
    }

    public String getSiteIdFullLine() {
        return siteIdFullLine;
    }

    public void setSiteIdFullLine(String siteIdFullLine) {
        this.siteIdFullLine = siteIdFullLine;
    }

    public String getSiteNameFullLine() {
        return siteNameFullLine;
    }

    public void setSiteNameFullLine(String siteNameFullLine) {
        this.siteNameFullLine = siteNameFullLine;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getYn() {
        return yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }

    public Integer getTransferFiveSiteCode() {
        return transferFiveSiteCode;
    }

    public void setTransferFiveSiteCode(Integer transferFiveSiteCode) {
        this.transferFiveSiteCode = transferFiveSiteCode;
    }

    public String getTransferFiveSiteName() {
        return transferFiveSiteName;
    }

    public void setTransferFiveSiteName(String transferFiveSiteName) {
        this.transferFiveSiteName = transferFiveSiteName;
    }

    public Integer getTransferFourSiteCode() {
        return transferFourSiteCode;
    }

    public void setTransferFourSiteCode(Integer transferFourSiteCode) {
        this.transferFourSiteCode = transferFourSiteCode;
    }

    public String getTransferFourSiteName() {
        return transferFourSiteName;
    }

    public void setTransferFourSiteName(String transferFourSiteName) {
        this.transferFourSiteName = transferFourSiteName;
    }

    public Integer getTransferOneSiteCode() {
        return transferOneSiteCode;
    }

    public void setTransferOneSiteCode(Integer transferOneSiteCode) {
        this.transferOneSiteCode = transferOneSiteCode;
    }

    public String getTransferOneSiteName() {
        return transferOneSiteName;
    }

    public void setTransferOneSiteName(String transferOneSiteName) {
        this.transferOneSiteName = transferOneSiteName;
    }

    public Integer getTransferThreeSiteCode() {
        return transferThreeSiteCode;
    }

    public void setTransferThreeSiteCode(Integer transferThreeSiteCode) {
        this.transferThreeSiteCode = transferThreeSiteCode;
    }

    public String getTransferThreeSiteName() {
        return transferThreeSiteName;
    }

    public void setTransferThreeSiteName(String transferThreeSiteName) {
        this.transferThreeSiteName = transferThreeSiteName;
    }

    public Integer getTransferTwoSiteCode() {
        return transferTwoSiteCode;
    }

    public void setTransferTwoSiteCode(Integer transferTwoSiteCode) {
        this.transferTwoSiteCode = transferTwoSiteCode;
    }

    public String getTransferTwoSiteName() {
        return transferTwoSiteName;
    }

    public void setTransferTwoSiteName(String transferTwoSiteName) {
        this.transferTwoSiteName = transferTwoSiteName;
    }
}
