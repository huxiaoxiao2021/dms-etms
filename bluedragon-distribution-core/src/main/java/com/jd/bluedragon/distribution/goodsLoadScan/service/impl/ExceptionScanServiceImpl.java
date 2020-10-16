package com.jd.bluedragon.distribution.goodsLoadScan.service.impl;

import com.jd.bluedragon.distribution.goodsLoadScan.GoodsLoadScanConstants;
import com.jd.bluedragon.distribution.goodsLoadScan.dao.GoodsExceptionScanDao;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.ExceptionScanDto;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.GoodsLoadScan;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.GoodsLoadScanRecord;
import com.jd.bluedragon.distribution.goodsLoadScan.service.ExceptionScanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class ExceptionScanServiceImpl implements ExceptionScanService {
    private final static Logger log = LoggerFactory.getLogger(ExceptionScanServiceImpl.class);


    @Autowired
    private GoodsExceptionScanDao goodsExceptionScanDao;

    /*
     * 取消扫描查询是否存在：先插记录表  再查扫描表
     */
    @Override
    public ExceptionScanDto findExceptionGoodsScan(GoodsLoadScanRecord record) {

        ExceptionScanDto res = null;

        record.setScanAction(GoodsLoadScanConstants.GOODS_SCAN_LOAD); //扫描动作：1是装车扫描，0是取消扫描
        GoodsLoadScanRecord goodsRecord = goodsExceptionScanDao.findExceptionGoodsScanRecord(record);

        if(goodsRecord != null && goodsRecord.getWayBillCode() != null) {
            log.info("ExceptionScanServiceImpl#findExceptionGoodsScan 取消扫描查询包裹记录表成功，包裹号【"+ record.getPackageCode() +"】");
            GoodsLoadScan loadScan = new GoodsLoadScan();
            loadScan.setTaskId(record.getTaskId());
            loadScan.setWayBillCode(goodsRecord.getWayBillCode());

            GoodsLoadScan loadScanRes = goodsExceptionScanDao.findGoodLoadScan(loadScan);

            if(loadScanRes != null) {
                log.info("ExceptionScanServiceImpl#findExceptionGoodsScan 取消扫描查询包裹扫描明细表成功，包裹号【"+ record.getPackageCode()
                        +"】，任务号【" + loadScanRes.getTaskId() + "】，运单号【" + loadScanRes.getWayBillCode() + "】");
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
        int num = goodsExceptionScanDao.updateGoodsScanRecord(record);

        if( num > 0) {
            GoodsLoadScan lc = new GoodsLoadScan();
            lc.setWayBillCode(exceptionScanDto.getWayBillCode());
            lc.setTaskId(exceptionScanDto.getTaskId());
            lc.setLoadAmount(exceptionScanDto.getLoadAmount() - 1);
            lc.setUnloadAmount(exceptionScanDto.getUnloadAmount() + 1);
            if(exceptionScanDto.getLoadAmount() == 1) {
                lc.setStatus(GoodsLoadScanConstants.GOODS_SCAN_LOAD_BLANK);
            }
            int scNum = goodsExceptionScanDao.updateGoodsScan(lc);
            if(scNum > 0) {
                flag = false;
            }
        }

        return flag;
    }


}
