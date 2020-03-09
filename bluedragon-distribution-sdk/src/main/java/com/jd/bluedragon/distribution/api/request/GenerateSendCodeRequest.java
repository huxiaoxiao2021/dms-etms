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
    //数量
    private Integer quantity;

    /* 属性： 0：普通  1：生鲜 */
    private String freshProperty;

    /* 用户ERP */
    private String userCode;

    /**
     * 创建来源
     */
    private String fromSource;

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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getFreshProperty() {
        return freshProperty;
    }

    public void setFreshProperty(String freshProperty) {
        this.freshProperty = freshProperty;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getFromSource() {
        return fromSource;
    }

    public void setFromSource(String fromSource) {
        this.fromSource = fromSource;
    }
}
