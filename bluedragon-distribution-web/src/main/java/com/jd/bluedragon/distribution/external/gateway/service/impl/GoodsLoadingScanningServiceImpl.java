package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.GoodsLoadingReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.GoodsExceptionScanningReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.GoodsLoadingScanningReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.response.GoodsDetailDto;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.response.GoodsExceptionScanningDto;
import com.jd.bluedragon.distribution.goodsLoadScan.dao.GoodsExceptionScanDao;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.GoodsLoadScan;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.GoodsLoadScanRecord;
import com.jd.bluedragon.external.gateway.service.GoodsLoadingScanningService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Objects;

public class GoodsLoadingScanningServiceImpl implements GoodsLoadingScanningService {

    @Autowired
    private GoodsExceptionScanDao goodsExceptionScanDao;

    @Override
    public JdCResponse goodsRemoveScanning(GoodsExceptionScanningReq req) {
        /*
            1: 先根据包裹号，去暂存记录表里查询该包裹是否存在  不存在未多扫   查询结果中含该包裹运单号
            2： 存在该包裹，去修改下该包裹扫描取消的动作
            2：在通过该包裹运单号，去暂存表中修改该包裹对应运单
         */
        JdCResponse response = new JdCResponse<Boolean>();

        if(req.getTaskId() == null){
            response.toFail("任务号不能为空");
            return response;
        }

        if(req.getWaybillCode() == null || req.getWaybillCode().size() == 0 ){
            response.toFail("运单号不能为空");
            return response;
        }

        if(StringUtils.isBlank(req.getPackageCode())){
            response.toFail("包裹号不能为空");
            return response;
        }

        GoodsLoadScanRecord record = new GoodsLoadScanRecord();
        record.setTaskId(req.getTaskId());
        record.setPackageCode(req.getPackageCode());

        GoodsLoadScanRecord goodsRecord = goodsExceptionScanDao.findExceptionGoodsScanRecord(record);//入参 包裹号  包裹状态=1 yn

        if(goodsRecord == null) {
            response.toFail("此包裹号未操作装车，无法取消");
            return response;
        }

        //改状态
        record.setScanAction(0); //扫描动作：1是装车扫描，0是取消扫描
        int num1 = goodsExceptionScanDao.updateGoodsScanRecord(record);

        //根据运单查询扫描表，查看该运单记录已安装是否为1     不为1 只改已装车 未装车    1 还需要改 运单颜色状态
        //不可能为0  0说明此时没有该运单没有已装包裹，那么记录表肯定是没有数据的， 此时已经肯定记录表有数据去操作，只考虑1的情况
        GoodsLoadScan loadScan = new GoodsLoadScan();
        loadScan.setTaskId(req.getTaskId());
        loadScan.setWayBillCode(goodsRecord.getWayBillCode());

        GoodsLoadScan loadScanRes = goodsExceptionScanDao.findGoodLoadScan(loadScan);

//        if()

        int num2 = goodsExceptionScanDao.updateGoodsScan(req);

        //改数据
        response.toSucceed("取消发货成功");
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
