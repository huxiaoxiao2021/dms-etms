package com.jd.bluedragon.distribution.goodsLoadScan.service.impl;

import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.GoodsExceptionScanningReq;
import com.jd.bluedragon.distribution.goodsLoadScan.GoodsLoadScanConstants;
import com.jd.bluedragon.distribution.goodsLoadScan.dao.GoodsLoadScanDao;
import com.jd.bluedragon.distribution.goodsLoadScan.dao.GoodsLoadScanRecordDao;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.ExceptionScanDto;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.GoodsLoadScan;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.GoodsLoadScanRecord;
import com.jd.bluedragon.distribution.goodsLoadScan.service.ExceptionScanService;
import com.jd.bluedragon.utils.JsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class ExceptionScanServiceImpl implements ExceptionScanService {
    private final static Logger log = LoggerFactory.getLogger(ExceptionScanServiceImpl.class);


    @Autowired
    private GoodsLoadScanRecordDao goodsLoadScanRecordDao;

    @Autowired
    private GoodsLoadScanDao goodsLoadScanDao;

    /*
     * 取消扫描查询是否存在：先插记录表  再查扫描表
     */
    @Override
    public ExceptionScanDto findExceptionGoodsScan(GoodsLoadScanRecord record) {

        ExceptionScanDto res = null;

        record.setScanAction(GoodsLoadScanConstants.GOODS_SCAN_LOAD); //扫描动作：1是装车扫描，0是取消扫描
        GoodsLoadScanRecord goodsRecord = goodsLoadScanRecordDao.selectListByCondition(record);

        if(goodsRecord != null && goodsRecord.getWayBillCode() != null) {
            log.info("ExceptionScanServiceImpl#findExceptionGoodsScan 取消扫描查询包裹记录成功 出参【" + JsonHelper.toJson(goodsRecord) + "】");

            GoodsLoadScan loadScanRes = goodsLoadScanDao.findLoadScanByTaskIdAndWaybillCode(record.getTaskId(),goodsRecord.getWayBillCode());

            if(loadScanRes != null) {
                log.info("ExceptionScanServiceImpl#findExceptionGoodsScan 取消扫描查询包裹扫描明细表成功，出参【" + JsonHelper.toJson(loadScanRes) + "】");
                res = new ExceptionScanDto();
                res.setTaskId(loadScanRes.getTaskId());
                res.setWayBillCode(loadScanRes.getWayBillCode());
                res.setPackageCode(goodsRecord.getPackageCode());
                res.setLoadAmount(loadScanRes.getLoadAmount());
                res.setUnloadAmount(loadScanRes.getUnloadAmount());
            }else {
                log.info("ExceptionScanServiceImpl#findExceptionGoodsScan 取消扫描查询包裹扫描明细表失败，包裹号【"+ record.getPackageCode() + "】");
            }
        }else  {
            log.info("ExceptionScanServiceImpl#findExceptionGoodsScan 取消扫描查询包裹记录表失败，包裹号【"+ record.getPackageCode() +"】");

        }

        return res;
    }

    @Override
    public boolean removeGoodsScan(ExceptionScanDto exceptionScanDto) {
        boolean flag = false;

        GoodsLoadScanRecord record = new GoodsLoadScanRecord();
        record.setPackageCode(exceptionScanDto.getPackageCode());
        record.setTaskId(exceptionScanDto.getTaskId());
        record.setWayBillCode(exceptionScanDto.getWayBillCode());
        record.setScanAction(GoodsLoadScanConstants.GOODS_SCAN_REMOVE);
        log.info("ExceptionScanServiceImpl#removeGoodsScan 取消扫描修改包裹记录表--begin--，入参【"+ JsonHelper.toJson(record) + "】");
        int num = goodsLoadScanRecordDao.updateGoodsScanRecordById(record);

        if( num > 0) {
            log.info("ExceptionScanServiceImpl#removeGoodsScan 取消扫描修改包裹记录表成功--success--，包裹号【"+ exceptionScanDto.getPackageCode()
                    +"】，任务号【" + exceptionScanDto.getTaskId() + "】");
            GoodsLoadScan lc = new GoodsLoadScan();
            lc.setWayBillCode(exceptionScanDto.getWayBillCode());
            lc.setTaskId(exceptionScanDto.getTaskId());
            lc.setLoadAmount(exceptionScanDto.getLoadAmount() - 1);
            lc.setUnloadAmount(exceptionScanDto.getUnloadAmount() + 1);
            if(exceptionScanDto.getLoadAmount() == 1) {
                lc.setStatus(GoodsLoadScanConstants.GOODS_SCAN_LOAD_BLANK);
            }

            log.info("ExceptionScanServiceImpl#removeGoodsScan 取消扫描修改包裹明细表 --begin--，入参【"+ JsonHelper.toJson(lc) + "】");
            boolean scNum = goodsLoadScanDao.updateByPrimaryKey(lc);
            if(scNum == true) {
                log.info("ExceptionScanServiceImpl#removeGoodsScan 取消扫描修改包裹明细表 --success--，参数【"+ JsonHelper.toJson(lc) + "】");
                flag = true;
            }else {
                log.info("ExceptionScanServiceImpl#removeGoodsScan 取消扫描修改包裹明细表 --error--，参数【"+ JsonHelper.toJson(lc) + "】");
                flag = false;
            }
        }else {
            log.info("ExceptionScanServiceImpl#removeGoodsScan 取消扫描修改包裹记录表成功--error--参数【"+ JsonHelper.toJson(record) + "】");
        }

        return flag;
    }

    @Override
    public boolean goodsCompulsoryDeliver(GoodsExceptionScanningReq req) {
        Long taskNo = req.getTaskId();
        for(int i=0; i<req.getWaybillCode().size(); i++) {
            log.info("ExceptionScanServiceImpl#goodsCompulsoryDeliver 强发查询运单信息--begin--任务号【"+ taskNo + "】，运单号【" + req.getWaybillCode().get(i) + "】");
            GoodsLoadScan gls = goodsLoadScanDao.findLoadScanByTaskIdAndWaybillCode(taskNo, req.getWaybillCode().get(i));

            if(gls != null) {//更改强发数量 和 该运单状态
                gls.setForceAmount(gls.getLoadAmount());
                gls.setStatus(GoodsLoadScanConstants.GOODS_SCAN_LOAD_ORANGE);
                log.info("ExceptionScanServiceImpl#goodsCompulsoryDeliver 修改强发货物数据及状态--begin--参数【"+ JsonHelper.toJson(gls) + "】");
                boolean res = goodsLoadScanDao.updateByPrimaryKey(gls);
                if(res != true) {
                    log.info("ExceptionScanServiceImpl#goodsCompulsoryDeliver 修改强发货物数据及状态--error--参数【"+ JsonHelper.toJson(gls) + "】");
                    return false;
                }
            }else {
                log.info("ExceptionScanServiceImpl#goodsCompulsoryDeliver 强发查询运单信息不存在--error--任务号【"+ taskNo + "】，运单号【" + req.getWaybillCode().get(i) + "】");
                return false;
            }
        }
        return true;
    }
}
