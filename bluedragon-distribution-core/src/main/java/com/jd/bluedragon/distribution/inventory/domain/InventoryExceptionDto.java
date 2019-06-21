package com.jd.bluedragon.distribution.inventory.domain;

public class InventoryExceptionDto extends InventoryException {

    /**
     * 机构编号
     */
    private Integer orgId;
    /**
     * 机构名称
     */
    private String orgName;

    /**
     * 始发
     */
    private Integer createSiteCode;

    /**
     * 始发名称
     */
    private String createSiteName;

    /**
     * 任务创建人erp
     */
    private String createUserErp;

    public Integer getOrgId() {
        return orgId;
    }

    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public Integer getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public String getCreateSiteName() {
        return createSiteName;
    }

    public void setCreateSiteName(String createSiteName) {
        this.createSiteName = createSiteName;
    }

    public String getCreateUserErp() {
        return createUserErp;
    }

    public void setCreateUserErp(String createUserErp) {
        this.createUserErp = createUserErp;
    }
}
