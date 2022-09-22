package com.jd.bluedragon.distribution.jsf.domain;

import java.io.Serializable;
import java.util.List;

/**
 * 忽略条件
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2022-09-11 15:23:34 周日
 */
public class ValidateIgnoreRouterCondition implements Serializable {
    private static final long serialVersionUID = 768445654929989093L;

    private List<Long> receiveSiteIdList;

    private String matchType;

    public List<Long> getReceiveSiteIdList() {
        return receiveSiteIdList;
    }

    public void setReceiveSiteIdList(List<Long> receiveSiteIdList) {
        this.receiveSiteIdList = receiveSiteIdList;
    }

    public String getMatchType() {
        return matchType;
    }

    public void setMatchType(String matchType) {
        this.matchType = matchType;
    }
}
