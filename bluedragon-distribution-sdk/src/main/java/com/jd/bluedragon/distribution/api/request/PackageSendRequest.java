package com.jd.bluedragon.distribution.api.request;


/**
 * 原包发货对象
 * Created by wangtingwei on 2015/3/11.
 */
public class PackageSendRequest extends DeliveryRequest {

    private static final long serialVersionUID = 8464241458768408318L;

    /**
     * 是否强制发货
     */
    private boolean isForceSend;

    public boolean getIsForceSend() {
        return isForceSend;
    }

    public void setIsForceSend(boolean isForceSend) {
        this.isForceSend = isForceSend;
    }
}
