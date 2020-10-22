package com.jd.bluedragon.distribution.goodsLoadScan.service.impl;

import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.GoodsExceptionScanningReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.response.GoodsExceptionScanningDto;
import com.jd.bluedragon.distribution.goodsLoadScan.GoodsLoadScanConstants;
import com.jd.bluedragon.distribution.goodsLoadScan.dao.GoodsLoadScanDao;
import com.jd.bluedragon.distribution.goodsLoadScan.dao.GoodsLoadScanRecordDao;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.ExceptionScanDto;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.GoodsLoadScan;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.GoodsLoadScanException;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.GoodsLoadScanRecord;
import com.jd.bluedragon.distribution.goodsLoadScan.service.ExceptionScanService;
import com.jd.bluedragon.distribution.goodsLoadScan.service.LoadScanService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.dms.common.cache.CacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service("exceptionScanService")
public class ExceptionScanServiceImpl implements ExceptionScanService {
    private final static Logger log = LoggerFactory.getLogger(ExceptionScanServiceImpl.class);

    @Autowired
    private GoodsLoadScanRecordDao goodsLoadScanRecordDao;

    @Autowired
    private GoodsLoadScanDao goodsLoadScanDao;

    @Autowired
    private LoadScanService loadScanService;

    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService jimdbCacheService;

