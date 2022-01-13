package com.jd.bluedragon.distribution.dock.entity;

import com.jd.ql.dms.common.web.mvc.api.BasePagerCondition;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * 逐渐废弃，改为使用siteCodeList
     * @see DockPageQueryCondition#siteCodeList
     */
    @Deprecated
    private Integer siteCode;

    /**
     * 适配工作台的erp权限，查询部分站点
     */
    private List<Integer> siteCodeList = new ArrayList<Integer>();

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

    public List<Integer> getSiteCodeList() {
        return siteCodeList;
    }

    public void setSiteCodeList(List<Integer> siteCodeList) {
        this.siteCodeList = siteCodeList;
    }
}
