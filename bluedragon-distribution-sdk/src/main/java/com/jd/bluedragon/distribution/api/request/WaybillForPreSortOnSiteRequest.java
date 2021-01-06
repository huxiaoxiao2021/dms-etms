package com.jd.bluedragon.distribution.api.request;

import com.jd.bluedragon.distribution.api.JdRequest;

public class WaybillForPreSortOnSiteRequest extends JdRequest {
    //erp
    private String erp;

    //运单
    private String waybill;

    //分拣中心
    private Integer sortingSite;

    //现场调度站点
    private Integer siteOfSchedulingOnSite;


    public String getErp() {
        return erp;
    }

    public void setErp(String erp) {
        this.erp = erp;
    }

    public String getWaybill() {
        return waybill;
    }

    public void setWaybill(String waybill) {
        this.waybill = waybill;
    }

    public Integer getSortingSite() {
        return sortingSite;
    }

    public void setSortingSite(Integer sortingSite) {
        this.sortingSite = sortingSite;
    }

    public Integer getSiteOfSchedulingOnSite() {
        return siteOfSchedulingOnSite;
    }

    public void setSiteOfSchedulingOnSite(Integer siteOfSchedulingOnSite) {
        this.siteOfSchedulingOnSite = siteOfSchedulingOnSite;
    }
}
