package com.jd.bluedragon.distribution.api.request;

/**
 * Created by yangbo7 on 2016/6/22.
 */
public class CleanBoxCacheRequest {


    private static final long serialVersionUID = 5799267676878153722L;


    private String createSiteCode;

    private String siteCode;


    public CleanBoxCacheRequest() {
    }

    public String getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(String createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }
}
