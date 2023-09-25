package com.jd.bluedragon.common.dto.collectpackage.request;

import com.jd.bluedragon.common.dto.base.request.BaseReq;
import lombok.Getter;

import java.io.Serializable;
public class CollectPackageReq  extends BaseReq implements Serializable {
    private static final long serialVersionUID = 1524434357342779618L;

    private String packageCode;
    private String boxCode;
    private String bizId;

    /**
     * 强制集包（运单路由信息- 不在允许集包的流向集合中时）
     */
    private boolean forceCollectPackage;

    private boolean skipInterceptChain;

    public boolean getSkipInterceptChain() {
        return skipInterceptChain;
    }

    public void setSkipInterceptChain(boolean skipInterceptChain) {
        this.skipInterceptChain = skipInterceptChain;
    }

    public boolean getForceCollectPackage() {
        return forceCollectPackage;
    }

    public void setForceCollectPackage(boolean forceCollectPackage) {
        this.forceCollectPackage = forceCollectPackage;
    }

    private Long endSiteId;

    public Long getEndSiteId() {
        return endSiteId;
    }

    public void setEndSiteId(Long endSiteId) {
        this.endSiteId = endSiteId;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }
}
