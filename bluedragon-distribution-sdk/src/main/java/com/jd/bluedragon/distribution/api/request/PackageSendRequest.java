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

    /**
     * 是否取消上次发货，false - 不取消（默认），true - 取消上次发货
     */
    private Boolean isCancelLastSend;

    /**
     * 业务来源
     */
    private Integer bizSource;

    public boolean getIsForceSend() {
        return isForceSend;
    }

    public void setIsForceSend(boolean isForceSend) {
        this.isForceSend = isForceSend;
    }

    public Boolean getIsCancelLastSend() {
        return isCancelLastSend;
    }

    public void setIsCancelLastSend(Boolean cancelLastSend) {
        isCancelLastSend = cancelLastSend;
    }

    public Integer getBizSource() {
        return bizSource;
    }

    public void setBizSource(Integer bizSource) {
        this.bizSource = bizSource;
    }
}
