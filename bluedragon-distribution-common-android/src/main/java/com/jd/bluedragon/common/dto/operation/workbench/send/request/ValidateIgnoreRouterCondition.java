package com.jd.bluedragon.common.dto.operation.workbench.send.request;

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
    /**
     * 错发过滤流向【已知强发流向】
     */
    private List<Long> receiveSiteIdList;

    private String matchType;
    /**
     * 维护强发关系【未知强发流向】
     * [A-A1,A-A2,B-B2,B-B3]： A1、A2强发A  B1、B2强发B
     */
    private List<ForceSendSiteRelation> forceSendSiteRelationList;

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

    public List<ForceSendSiteRelation> getForceSendSiteRelationList() {
        return forceSendSiteRelationList;
    }

    public void setForceSendSiteRelationList(List<ForceSendSiteRelation> forceSendSiteRelationList) {
        this.forceSendSiteRelationList = forceSendSiteRelationList;
    }
}
