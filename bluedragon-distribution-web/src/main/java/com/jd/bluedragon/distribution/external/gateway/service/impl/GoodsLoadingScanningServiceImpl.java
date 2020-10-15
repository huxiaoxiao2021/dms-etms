package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.GoodsLoadingReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.GoodsExceptionScanningReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.GoodsLoadingScanningReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.response.GoodsDetailDto;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.response.GoodsExceptionScanningDto;
import com.jd.bluedragon.external.gateway.service.GoodsLoadingScanningService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Objects;

public class GoodsLoadingScanningServiceImpl implements GoodsLoadingScanningService {

//    @Autowired
//    private GoodsExceptionScanManager goodsExceptionScanManager;
    @Override
    public JdCResponse goodsRemoveScanning(GoodsExceptionScanningReq req) {
        JdCResponse response = new JdCResponse<Boolean>();

//        if(StringUtils.isBlank(req.getTaskId())){
//            response.toFail("任务号不能为空");
//            return response;
//        }

        if(StringUtils.isBlank(req.getPackageCode())){
            response.toFail("包裹号不能为空");
            return response;
        }

//        JdCResponse res = goodsExceptionScanManager.goodsRemoveScanning(req);

//        if (!Objects.equals(res.getCode(), res.CODE_SUCCESS)) {
//            response.toError(response.getMessage());
//            return response;
//        }
        response.toSucceed(response.getMessage());
        return response;

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
    public JdCResponse<List<GoodsDetailDto>> goodsLoadingScan(GoodsLoadingScanningReq req) {
        return null;
    }

    @Override
    public JdCResponse checkByBatchCodeOrBoardCodeOrPackageCode(GoodsLoadingScanningReq req) {
        return null;
    }

    @Override
    public JdCResponse saveByPackageCode(GoodsLoadingScanningReq req) {
        return null;
    }


}
