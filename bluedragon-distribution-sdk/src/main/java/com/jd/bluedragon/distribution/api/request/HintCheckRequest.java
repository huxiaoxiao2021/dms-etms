package com.jd.bluedragon.distribution.api.request;

import com.jd.bluedragon.distribution.api.JdRequest;

/**
 * @author lijie
 * @date 2020/6/18 16:07
 */
public class HintCheckRequest extends JdRequest {

    private String packageCode;

    private int createSiteCode;

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public int getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(int createSiteCode) {
        this.createSiteCode = createSiteCode;
    }
}
