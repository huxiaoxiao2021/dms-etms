package com.jd.bluedragon.core.base;

import com.jd.etms.api.common.enums.RouteProductEnum;

import java.util.Date;

/**
 * 路由系统的jsf接口，查询路由信息
 *
 * @author tangchunqing
 * @ClassName: VrsRouteTransferRelationManager
 * @Description: 路由系统接口
 * @date 2018年04月09日 14时:56分
 */
public interface VrsRouteTransferRelationManager {
    /**
     * 路由系统的jsf接口，查询路由信息
     *
     * @param startNode
     * @param endNodeCode
     * @param operateTime
     * @param routeProduct
     * @return
     */
    public String queryRecommendRoute(String startNode, String endNodeCode,
                                      Date operateTime, RouteProductEnum routeProduct);

    /**
     * @param configType    配置类型
     * @param bizzType      业务类型
     * @param startSiteNode 始发地7位编码
     * @param toSiteNode    目的地7位编码
     * @param pickUpEndTime 预约揽收截止时间
     * @return
     */
    String queryRoutePredictDate(Integer configType, Integer bizzType, String startSiteNode, String toSiteNode, Date pickUpEndTime);
}
