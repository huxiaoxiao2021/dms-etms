package com.jd.bluedragon.distribution.api.request;

/**
 * @author lixin39
 * @Description TODO
 * @ClassName TransPlanScheduleRequest
 * @date 2019/4/9
 */
public class TransPlanScheduleRequest {

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
