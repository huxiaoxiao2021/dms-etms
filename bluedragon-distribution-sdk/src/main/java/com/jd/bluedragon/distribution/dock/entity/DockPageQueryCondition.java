package com.jd.bluedragon.distribution.dock.entity;

import com.jd.ql.dms.common.web.mvc.api.BasePagerCondition;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.dock.entity
 * @ClassName: DockPageQueryCondition
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2021/12/2 10:21
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public class DockPageQueryCondition extends BasePagerCondition {

    private Integer orgId;

    private Integer siteCode;

    public Integer getOrgId() {
        return orgId;
    }

    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }
}
