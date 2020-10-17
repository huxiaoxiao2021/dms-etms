package com.jd.bluedragon.distribution.goodsLoadScan.service;

import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.GoodsExceptionScanningReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.response.GoodsExceptionScanningDto;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.ExceptionScanDto;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.GoodsLoadScan;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.GoodsLoadScanRecord;

import java.util.List;

public interface ExceptionScanService {

    //查询包裹是否存在扫描记录和装车记录
    ExceptionScanDto findExceptionGoodsScan(GoodsLoadScanRecord record);
    // 取消包裹扫描修改扫描记录表
    boolean removeGoodsScan(ExceptionScanDto record);
    //强发
    boolean goodsCompulsoryDeliver(GoodsExceptionScanningReq req);
    //查询不齐异常数据
    List<GoodsLoadScan> findAllExceptionGoodsScan(Long taskId);
}
