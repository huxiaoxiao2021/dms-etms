package com.jd.bluedragon.distribution.api.request;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by jinjingcheng on 2018/3/7.
 */
public class GenerateSendCodeRequest implements Serializable {
    private static final long serialVersionUID = -9133621094731793516L;
    private long createSiteCode;
    private long receiveSiteCode;
    private Date time;

    public long getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(long createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public long getReceiveSiteCode() {
        return receiveSiteCode;
    }

    public void setReceiveSiteCode(long receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
