package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.GoodsLoadingReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.GoodsExceptionScanningReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.GoodsLoadingScanningReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.response.GoodsExceptionScanningDto;
import com.jd.bluedragon.external.gateway.service.GoodsLoadingScanningService;

import java.util.List;

public class GoodsLoadingScanningServiceImpl implements GoodsLoadingScanningService {

    @Override
    public JdCResponse goodsRemoveScanning(GoodsExceptionScanningReq req) {
        return null;
    }

    @Override
    public JdCResponse goodsCompulsoryDeliver(GoodsExceptionScanningReq req) {
        return null;
    }

    @Override
    public JdCResponse<List<GoodsExceptionScanningDto>> findExceptionGoodsLoading(GoodsExceptionScanningReq req) {
        return null;
    }

    @Override
    public JdCResponse goodsLoadingDeliver(GoodsLoadingReq req) {
        return null;
    }

    @Override
    public JdCResponse goodsLoadingScan(GoodsLoadingScanningReq req) {
        return null;
    }

    @Override
    public JdCResponse checkByBatchCodeOrBoardCodeOrPackageCode(GoodsLoadingScanningReq req) {
        return null;
    }


}
