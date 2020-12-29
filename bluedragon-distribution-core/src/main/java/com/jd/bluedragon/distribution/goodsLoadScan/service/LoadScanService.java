package com.jd.bluedragon.distribution.goodsLoadScan.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.GoodsLoadingReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.GoodsLoadingScanningReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.response.LoadScanDetailDto;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.GoodsLoadScan;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.GoodsLoadScanRecord;
import com.jd.bluedragon.distribution.loadAndUnload.LoadCar;

import java.util.List;

public interface LoadScanService {
    //装车发货
    JdCResponse goodsLoadingDeliver(GoodsLoadingReq req);

    //根据任务号查询任务状态
    LoadCar findTaskStatus(Long taskId);

    //根据任务id和运单号查询缓存中运单数据
//    GoodsLoadScan queryWaybillCache(Long taskId, String waybillCode);

    //根据任务id和运单号查询缓存中运单数据
//    GoodsLoadScanRecord queryPackageCache(Long taskId, String waybillCode, String packageCode);

    /**
     * 修改装车运单记录，计算已装车、未装车数量
     * @param goodsLoadScan 运单信息
     * @param goodsLoadScanRecord   包裹信息
     * @param currentSiteCode  当前分拣中心
     * @return
     */
    boolean updateGoodsLoadScanAmount(GoodsLoadScan goodsLoadScan, GoodsLoadScanRecord goodsLoadScanRecord, Integer currentSiteCode);

    /**
     * 获取装车扫描运单暂存列表
     */
    JdCResponse<LoadScanDetailDto> goodsLoadingScan(GoodsLoadingScanningReq req);

    /**
     * 包裹号转板号进行暂存
     */
    JdCResponse<Void> saveLoadScanByBoardCode(GoodsLoadingScanningReq req, JdCResponse<Void> response, LoadCar loadCar);

    /**
     * 包裹号转运单号进行暂存
     */
    JdCResponse<Void> saveLoadScanByWaybillCode(GoodsLoadingScanningReq req, JdCResponse<Void> response, LoadCar loadCar);

    /**
     * 校验包裹号并暂存
     */
    JdCResponse<Void> checkInspectAndSave(GoodsLoadingScanningReq req, JdCResponse<Void> response, LoadCar loadCar);

    /**
     * 校验包裹号
     */
    JdVerifyResponse<Void> checkPackageCode(GoodsLoadingScanningReq req, JdVerifyResponse<Void> response);

    /**
     * 校验板号
     */
    JdVerifyResponse<Void> checkBoardCode(GoodsLoadingScanningReq req, JdVerifyResponse<Void> response);

    /**
     * 校验运单号
     */
    JdVerifyResponse<Void> checkWaybillCode(GoodsLoadingScanningReq req, JdVerifyResponse<Void> response);

    /**
     * 校验批次号
     */
    JdCResponse<Void> checkBatchCode(GoodsLoadingScanningReq req, JdCResponse<Void> response);

    /**
     * 根据任务ID删除装车扫描运单暂存记录和包裹扫描记录
     */
    void deleteLoadScanByTaskId(Long taskId);

    /**
     * 未装包裹明细列表查询
     */
    JdCResponse<List<String>> findUnloadPackages(GoodsLoadingScanningReq req, JdCResponse<List<String>> response);


}