    /*
     * 取消扫描查询是否存在：先插记录表  再查扫描表
     */
    @Override
    public ExceptionScanDto findExceptionGoodsScan(GoodsLoadScanRecord record) {

        ExceptionScanDto res = null;

        record.setScanAction(GoodsLoadScanConstants.GOODS_SCAN_LOAD); //扫描动作：1是装车扫描，0是取消扫描
        List<GoodsLoadScanRecord> goodsRecord = goodsLoadScanRecordDao.selectListByCondition(record);

        if(goodsRecord != null && goodsRecord.size() > 0 && goodsRecord.get(0).getWayBillCode() != null) {
            String wayBill = goodsRecord.get(0).getWayBillCode();
            log.info("ExceptionScanServiceImpl#findExceptionGoodsScan 取消扫描查询包裹记录成功 出参【" + JsonHelper.toJson(goodsRecord) + "】");

            GoodsLoadScan loadScanRes = goodsLoadScanDao.findLoadScanByTaskIdAndWaybillCode(record.getTaskId(),wayBill);

            if(loadScanRes != null) {
                log.info("ExceptionScanServiceImpl#findExceptionGoodsScan 取消扫描查询包裹扫描明细表成功，出参【" + JsonHelper.toJson(loadScanRes) + "】");
                res = new ExceptionScanDto();
                res.setTaskId(loadScanRes.getTaskId());
                res.setWayBillCode(loadScanRes.getWayBillCode());
                res.setPackageCode(goodsRecord.get(0).getPackageCode());
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
        record.setUpdateUserCode(exceptionScanDto.getOperatorCode());
        record.setUpdateUserName(exceptionScanDto.getOperator());
        record.setUpdateTime(new Date());

        GoodsLoadScan lc = new GoodsLoadScan();
        lc.setWayBillCode(exceptionScanDto.getWayBillCode());
        lc.setTaskId(exceptionScanDto.getTaskId());
        lc.setUpdateUserCode(exceptionScanDto.getOperatorCode());
        lc.setUpdateUserName(exceptionScanDto.getOperator());
        lc.setUpdateTime(new Date());

        boolean res = loadScanService.updateGoodsLoadScanAmount(lc, record, exceptionScanDto.getCurrentSiteCode());

        if(res) {
            log.info("ExceptionScanServiceImpl#removeGoodsScan 取消扫描修改包裹明细表 --success--，参数【"+ JsonHelper.toJson(lc) + "】");
        }else {
            log.info("ExceptionScanServiceImpl#removeGoodsScan 取消扫描修改包裹明细表 --error--，参数【"+ JsonHelper.toJson(lc) + "】");
        }
        return res;
    }

//    @Override
//    public boolean goodsCompulsoryDeliver(GoodsExceptionScanningReq req) {
//        /**
//         * 遍历list1运单数据,根据运单号任务号查运单表 ，返回单个运单信息waybill
//         * 存在：  根据运单号查询包裹表返回lsit2包裹，
//         * 遍历list2 根据包裹id更改包裹表
//         * 根据waybill id改运单表
//         */
//        Long taskNo = req.getTaskId();
//        for(int i=0; i<req.getWaybillCode().size(); i++) {
//            log.info("ExceptionScanServiceImpl#goodsCompulsoryDeliver 强发查询运单信息--begin--任务号【"+ taskNo + "】，运单号【" + req.getWaybillCode().get(i) + "】");
//            GoodsLoadScan gls = goodsLoadScanDao.findLoadScanByTaskIdAndWaybillCode(taskNo, req.getWaybillCode().get(i));
//
//            if(gls != null) {//更改强发数量 和 该运单状态
//                GoodsLoadScanRecord param = new GoodsLoadScanRecord();
//                param.setTaskId(taskNo);
//                param.setWayBillCode(gls.getWayBillCode());
//                List<GoodsLoadScanRecord> goodsRecordList  =  goodsLoadScanRecordDao.selectListByCondition(param);
//
//                if(goodsRecordList != null && goodsRecordList.size() > 0) {
//                    for(int k = 0; k < goodsRecordList.size(); k++) {
//                        GoodsLoadScanRecord record = new GoodsLoadScanRecord();
//                        record.setId(goodsRecordList.get(k).getId());
//                        record.setForceStatus(GoodsLoadScanConstants.GOODS_LOAD_SCAN_FORCE_STATUS_Y);//强发
//                        record.setUpdateTime(new Date());
//                        record.setUpdateUserName(req.getUser().getUserName());
//                        record.setUpdateUserCode(req.getUser().getUserCode());
//                        log.info("ExceptionScanServiceImpl#goodsCompulsoryDeliver 强发包裹状态记录--begin--参数【"+ JsonHelper.toJson(record) + "】");
//                        goodsLoadScanRecordDao.updateGoodsScanRecordById(record);
//                        log.info("ExceptionScanServiceImpl#goodsCompulsoryDeliver 强发包裹状态记录--end--参数【"+ JsonHelper.toJson(record) + "】");
//                    }
//                }
//
//                gls.setForceAmount(gls.getLoadAmount());
//                gls.setUpdateTime(new Date());
//                gls.setUpdateUserName(req.getUser().getUserName());
//                gls.setUpdateUserCode(req.getUser().getUserCode());
//                gls.setStatus(GoodsLoadScanConstants.GOODS_SCAN_LOAD_ORANGE);
//                log.info("ExceptionScanServiceImpl#goodsCompulsoryDeliver 强发运单状态记录--begin--参数【"+ JsonHelper.toJson(gls) + "】");
//                boolean res = goodsLoadScanDao.updateByPrimaryKey(gls);
//                log.info("ExceptionScanServiceImpl#goodsCompulsoryDeliver 强发运单状态记录--end--参数【"+ JsonHelper.toJson(gls) + "】");
//
//            }else {
//                log.info("ExceptionScanServiceImpl#goodsCompulsoryDeliver 强发查询运单信息不存在--error--任务号【"+ taskNo + "】，运单号【" + req.getWaybillCode().get(i) + "】");
//                return false;
//            }
//        }
//        return true;
//    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean goodsCompulsoryDeliver(GoodsExceptionScanningReq req) {
        Long taskNo = req.getTaskId();
        for(int i=0; i<req.getWaybillCode().size(); i++) {

            String cacheKey = taskNo + "_" + req.getWaybillCode().get(i);
            //查缓存，查库，获取id，根据id修改
            GoodsLoadScan cacheRes = jimdbCacheService.get(cacheKey, GoodsLoadScan.class);
            if(cacheRes == null || cacheRes.getId() == null) {
                cacheRes = goodsLoadScanDao.findLoadScanByTaskIdAndWaybillCode(taskNo, req.getWaybillCode().get(i));
                if(cacheRes == null) {//强发操作时，必须有运单信息
                    throw new GoodsLoadScanException("运单强发操作失败，未查到该运单");
//                    return false;
                }
            }

            GoodsLoadScan gls = new GoodsLoadScan();
            gls.setForceAmount(cacheRes.getLoadAmount());
            gls.setUpdateTime(new Date());
            gls.setUpdateUserName(req.getUser().getUserName());
            gls.setUpdateUserCode(req.getUser().getUserCode());
            gls.setStatus(GoodsLoadScanConstants.GOODS_SCAN_LOAD_ORANGE);
            gls.setId(cacheRes.getId());

            log.info("ExceptionScanServiceImpl#goodsCompulsoryDeliver 运单强制下发，运单记录表修改--begin--参数【"+ JsonHelper.toJson(gls) + "】");
            boolean res = goodsLoadScanDao.updateByPrimaryKey(gls);
            if(!res) {
                log.info("ExceptionScanServiceImpl#goodsCompulsoryDeliver 运单强制下发，运单记录表修改--error--参数【"+ JsonHelper.toJson(gls) + "】");
                throw new GoodsLoadScanException("运单强制下发，运单记录表修改失败");
//                return false;
            }
            log.info("ExceptionScanServiceImpl#goodsCompulsoryDeliver 运单强制下发，运单记录表修改--end--参数【"+ JsonHelper.toJson(gls) + "】");


            GoodsLoadScanRecord param = new GoodsLoadScanRecord();
            param.setTaskId(taskNo);
            param.setWayBillCode(cacheRes.getWayBillCode());
            List<GoodsLoadScanRecord> goodsRecordList  =  goodsLoadScanRecordDao.selectListByCondition(param);

            if(goodsRecordList != null && goodsRecordList.size() > 0) {
                for(int k = 0; k < goodsRecordList.size(); k++) {
                    GoodsLoadScanRecord record = new GoodsLoadScanRecord();
                    record.setId(goodsRecordList.get(k).getId());
                    record.setForceStatus(GoodsLoadScanConstants.GOODS_LOAD_SCAN_FORCE_STATUS_Y);//强发
                    record.setUpdateTime(new Date());
                    record.setUpdateUserName(req.getUser().getUserName());
                    record.setUpdateUserCode(req.getUser().getUserCode());
                    log.info("ExceptionScanServiceImpl#goodsCompulsoryDeliver 强发包裹状态记录--begin--参数【"+ JsonHelper.toJson(record) + "】");
                    int resNum = goodsLoadScanRecordDao.updateGoodsScanRecordById(record);
                    if(resNum < 1) {
                        log.info("ExceptionScanServiceImpl#goodsCompulsoryDeliver 强发包裹状态记录--error--参数【"+ JsonHelper.toJson(record) + "】");
                        throw new GoodsLoadScanException("运单强制下发包裹信息记录失败");
//                        return  false;
                    }
                    log.info("ExceptionScanServiceImpl#goodsCompulsoryDeliver 强发包裹状态记录--end--参数【"+ JsonHelper.toJson(record) + "】");
                }
            }

        }
        return true;
    }

    @Override
    public List<GoodsExceptionScanningDto> findAllExceptionGoodsScan(Long taskId) {
        List<GoodsExceptionScanningDto> res= new ArrayList<>();

        log.info("ExceptionScanServiceImpl#findAllExceptionGoodsScan--begin 根据任务号查询不齐异常数据： 参数【" + taskId + "】");
        List<GoodsLoadScan> list = goodsLoadScanDao.findLoadScanByTaskId(taskId);
        log.info("ExceptionScanServiceImpl#findAllExceptionGoodsScan--begin 根据任务号【" + taskId + "】查询不齐异常数据： 出参【" + JsonHelper.toJson(list) + "】");
//todo  日志
        if(list != null && list.size() > 0) {
            for(GoodsLoadScan glc : list) {
                if(glc.getStatus() == GoodsLoadScanConstants.GOODS_SCAN_LOAD_RED || glc.getStatus() == GoodsLoadScanConstants.GOODS_SCAN_LOAD_YELLOW) {
                    GoodsExceptionScanningDto resDto = new GoodsExceptionScanningDto();
                    resDto.setId(glc.getId());
                    resDto.setTaskId(glc.getTaskId());
                    resDto.setWaybillCode(glc.getWayBillCode());
                    resDto.setLoadAmount(glc.getLoadAmount());
                    resDto.setUnloadAmount(glc.getUnloadAmount());
                    resDto.setForceAmount(glc.getForceAmount());
                    res.add(resDto);
                }
            }
        }
        return res;
    }

    @Override
    public boolean checkException(Long taskId) {
        //根据任务号查询运单表中 不齐和多扫的运单记录
        //如果存在 返回true
        //否则  返回false
        ArrayList<Integer> list = new ArrayList<>();
        list.add(GoodsLoadScanConstants.GOODS_SCAN_LOAD_RED);
        list.add(GoodsLoadScanConstants.GOODS_SCAN_LOAD_YELLOW);

        List<GoodsLoadScan> res = new ArrayList<>();
        res = goodsLoadScanDao.findException(taskId, list);

        if(res.size() > 0) {
            return true;
        }

        return false;
    }
}
