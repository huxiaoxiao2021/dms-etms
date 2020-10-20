package com.jd.bluedragon.distribution.goodsLoadScan.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.GoodsLoadingReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.GoodsLoadingScanningReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.response.LoadScanDetailDto;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.GoodsLoadScan;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.GoodsLoadScanRecord;
import com.jd.bluedragon.distribution.loadAndUnload.LoadCar;

public interface LoadScanService {
    //装车发货
    JdCResponse goodsLoadingDeliver(GoodsLoadingReq req);

    //根据任务号查询任务状态
    Integer findTaskStatus(Long taskId);

    //根据任务id和运单号查询缓存中运单数据
    GoodsLoadScan queryByWaybillCodeAndTaskId(Long taskId, String waybillCode);

    //修改装车运单记录，计算已装车、未装车数量
    boolean updateGoodsLoadScanAmount(GoodsLoadScan goodsLoadScan, GoodsLoadScanRecord goodsLoadScanRecord, Integer currentSiteCode);

    JdCResponse<LoadScanDetailDto> goodsLoadingScan(GoodsLoadingScanningReq req);

    JdCResponse<Void> saveLoadScanByBoardCode(GoodsLoadingScanningReq req, JdCResponse<Void> response, LoadCar loadCar);

    JdCResponse<Void> checkInspectAndSave(GoodsLoadingScanningReq req, JdCResponse<Void> response, LoadCar loadCar);

    JdVerifyResponse<Void> checkPackageCode(GoodsLoadingScanningReq req, JdVerifyResponse<Void> response);

    JdVerifyResponse<Void> checkBoardCode(GoodsLoadingScanningReq req, JdVerifyResponse<Void> response);

    JdCResponse<Void> checkBatchCode(GoodsLoadingScanningReq req, JdCResponse<Void> response);



    }
