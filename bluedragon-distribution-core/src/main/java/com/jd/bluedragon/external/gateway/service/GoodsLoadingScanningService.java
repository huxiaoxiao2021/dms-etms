package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.GoodsLoadingReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.GoodsLoadingScanningReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.response.GoodsLoadingScanningDto;

import java.util.List;

/**
 * 货物装车扫描相关
 * 发布到物流网关 由安卓调用
 */
public interface GoodsLoadingScanningService {


    /**
     * 货物取消扫描接口：入参： 任务号 、包裹号
     * @param req
     * @return
     */
    JdCResponse goodsRemoveScanning(GoodsLoadingScanningReq req);

    /**
     * 货物强制下发接口：入参：任务号、list<运单号>
     * @param req
     * @return
     */
    JdCResponse goodsCompulsoryDeliver(GoodsLoadingScanningReq req);

    /**
     * 查询货物不齐异常运单数据 入参：任务号
     * @param req
     * @return
     */
    JdCResponse<List<GoodsLoadingScanningDto>> findExceptionGoodsLoading(GoodsLoadingScanningReq req);

    /**
     * 装车发货
     * @param req
     * @return
     */
    JdCResponse goodsLoading(GoodsLoadingReq req);


}
