package com.jd.bluedragon.distribution.spotcheck.domain;


import java.util.Date;

/**
 * 操作人称重流水汇总
 *
 * @author hujiping
 * @date 2021/8/18 10:00 上午
 */
public class WeightVolumeSummary {

    /**
     * 总重量
     * */
    private Double totalWeight;
    /**
     * 总体积
     * */
    private Double totalVolume;
    /**
     * 操作人id
     * */
    private Integer operateId;
    /**
     * 操作人erp
     * */
    private String operateErp;
    /**
     * 操作区域ID
     */
    private Integer operateOrgId;
    /**
     * 操作区域名称
     */
    private String operateOrgName;
    /**
     * 操作片区编码
     */
    private String operateAreaCode;
    /**
     * 操作片区名称
     */
    private String operateAreaName;
    /**
     * 操作站点ID
     * */
    private Integer operateSiteCode;
    /**
     * 操作站点名称
     * */
    private String operateSiteName;
    /**
     * 站点类型
     */
    private Integer siteType;

    public Double getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(Double totalWeight) {
        this.totalWeight = totalWeight;
    }

    public Double getTotalVolume() {
        return totalVolume;
    }

    public void setTotalVolume(Double totalVolume) {
        this.totalVolume = totalVolume;
    }

    public Integer getOperateId() {
        return operateId;
    }

    public void setOperateId(Integer operateId) {
        this.operateId = operateId;
    }

    public String getOperateErp() {
        return operateErp;
    }

    public void setOperateErp(String operateErp) {
        this.operateErp = operateErp;
    }

    public Integer getOperateOrgId() {
        return operateOrgId;
    }

    public void setOperateOrgId(Integer operateOrgId) {
        this.operateOrgId = operateOrgId;
    }

    public String getOperateOrgName() {
        return operateOrgName;
    }

    public void setOperateOrgName(String operateOrgName) {
        this.operateOrgName = operateOrgName;
    }

    public String getOperateAreaCode() {
        return operateAreaCode;
    }

    public void setOperateAreaCode(String operateAreaCode) {
        this.operateAreaCode = operateAreaCode;
    }

    public String getOperateAreaName() {
        return operateAreaName;
    }

    public void setOperateAreaName(String operateAreaName) {
        this.operateAreaName = operateAreaName;
    }

    public Integer getOperateSiteCode() {
        return operateSiteCode;
    }

    public void setOperateSiteCode(Integer operateSiteCode) {
        this.operateSiteCode = operateSiteCode;
    }

    public String getOperateSiteName() {
        return operateSiteName;
    }

    public void setOperateSiteName(String operateSiteName) {
        this.operateSiteName = operateSiteName;
    }

    public Integer getSiteType() {
        return siteType;
    }

    public void setSiteType(Integer siteType) {
        this.siteType = siteType;
    }
}
