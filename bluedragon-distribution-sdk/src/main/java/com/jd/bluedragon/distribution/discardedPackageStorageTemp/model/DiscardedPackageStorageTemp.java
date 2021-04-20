package com.jd.bluedragon.distribution.discardedPackageStorageTemp.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Description: 快递弃件暂存<br>
 * Copyright: Copyright (c) 2020<br>
 * Company: jd.com 京东物流JDL<br>
 * 
 * @author fanggang7
 * @time 2021-03-31 11:32:59 周三
 */
public class DiscardedPackageStorageTemp implements Serializable{

    private static final long serialVersionUID = -3635180133815548634L;

    //columns START
    /**
     * 主键，雪花计算值  db_column: id
     */
    private Long id;
    /**
     * 运单号  db_column: waybill_code
     */
    private String waybillCode;
    /**
     * 包裹号  db_column: package_code
     */
    private String packageCode;
    /**
     * 状态 0 弃件暂存 1 弃件出库 2 已认领  db_column: status
     */
    private Integer status;
    /**
     * 快递产品类型  db_column: waybill_product
     */
    private String waybillProduct;
    /**
     * 托寄物名称  db_column: consignment_name
     */
    private String consignmentName;
    /**
     * 重量 单位KG  db_column: weight
     */
    private BigDecimal weight;
    /**
     * 是否cod 0 不是cod 1 是cod  db_column: cod
     */
    private Integer cod;
    /**
     * cod代收款金额 单位元  db_column: cod_amount
     */
    private String codAmount;
    /**
     * 商家编码  db_column: business_code
     */
    private String businessCode;
    /**
     * 商家名称  db_column: business_name
     */
    private String businessName;
    /**
     * 操作人ID  db_column: operator_code
     */
    private Long operatorCode;
    /**
     * 操作人erp  db_column: operator_erp
     */
    private String operatorErp;
    /**
     * 操作人姓名  db_column: operator_name
     */
    private String operatorName;
    /**
     * 操作站点ID  db_column: site_code
     */
    private Integer siteCode;
    /**
     * 操作站点名称  db_column: site_name
     */
    private String siteName;
    /**
     * 操作站点所在城市  db_column: site_city
     */
    private String siteCity;
    /**
     * 操作站点所在大区  db_column: org_code
     */
    private Integer orgCode;
    /**
     * 操作站点所在大区名称  db_column: org_name
     */
    private String orgName;
    /**
     * 上一级操作站点ID  db_column: prev_site_code
     */
    private Integer prevSiteCode;
    /**
     * 上一级操作站点ID名称  db_column: prev_site_name
     */
    private String prevSiteName;
    /**
     * 上一级操作站点所在战区  db_column: prev_province_company_code
     */
    private String prevProvinceCompanyCode;
    /**
     * 上一级操作站点所在战区名称  db_column: prev_province_company_name
     */
    private String prevProvinceCompanyName;
    /**
     * 创建时间  db_column: create_time
     */
    private Date createTime;
    /**
     * 更新时间  db_column: update_time
     */
    private Date updateTime;
    /**
     * 数据状态 1 有效 0 无效  db_column: yn
     */
    private Integer yn;
    /**
     * 时间戳  db_column: ts
     */
    private Date ts;
    //columns END

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getWaybillProduct() {
        return waybillProduct;
    }

    public void setWaybillProduct(String waybillProduct) {
        this.waybillProduct = waybillProduct;
    }

    public String getConsignmentName() {
        return consignmentName;
    }

    public void setConsignmentName(String consignmentName) {
        this.consignmentName = consignmentName;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public Integer getCod() {
        return cod;
    }

    public void setCod(Integer cod) {
        this.cod = cod;
    }

    public String getCodAmount() {
        return codAmount;
    }

    public void setCodAmount(String codAmount) {
        this.codAmount = codAmount;
    }

    public String getBusinessCode() {
        return businessCode;
    }

    public void setBusinessCode(String businessCode) {
        this.businessCode = businessCode;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public Long getOperatorCode() {
        return operatorCode;
    }

    public void setOperatorCode(Long operatorCode) {
        this.operatorCode = operatorCode;
    }

    public String getOperatorErp() {
        return operatorErp;
    }

    public void setOperatorErp(String operatorErp) {
        this.operatorErp = operatorErp;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

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

    public String getSiteCity() {
        return siteCity;
    }

    public void setSiteCity(String siteCity) {
        this.siteCity = siteCity;
    }

    public Integer getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(Integer orgCode) {
        this.orgCode = orgCode;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public Integer getPrevSiteCode() {
        return prevSiteCode;
    }

    public void setPrevSiteCode(Integer prevSiteCode) {
        this.prevSiteCode = prevSiteCode;
    }

    public String getPrevSiteName() {
        return prevSiteName;
    }

    public void setPrevSiteName(String prevSiteName) {
        this.prevSiteName = prevSiteName;
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

    public Integer getYn() {
        return yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }

    public Date getTs() {
        return ts;
    }

    public void setTs(Date ts) {
        this.ts = ts;
    }

    public String getPrevProvinceCompanyCode() {
        return prevProvinceCompanyCode;
    }

    public void setPrevProvinceCompanyCode(String prevProvinceCompanyCode) {
        this.prevProvinceCompanyCode = prevProvinceCompanyCode;
    }

    public String getPrevProvinceCompanyName() {
        return prevProvinceCompanyName;
    }

    public void setPrevProvinceCompanyName(String prevProvinceCompanyName) {
        this.prevProvinceCompanyName = prevProvinceCompanyName;
    }

    @Override
    public String toString() {
        return "DiscardedPackageStorageTemp{" +
                "id=" + id +
                ", waybillCode='" + waybillCode + '\'' +
                ", packageCode='" + packageCode + '\'' +
                ", status=" + status +
                ", waybillProduct='" + waybillProduct + '\'' +
                ", consignmentName='" + consignmentName + '\'' +
                ", weight=" + weight +
                ", cod=" + cod +
                ", codAmount=" + codAmount +
                ", businessCode='" + businessCode + '\'' +
                ", businessName='" + businessName + '\'' +
                ", operatorCode=" + operatorCode +
                ", operatorErp='" + operatorErp + '\'' +
                ", operatorName='" + operatorName + '\'' +
                ", siteCode=" + siteCode +
                ", siteName='" + siteName + '\'' +
                ", siteCity='" + siteCity + '\'' +
                ", orgCode=" + orgCode +
                ", orgName='" + orgName + '\'' +
                ", prevSiteCode=" + prevSiteCode +
                ", prevSiteName='" + prevSiteName + '\'' +
                ", prevProvinceCompanyCode=" + prevProvinceCompanyCode +
                ", prevProvinceCompanyName='" + prevProvinceCompanyName + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", yn=" + yn +
                ", ts=" + ts +
                '}';
    }
}
