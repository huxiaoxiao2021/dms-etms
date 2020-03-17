package com.jd.bluedragon.distribution.weightAndVolumeCheck;

import com.jd.ql.dms.common.web.mvc.api.DbEntity;

/**
 * @author lijie
 * @date 2020/3/9 20:13
 */
public class SpotCheckExcelData extends DbEntity {

    private static final long serialVersionUID = 1L;

    /** 区域编码 */
    private Integer orgCode;
    /** 机构编码 */
    private Integer siteCode;
    /** 机构名称 */
    private String siteName;
    /** 业务类型 */
    private String spotCheckType;
    /** 普通应抽查包裹数 */
    private Integer normalPackageNum;
    /** 信任商家应抽查包裹数 */
    private Integer trustPackageNum;
    /** 导入人ERP */
    private String importErp;

    public Integer getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(Integer orgCode) {
        this.orgCode = orgCode;
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

    public String getSpotCheckType() {
        return spotCheckType;
    }

    public void setSpotCheckType(String spotCheckType) {
        this.spotCheckType = spotCheckType;
    }

    public Integer getNormalPackageNum() {
        return normalPackageNum;
    }

    public void setNormalPackageNum(Integer normalPackageNum) {
        this.normalPackageNum = normalPackageNum;
    }

    public Integer getTrustPackageNum() {
        return trustPackageNum;
    }

    public void setTrustPackageNum(Integer trustPackageNum) {
        this.trustPackageNum = trustPackageNum;
    }

    public String getImportErp() {
        return importErp;
    }

    public void setImportErp(String importErp) {
        this.importErp = importErp;
    }
}
