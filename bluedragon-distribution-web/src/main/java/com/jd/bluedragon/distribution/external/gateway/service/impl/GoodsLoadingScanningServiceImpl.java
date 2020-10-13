package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.GoodsLoadingReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.GoodsLoadingScanningReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.response.GoodsLoadingScanningDto;
import com.jd.bluedragon.external.gateway.service.GoodsLoadingScanningService;

import java.util.List;

public class GoodsLoadingScanningServiceImpl implements GoodsLoadingScanningService {

    @Override
    public JdCResponse goodsRemoveScanning(GoodsLoadingScanningReq req) {
        return null;
    }

    @Override
    public JdCResponse goodsCompulsoryDeliver(GoodsLoadingScanningReq req) {
        return null;
    }

    @Override
    public JdCResponse<List<GoodsLoadingScanningDto>> findExceptionGoodsLoading(GoodsLoadingScanningReq req) {
        return null;
    }

    @Override
    public JdCResponse goodsLoading(GoodsLoadingReq req) {
        return null;
    }

    @Override
    public JdCResponse goodsLoadingScan(GoodsLoadingReq req) {
        return null;
    }

    @Override
    public JdCResponse checkBySendCodeOrBoardcodeOrPackageBarcode(GoodsLoadingReq req) {
        return null;
    }

    @Override
    public JdCResponse saveForLoadingScanGoodsDetail(GoodsLoadingReq req) {
        return null;
    }

    @Override
    public JdCResponse checkLoadingScanComplete(GoodsLoadingReq req) {
        return null;
    }


}
