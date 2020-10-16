package com.jd.bluedragon.distribution.goodsLoadScan.service;

import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.GoodsExceptionScanningReq;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.ExceptionScanDto;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.GoodsLoadScanRecord;

public interface ExceptionScanService {

    //取消包裹扫描查询包裹是否存在
    ExceptionScanDto findExceptionGoodsScan(GoodsLoadScanRecord record);
    // 取消包裹扫描修改扫描记录表
    boolean removeGoodsScan(ExceptionScanDto record);
    //强发
    boolean goodsCompulsoryDeliver(GoodsExceptionScanningReq req);
}
