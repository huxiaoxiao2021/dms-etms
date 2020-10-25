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
import com.jd.bluedragon.distribution.goodsLoadScan.service.LoadScanCacheService;
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

import javax.annotation.Resource;
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

    @Resource
    private LoadScanCacheService loadScanCacheService;

    /*
     * 取消扫描查询是否存在：先插记录表  再查扫描表
     */
    @Override
    public ExceptionScanDto findExceptionGoodsScan(GoodsLoadScanRecord record) {
    //todo  mapper sql重写    日志重写
        ExceptionScanDto res = null;

        record.setScanAction(GoodsLoadScanConstants.GOODS_SCAN_LOAD); //扫描动作：1是装车扫描，0是取消扫描
        List<GoodsLoadScanRecord> goodsRecord = goodsLoadScanRecordDao.selectRecordList(record);

        if(goodsRecord != null && goodsRecord.size() > 0 && goodsRecord.get(0).getWayBillCode() != null) {
            if(log.isDebugEnabled()) {
                log.debug("取消扫描查询包裹信息--success 出参【{}】", JsonHelper.toJson(goodsRecord));
            }
            String wayBill = goodsRecord.get(0).getWayBillCode();

            GoodsLoadScan loadScanRes = goodsLoadScanDao.findLoadScanByTaskIdAndWaybillCode(record.getTaskId(),wayBill);

            if(loadScanRes != null) {
                log.info("取消扫描查询运单信息-，出参【{}】", JsonHelper.toJson(loadScanRes));
                res = new ExceptionScanDto();
                res.setTaskId(loadScanRes.getTaskId());
                res.setWayBillCode(loadScanRes.getWayBillCode());
                res.setPackageCode(goodsRecord.get(0).getPackageCode());
                res.setLoadAmount(loadScanRes.getLoadAmount());
                res.setUnloadAmount(loadScanRes.getUnloadAmount());
                res.setForceStatus(goodsRecord.get(0).getForceStatus());
            }else {
                throw  new GoodsLoadScanException("包裹【" + record.getPackageCode() + "】对应运单信息查询为空");
            }
        }

        return res;
    }

    @Override
    public boolean removeGoodsScan(ExceptionScanDto exceptionScanDto) {
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

        if(!res) {
            log.info("取消扫描修改包裹明细表 --error--，参数【{}】", JsonHelper.toJson(lc));
        }
        return res;
    }

    @Override
    // todo 事务增加 main_undiv
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, value = "main_undiv")
    public boolean goodsCompulsoryDeliver(GoodsExceptionScanningReq req) {
        Long taskNo = req.getTaskId();
        List<String>  waybillList = req.getWaybillCode();
        for(int i=0; i<waybillList.size(); i++) {
            String wayBillCode = waybillList.get(i);
            // todo key format
            //查缓存，查库，获取id，根据id修改
            GoodsLoadScan cacheRes = loadScanCacheService.getWaybillLoadScan(taskNo,wayBillCode);
            if(cacheRes == null || cacheRes.getId() == null) {
//                cacheRes = goodsLoadScanDao.findLoadScanByTaskIdAndWaybillCode(taskNo, wayBillCode);
                cacheRes = goodsLoadScanDao.findWaybillInfoByTaskIdAndWaybillCode(taskNo, wayBillCode);
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

            log.info("运单强制下发，运单记录表修改--begin--参数【{}】", JsonHelper.toJson(gls));
            boolean res = goodsLoadScanDao.updateByPrimaryKey(gls);
            if(!res) {
                throw new GoodsLoadScanException("运单强制下发，运单记录表修改失败");
            }

            //根据运单号、任务号、修改包裹信息强发标识

            GoodsLoadScanRecord param = new GoodsLoadScanRecord();
            param.setTaskId(taskNo);
            param.setWayBillCode(wayBillCode);
            int num = goodsLoadScanRecordDao.updatePackageForceStatus(param);
            if(num <=0 ) {
                throw new GoodsLoadScanException("运单【" + wayBillCode + "】强制下发包裹信息记录失败,");
            }

           /* GoodsLoadScanRecord param = new GoodsLoadScanRecord();
            param.setTaskId(taskNo);
            param.setWayBillCode(cacheRes.getWayBillCode());
            List<GoodsLoadScanRecord> goodsRecordList  =  goodsLoadScanRecordDao.selectListByCondition(param);

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
                }
                log.info("ExceptionScanServiceImpl#goodsCompulsoryDeliver 强发包裹状态记录--end--参数【"+ JsonHelper.toJson(record) + "】");
            }*/

        }
        return  true ;
    }

    @Override
    public List<GoodsExceptionScanningDto> findAllExceptionGoodsScan(Long taskId) {
        List<GoodsExceptionScanningDto> res= new ArrayList<>();

//        if(log.isDebugEnabled()) {
//            log.debug("根据任务号【{}】查询不齐异常数据 --begin--", taskId);
//        }
        List<GoodsLoadScan> list = goodsLoadScanDao.findAllLoadScanByTaskId(taskId);

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
//        if(log.isDebugEnabled()) {
//            log.debug("根据任务号【{}】查询不齐异常数据 --end-- 返回【{}】", taskId, JsonHelper.toJson(res));
//        }
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
