package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.GoodsLoadingReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.GoodsExceptionScanningReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.GoodsLoadingScanningReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.response.GoodsDetailDto;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.response.GoodsExceptionScanningDto;
import com.jd.bluedragon.distribution.goodsLoadScan.dao.GoodsExceptionScanDao;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.ExceptionScanDto;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.GoodsLoadScan;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.GoodsLoadScanRecord;
import com.jd.bluedragon.distribution.goodsLoadScan.service.ExceptionScanService;
import com.jd.bluedragon.external.gateway.service.GoodsLoadingScanningService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Objects;

public class GoodsLoadingScanningServiceImpl implements GoodsLoadingScanningService {

    @Autowired
    private ExceptionScanService exceptionScanService;

    private final static Logger log = LoggerFactory.getLogger(GoodsLoadingScanningServiceImpl.class);

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
        log.info("GoodsLoadingScanningServiceImpl#goodsRemoveScanning- 取消发货查询任务号【"+ req.getTaskId() +"】-包裹号【" + req.getPackageCode() +"】是否被扫描装车");
        ExceptionScanDto exceptionScanDto = exceptionScanService.findExceptionGoodsScan(record);//入参 包裹号  包裹状态=1 yn

        if(exceptionScanDto == null) {
            response.toFail("此包裹号未操作装车，无法取消");
            return response;
        }

        log.info("GoodsLoadingScanningServiceImpl#goodsRemoveScanning- 取消发货更改不齐异常数据，任务号【"+ exceptionScanDto.getTaskId() +"】-运单号【" + exceptionScanDto.getPackageCode() +"】");
        boolean removeRes =  exceptionScanService.removeGoodsScan(exceptionScanDto);

        if(removeRes == true) {
            log.info("GoodsLoadingScanningServiceImpl#goodsRemoveScanning- 取消包裹扫描成功【"+ req.getTaskId() +"】-包裹号【" + req.getPackageCode() +"】是否被扫描装车");
            response.toSucceed("取消包裹扫描成功");
        } else {
            log.info("GoodsLoadingScanningServiceImpl#goodsRemoveScanning- 取消包裹扫描失败【"+ req.getTaskId() +"】-包裹号【" + req.getPackageCode() +"】是否被扫描装车");
            response.toError("取消包裹扫描失败");
        }
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
