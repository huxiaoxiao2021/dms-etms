package com.jd.bluedragon.distribution.router.dto.request;

import com.jd.dms.java.utils.sdk.base.Page;

import java.io.Serializable;

/**
 * Description: <br>
 * Copyright: Copyright (c) 2020<br>
 * Company: jd.com 京东物流JDL<br>
 * 
 * @author fanggang7
 * @time 2024-04-01 19:33:04 周一
 */
public class RouterDynamicLineReplacePlanLogQuery extends Page implements Serializable {
    private static final long serialVersionUID = -908677990726708892L;

    private Integer siteId;

    private Long refId;

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Long getRefId() {
        return refId;
    }

    public void setRefId(Long refId) {
        this.refId = refId;
    }
}