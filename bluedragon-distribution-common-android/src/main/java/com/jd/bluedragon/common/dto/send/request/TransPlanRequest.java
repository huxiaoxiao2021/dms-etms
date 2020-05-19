package com.jd.bluedragon.common.dto.send.request;

public class TransPlanRequest {
    /**
     * 操作人所属站点编号
     */
    private Integer createSiteCode;

    /**
     * 收货单位编号
     */
    private Integer receiveSiteCode;

    public Integer getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public Integer getReceiveSiteCode() {
        return receiveSiteCode;
    }

    public void setReceiveSiteCode(Integer receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }
}
