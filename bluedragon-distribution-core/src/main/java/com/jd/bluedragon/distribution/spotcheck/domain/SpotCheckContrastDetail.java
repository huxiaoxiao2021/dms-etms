package com.jd.bluedragon.distribution.spotcheck.domain;

import java.io.Serializable;

/**
 * 抽检核对数据
 *
 * @author hujiping
 * @date 2021/8/10 2:17 下午
 */
public class SpotCheckContrastDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 核对来源
     */
    private Integer contrastSourceFrom;
    /**
     * 核对重量kg
     */
    private Double contrastWeight;
    /**
     * 核对体积cm³
     */
    private Double contrastVolume;
    /**
     * 核对体积重量
     */
    private Double contrastVolumeWeight;
    /**
     * 核对较大值
     */
    private Double contrastLarge;
    /**
     * 计费结算重量
     */
    private Double billingCalcWeight;
    /**
     * 核对区域ID
     */
    private Integer contrastOrgId;
    /**
     * 核对区域名称
     */
    private String contrastOrgName;
    /**
     * 核对操作战区编码
     */
    private String contrastWarZoneCode;
    /**
     * 核对操作战区名称
     */
    private String contrastWarZoneName;
    /**
     * 核对操作片区编码
     */
    private String contrastAreaCode;
    /**
     * 核对操作片区名称
     */
    private String contrastAreaName;
    /**
     * 核对片区编码
     */
    private String contrastDeptCode;
    /**
     * 核对片区
     */
    private String contrastDeptName;
    /**
     * 核对站点ID
     */
    private Integer contrastSiteCode;
    /**
     * 核对站点名称
     */
    private String contrastSiteName;
    /**
     * 核对操作人ERP
     */
    private String contrastOperateUserErp;
    /**
     * 核对操作人姓名
     */
    private String contrastOperateUserName;
    /**
     * 核对操作人账号类型
     */
    private Integer contrastOperateUserAccountType;
    /**
     * 责任类型
     */
    private Integer dutyType;
    /**
     * 责任一级ID
     */
    private String dutyFirstId;
    private String dutyFirstName;
    /**
     * 责任二级ID
     */
    private String dutySecondId;
    private String dutySecondName;
    /**
     * 责任三级ID
     */
    private String dutyThirdId;
    private String dutyThirdName;

    /**
     * 核对省区编码
     */
    private String contrastProvinceAgencyCode;
    /**
     * 核对省区名称
     */
    private String contrastProvinceAgencyName;

    public Integer getContrastSourceFrom() {
        return contrastSourceFrom;
    }

    public void setContrastSourceFrom(Integer contrastSourceFrom) {
        this.contrastSourceFrom = contrastSourceFrom;
    }

    public Double getContrastWeight() {
        return contrastWeight;
    }

    public void setContrastWeight(Double contrastWeight) {
        this.contrastWeight = contrastWeight;
    }

    public Double getContrastVolume() {
        return contrastVolume;
    }

    public void setContrastVolume(Double contrastVolume) {
        this.contrastVolume = contrastVolume;
    }

    public Double getContrastVolumeWeight() {
        return contrastVolumeWeight;
    }

    public void setContrastVolumeWeight(Double contrastVolumeWeight) {
        this.contrastVolumeWeight = contrastVolumeWeight;
    }

    public Double getContrastLarge() {
        return contrastLarge;
    }

    public void setContrastLarge(Double contrastLarge) {
        this.contrastLarge = contrastLarge;
    }

    public Double getBillingCalcWeight() {
        return billingCalcWeight;
    }

    public void setBillingCalcWeight(Double billingCalcWeight) {
        this.billingCalcWeight = billingCalcWeight;
    }

    public Integer getContrastOrgId() {
        return contrastOrgId;
    }

    public void setContrastOrgId(Integer contrastOrgId) {
        this.contrastOrgId = contrastOrgId;
    }

    public String getContrastOrgName() {
        return contrastOrgName;
    }

    public void setContrastOrgName(String contrastOrgName) {
        this.contrastOrgName = contrastOrgName;
    }

    public String getContrastProvinceAgencyCode() {
        return contrastProvinceAgencyCode;
    }

    public void setContrastProvinceAgencyCode(String contrastProvinceAgencyCode) {
        this.contrastProvinceAgencyCode = contrastProvinceAgencyCode;
    }

    public String getContrastProvinceAgencyName() {
        return contrastProvinceAgencyName;
    }

    public void setContrastProvinceAgencyName(String contrastProvinceAgencyName) {
        this.contrastProvinceAgencyName = contrastProvinceAgencyName;
    }

    public String getContrastWarZoneCode() {
        return contrastWarZoneCode;
    }

    public void setContrastWarZoneCode(String contrastWarZoneCode) {
        this.contrastWarZoneCode = contrastWarZoneCode;
    }

    public String getContrastWarZoneName() {
        return contrastWarZoneName;
    }

    public void setContrastWarZoneName(String contrastWarZoneName) {
        this.contrastWarZoneName = contrastWarZoneName;
    }

    public String getContrastAreaCode() {
        return contrastAreaCode;
    }

    public void setContrastAreaCode(String contrastAreaCode) {
        this.contrastAreaCode = contrastAreaCode;
    }

    public String getContrastAreaName() {
        return contrastAreaName;
    }

    public void setContrastAreaName(String contrastAreaName) {
        this.contrastAreaName = contrastAreaName;
    }

    public String getContrastDeptCode() {
        return contrastDeptCode;
    }

    public void setContrastDeptCode(String contrastDeptCode) {
        this.contrastDeptCode = contrastDeptCode;
    }

    public String getContrastDeptName() {
        return contrastDeptName;
    }

    public void setContrastDeptName(String contrastDeptName) {
        this.contrastDeptName = contrastDeptName;
    }

    public Integer getContrastSiteCode() {
        return contrastSiteCode;
    }

    public void setContrastSiteCode(Integer contrastSiteCode) {
        this.contrastSiteCode = contrastSiteCode;
    }

    public String getContrastSiteName() {
        return contrastSiteName;
    }

    public void setContrastSiteName(String contrastSiteName) {
        this.contrastSiteName = contrastSiteName;
    }

    public String getContrastOperateUserErp() {
        return contrastOperateUserErp;
    }

    public void setContrastOperateUserErp(String contrastOperateUserErp) {
        this.contrastOperateUserErp = contrastOperateUserErp;
    }

    public String getContrastOperateUserName() {
        return contrastOperateUserName;
    }

    public void setContrastOperateUserName(String contrastOperateUserName) {
        this.contrastOperateUserName = contrastOperateUserName;
    }

    public Integer getContrastOperateUserAccountType() {
        return contrastOperateUserAccountType;
    }

    public void setContrastOperateUserAccountType(Integer contrastOperateUserAccountType) {
        this.contrastOperateUserAccountType = contrastOperateUserAccountType;
    }

    public Integer getDutyType() {
        return dutyType;
    }

    public void setDutyType(Integer dutyType) {
        this.dutyType = dutyType;
    }

    public String getDutyFirstId() {
        return dutyFirstId;
    }

    public void setDutyFirstId(String dutyFirstId) {
        this.dutyFirstId = dutyFirstId;
    }

    public String getDutyFirstName() {
        return dutyFirstName;
    }

    public void setDutyFirstName(String dutyFirstName) {
        this.dutyFirstName = dutyFirstName;
    }

    public String getDutySecondId() {
        return dutySecondId;
    }

    public void setDutySecondId(String dutySecondId) {
        this.dutySecondId = dutySecondId;
    }

    public String getDutySecondName() {
        return dutySecondName;
    }

    public void setDutySecondName(String dutySecondName) {
        this.dutySecondName = dutySecondName;
    }

    public String getDutyThirdId() {
        return dutyThirdId;
    }

    public void setDutyThirdId(String dutyThirdId) {
        this.dutyThirdId = dutyThirdId;
    }

    public String getDutyThirdName() {
        return dutyThirdName;
    }

    public void setDutyThirdName(String dutyThirdName) {
        this.dutyThirdName = dutyThirdName;
    }
}
