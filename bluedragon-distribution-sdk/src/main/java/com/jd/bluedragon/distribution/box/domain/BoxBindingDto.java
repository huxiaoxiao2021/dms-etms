package com.jd.bluedragon.distribution.box.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName BoxBindingDto
 * @Description
 * @Author wyh
 * @Date 2020/12/23 18:29
 **/
public class BoxBindingDto implements Serializable {

    private static final long serialVersionUID = -1366204586500694657L;

    /**
     * 箱号
     */
    private String boxCode;

    /**
     * 关联箱号
     */
    private String relationBoxCode;

    /**
     * 区域
     */
    private String orgName;

    /**
     * 分拣中心
     */
    private Long siteCode;

    /**
     * 分拣中心名称
     */
    private String siteName;

    /**
     * erp
     */
    private String userErp;

    /**
     * 创建时间
     */
    private Date createTime;

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getRelationBoxCode() {
        return relationBoxCode;
    }

    public void setRelationBoxCode(String relationBoxCode) {
        this.relationBoxCode = relationBoxCode;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public Long getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Long siteCode) {
        this.siteCode = siteCode;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getUserErp() {
        return userErp;
    }

    public void setUserErp(String userErp) {
        this.userErp = userErp;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
