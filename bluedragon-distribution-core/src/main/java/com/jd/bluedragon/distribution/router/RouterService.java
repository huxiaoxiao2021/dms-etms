package com.jd.bluedragon.distribution.router;

import com.jd.bluedragon.distribution.jy.dto.common.BoxNextSiteDto;
import com.jd.bluedragon.distribution.router.domain.dto.RouteNextDto;
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

    /**
     * 根据当前网点 匹配下一网点
     * @param siteCode 当前网点
     * @param waybillCode 运单号
     * @return RouteNextDto 包括下一网点和当前网点后续所有网点
     */
    RouteNextDto matchRouterNextNode(Integer siteCode, String waybillCode);

    /**
     * 根据已知路由链路倒序查后续网点及上一网点
     * @param siteCode   必填
     * @param waybillCode   必填
     * @param routerStr    选填，可null
     * @return
     */
    public RouteNextDto matchNextNodeAndLastNodeByRouter(Integer siteCode, String waybillCode, String routerStr);


    /**
     * 根据箱号查询箱号下一流向（随机取箱内三个运单来确定箱的流向）
     * @param curSiteId
     * @param box
     * @return
     */
    BoxNextSiteDto getRouteNextSiteByBox(Integer curSiteId, String box);

    /**
     * 根据运单号查询该运单路由的下一流向场地
     * 【可能为空】
     * @param curSiteId
     * @param waybillCode
     * @return
     */
    BaseStaffSiteOrgDto getRouteNextSiteByWaybillCode(Integer curSiteId, String waybillCode);
}
