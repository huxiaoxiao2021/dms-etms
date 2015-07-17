package com.jd.bluedragon.distribution.api.request;

import com.jd.bluedragon.distribution.api.JdRequest;

public class SealBoxRequest extends JdRequest {

    private static final long serialVersionUID = -4900034488418807075L;

    /** 封箱编号 */
    private String sealCode;

    /** 箱号 */
    private String boxCode;

    /** 创建站点编号 */
    private Integer createSiteCode;

    /** 创建站点名称 */
    private String createSiteName;

    /** 收货站点编号 */
    private Integer receiveSiteCode;

    /** 收货站点名称 */
    private Integer receiveSiteName;

    public SealBoxRequest() {
        super();
    }

    public String getSealCode() {
        return this.sealCode;
    }

    public void setSealCode(String sealCode) {
        this.sealCode = sealCode;
    }

    public String getBoxCode() {
        return this.boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public Integer getCreateSiteCode() {
        return this.createSiteCode;
    }

    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public String getCreateSiteName() {
        return this.createSiteName;
    }

    public void setCreateSiteName(String createSiteName) {
        this.createSiteName = createSiteName;
    }

    public Integer getReceiveSiteCode() {
        return this.receiveSiteCode;
    }

    public void setReceiveSiteCode(Integer receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }

    public Integer getReceiveSiteName() {
        return this.receiveSiteName;
    }

    public void setReceiveSiteName(Integer receiveSiteName) {
        this.receiveSiteName = receiveSiteName;
    }

    @Override
    public int hashCode() {
        return 360 + this.boxCode.hashCode() + this.sealCode.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        SealBoxRequest other = (SealBoxRequest) obj;

        if ((this.boxCode == null) || (this.sealCode == null)) {
            return (this.boxCode == other.boxCode) || (this.sealCode == other.sealCode);
        }

        return this.boxCode.equals(other.boxCode) && this.sealCode.equals(other.sealCode);
    }
}
