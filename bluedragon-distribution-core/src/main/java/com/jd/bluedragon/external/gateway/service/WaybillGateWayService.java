package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.waybill.request.WaybillRouterReq;
import com.jd.bluedragon.common.dto.waybill.request.WaybillTrackReq;
import com.jd.bluedragon.common.dto.waybill.request.WaybillTrackResponse;

import java.util.List;

/**
 * 运单相关服务
 * 发布到物流网关 由安卓调用
 * @author : xumigen
 * @date : 2019/7/16
 */
public interface WaybillGateWayService {

    JdCResponse<List<Integer>> getPerAndSfSiteByWaybill(String waybillCode);

    JdCResponse<List<String>> queryPackageCodes(String waybillCode);

    JdCResponse<List<String>> queryWaybillTrackHistory(String erp);

    JdCResponse<List<WaybillTrackResponse>> queryWaybillTrack(WaybillTrackReq waybillTrackReq);


    /**
     * 根据运单号获取路由的下一个节点
     *
     * @param req 运单路由请求对象，包含必要的路由信息
     * @return Integer 下一个站点的编号，如果存在的话；如果已经是最后一个站点，则可能返回null
     * @throws IllegalArgumentException 如果传入的请求对象不满足路由处理的必要条件
     */
    JdCResponse<Integer> getRouterNextSiteByWaybillCode(WaybillRouterReq req);
}
