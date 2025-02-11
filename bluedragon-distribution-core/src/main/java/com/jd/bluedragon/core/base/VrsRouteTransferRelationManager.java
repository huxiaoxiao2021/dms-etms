package com.jd.bluedragon.core.base;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.etms.api.common.dto.PageDto;
import com.jd.etms.api.common.enums.RouteProductEnum;
import com.jd.etms.api.common.enums.WaybillRouteEnum;
import com.jd.etms.api.resource.req.AirlineReq;
import com.jd.etms.api.resource.resp.AirLineResp;
import com.jd.etms.api.transferwavemonitor.req.TransferWaveMonitorReq;
import com.jd.etms.api.transferwavemonitor.resp.TransferWaveMonitorDetailResp;
import com.jd.etms.api.transferwavemonitor.resp.TransferWaveMonitorResp;
import com.jd.etms.api.waybillroutelink.resp.WaybillRouteLinkCustDetailResp;

import java.util.Date;
import java.util.List;

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

    /**
     * 获取未发、已到未验单量及相关信息
     */
    PageDto<TransferWaveMonitorResp> getAbnormalTotal(PageDto<TransferWaveMonitorReq> page, TransferWaveMonitorReq parameter);

    /**
     * 批次清零 未发货明细
     *
     * @param page
     * @param waveBusinessId
     * @return
     */
    PageDto<TransferWaveMonitorDetailResp> getNoSendDetail(PageDto<TransferWaveMonitorDetailResp> page, String waveBusinessId);

    /**
     * 批次清零 未验货明细
     *
     * @param page
     * @param waveBusinessId
     * @return
     */
    PageDto<TransferWaveMonitorDetailResp> getArrivedButNoCheckDetail(PageDto<TransferWaveMonitorDetailResp> page, String waveBusinessId);

    /**
     * 批次清零 未验货明细
     *
     * @param waybillCode
     * @param nodeCode
     * @return
     */
    public String queryWaveInfoByWaybillCodeAndNodeCode(String waybillCode, Integer nodeCode);

    /**
     * B网根据始发和目的获取路由信息
     * @param originalDmsCode 始发网点
     * @param destinationDmsCode 目的网点
     * @param routeProduct 产品类型
     * @param predictSendTime 预计发货时间
     * @return
     */
    List<String> loadWaybillRouter(Integer originalDmsCode, Integer destinationDmsCode, RouteProductEnum routeProduct, Date predictSendTime);

    /**
     * 查询路由节点信息
     *
     * @param packageCode 包裹号
     * @param siteCode 站点
     * @param operateType 操作类型
     * @return
     */
    List<WaybillRouteLinkCustDetailResp> waybillRouteLinkQueryCondition(String packageCode, String siteCode,WaybillRouteEnum.RealTimeOperateType operateType);

    /**
     * 获取路由产品类型
     *
     * @param waybillSign
     * @return
     */
    RouteProductEnum obtainRouterProduct(String waybillSign);

    /**
     * 查询航空线路信息
     * @param request
     * @return
     */
    InvokeResult<List<AirLineResp>> queryAirLineByAirLineReq(AirlineReq request);
}

