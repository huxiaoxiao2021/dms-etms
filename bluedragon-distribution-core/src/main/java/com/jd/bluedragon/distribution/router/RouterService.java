package com.jd.bluedragon.distribution.router;

import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;

/**
 * 路由信息
 *
 * @author: hujiping
 * @date: 2019/8/31 16:31
 */
public interface RouterService {

    /**
     * 获取路由下一节点
     * @param siteCode
     * @param waybillCode
     * @return
     */
    BaseStaffSiteOrgDto getRouterNextSite(Integer siteCode, String waybillCode);

}
