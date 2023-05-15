package com.jd.bluedragon.distribution.reverse.domain;

import java.util.List;

/**
 * @author liwenji
 * @description
 * @date 2022-08-31 18:28
 */
public class ExchangeWaybillQuery extends ExchangeWaybillDto{
    /**
     * 不需要微笑字段
     */
    private List<String> hideInfo;

    /**
     * 拦截校验
     */
    private Boolean needCheckRevokeIntercept;
    
    public List<String> getHideInfo() {
        return hideInfo;
    }

    public void setHideInfo(List<String> hideInfo) {
        this.hideInfo = hideInfo;
    }

    public Boolean getNeedCheckRevokeIntercept() {
        return needCheckRevokeIntercept;
    }

    public void setNeedCheckRevokeIntercept(Boolean needCheckRevokeIntercept) {
        this.needCheckRevokeIntercept = needCheckRevokeIntercept;
    }
}
