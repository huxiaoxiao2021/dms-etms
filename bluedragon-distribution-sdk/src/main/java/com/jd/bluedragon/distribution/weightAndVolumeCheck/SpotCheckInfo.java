package com.jd.bluedragon.distribution.weightAndVolumeCheck;

import com.jd.ql.dms.common.web.mvc.api.DbEntity;

/**
 * @ClassName: SpotCheckInfo
 * @Description: 类描述信息
 * @author: hujiping
 * @date: 2019/4/23 11:17
 */
public class SpotCheckInfo extends DbEntity {

    private static final long serialVersionUID = 1L;

    /** 机构编码 */
    private Integer siteCode;
    /** 机构名称 */
    private String siteName;
    /** 普通应抽查包裹数 */
    private Integer normalPackageNum;
    /** 信任商家应抽查包裹数 */
    private Integer trustPackageNum;
    /** 导入人ERP */
    private String importErp;

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
