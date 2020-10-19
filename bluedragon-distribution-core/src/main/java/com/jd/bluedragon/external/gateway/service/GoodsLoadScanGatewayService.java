package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.GoodsLoadingReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.GoodsExceptionScanningReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.GoodsLoadingScanningReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.response.GoodsDetailDto;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.response.GoodsExceptionScanningDto;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.GoodsLoadScan;

import java.util.List;
import java.util.Map;

/**
 * 货物装车扫描相关
 * 发布到物流网关 由安卓调用
 */
public interface GoodsLoadScanGatewayService {


    /**
     * 货物取消扫描接口：入参： 任务号 、运单号、包裹号
     * @param req
     * @return
     * @Author zhengchengfa
     */
    JdCResponse goodsRemoveScanning(GoodsExceptionScanningReq req);

    /**
     * 货物强制下发接口：入参：任务号、list<运单号>
     * @param req
     * @return
     * @Author zhengchengfa
     */
    JdCResponse goodsCompulsoryDeliver(GoodsExceptionScanningReq req);

    /**
     * 查询货物不齐异常运单数据 入参：任务号
     * @param req
     * @return
     * @Author zhengchengfa
     */
    JdCResponse<List<GoodsExceptionScanningDto>> findExceptionGoodsLoading(GoodsExceptionScanningReq req);

    /**
     * 装车发货
     * @param req
     * @return
     * @Author zhengchengfa
     */
    JdCResponse goodsLoadingDeliver(GoodsLoadingReq req);


    /**
     * 装车扫描接口
     */
    JdCResponse<List<GoodsDetailDto>> goodsLoadingScan(GoodsLoadingScanningReq req);

    /**
     * 校验批次号接口--发货校验
     */
    JdVerifyResponse<Void> checkBatchCode(GoodsLoadingScanningReq req);

    /**
     * 校验板号/包裹号接口--发货校验
     */
    JdVerifyResponse<Void> checkPackageCode(GoodsLoadingScanningReq req);

    /**
     * 包裹暂存接口
     */
    JdCResponse<Void> saveByPackageCode(GoodsLoadingScanningReq req);


}
