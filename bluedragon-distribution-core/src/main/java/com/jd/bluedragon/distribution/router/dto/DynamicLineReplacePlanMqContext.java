package com.jd.bluedragon.distribution.router.dto;

import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;

import java.io.Serializable;
import java.util.Date;

/**
 * 动态线路替换方案
 * @author fanggang7
 * @date 2024-04-01 09:43:26 周一
 */
public class DynamicLineReplacePlanMqContext implements Serializable {

    private static final long serialVersionUID = -4544720569089863907L;

    private DynamicLineReplacePlanMq dynamicLineReplacePlanMq;

    private BaseStaffSiteOrgDto baseSiteStart;
    private BaseStaffSiteOrgDto baseSiteEndOld;
    private BaseStaffSiteOrgDto baseSiteEndNew;

    public DynamicLineReplacePlanMq getDynamicLineReplacePlanMq() {
        return dynamicLineReplacePlanMq;
    }

    public void setDynamicLineReplacePlanMq(DynamicLineReplacePlanMq dynamicLineReplacePlanMq) {
        this.dynamicLineReplacePlanMq = dynamicLineReplacePlanMq;
    }

    public BaseStaffSiteOrgDto getBaseSiteStart() {
        return baseSiteStart;
    }

    public void setBaseSiteStart(BaseStaffSiteOrgDto baseSiteStart) {
        this.baseSiteStart = baseSiteStart;
    }

    public BaseStaffSiteOrgDto getBaseSiteEndOld() {
        return baseSiteEndOld;
    }

    public void setBaseSiteEndOld(BaseStaffSiteOrgDto baseSiteEndOld) {
        this.baseSiteEndOld = baseSiteEndOld;
    }

    public BaseStaffSiteOrgDto getBaseSiteEndNew() {
        return baseSiteEndNew;
    }

    public void setBaseSiteEndNew(BaseStaffSiteOrgDto baseSiteEndNew) {
        this.baseSiteEndNew = baseSiteEndNew;
    }
}
