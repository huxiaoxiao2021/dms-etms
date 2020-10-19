package com.jd.bluedragon.distribution.goodsLoadScan.service.impl;

import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.GoodsExceptionScanningReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.response.GoodsExceptionScanningDto;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
            if(exceptionScanDto.getLoadAmount() == 1) {//  当前已装为1时，取消发货后已装为0，不属于不齐异常，变更状态
                lc.setStatus(GoodsLoadScanConstants.GOODS_SCAN_LOAD_BLANK);
            }
            lc.setUpdateUserCode(exceptionScanDto.getOperatorCode());
            lc.setUpdateUserName(exceptionScanDto.getOperator());
            lc.setUpdateTime(new Date());

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
        /**
         * 遍历list1运单数据,根据运单号任务号查运单表 ，返回单个运单信息waybill
         * 存在：  根据运单号查询包裹表返回lsit2包裹，
         * 遍历list2 根据包裹id更改包裹表
         * 根据waybill id改运单表
         */
        Long taskNo = req.getTaskId();
        for(int i=0; i<req.getWaybillCode().size(); i++) {
            log.info("ExceptionScanServiceImpl#goodsCompulsoryDeliver 强发查询运单信息--begin--任务号【"+ taskNo + "】，运单号【" + req.getWaybillCode().get(i) + "】");
            GoodsLoadScan gls = goodsLoadScanDao.findLoadScanByTaskIdAndWaybillCode(taskNo, req.getWaybillCode().get(i));

            if(gls != null) {//更改强发数量 和 该运单状态
                GoodsLoadScanRecord param = new GoodsLoadScanRecord();
                param.setTaskId(taskNo);
                param.setWayBillCode(gls.getWayBillCode());
                List<GoodsLoadScanRecord> goodsRecordList  =  goodsLoadScanRecordDao.selectListByCondition(param);

                if(goodsRecordList != null && goodsRecordList.size() > 0) {
                    for(int k = 0; k < goodsRecordList.size(); k++) {
                        GoodsLoadScanRecord record = new GoodsLoadScanRecord();
                        record.setId(goodsRecordList.get(k).getId());
                        record.setForceStatus(GoodsLoadScanConstants.GOODS_LOAD_SCAN_FORCE_STATUS_Y);//强发
                        record.setUpdateTime(new Date());
                        record.setUpdateUserName(req.getOperator());
                        record.setUpdateUserCode(req.getOperatorCode());
                        log.info("ExceptionScanServiceImpl#goodsCompulsoryDeliver 强发包裹状态记录--begin--参数【"+ JsonHelper.toJson(record) + "】");
                        goodsLoadScanRecordDao.updateGoodsScanRecordById(record);
                        log.info("ExceptionScanServiceImpl#goodsCompulsoryDeliver 强发包裹状态记录--end--参数【"+ JsonHelper.toJson(record) + "】");
                    }
                }

                gls.setForceAmount(gls.getLoadAmount());
                gls.setUpdateTime(new Date());
                gls.setUpdateUserName(req.getOperator());
                gls.setUpdateUserCode(req.getOperatorCode());
                gls.setStatus(GoodsLoadScanConstants.GOODS_SCAN_LOAD_ORANGE);
                log.info("ExceptionScanServiceImpl#goodsCompulsoryDeliver 强发运单状态记录--begin--参数【"+ JsonHelper.toJson(gls) + "】");
                boolean res = goodsLoadScanDao.updateByPrimaryKey(gls);
                log.info("ExceptionScanServiceImpl#goodsCompulsoryDeliver 强发运单状态记录--end--参数【"+ JsonHelper.toJson(gls) + "】");

            }else {
                log.info("ExceptionScanServiceImpl#goodsCompulsoryDeliver 强发查询运单信息不存在--error--任务号【"+ taskNo + "】，运单号【" + req.getWaybillCode().get(i) + "】");
                return false;
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

        if(list != null && list.size() > 0) {
            GoodsExceptionScanningDto resDto = new GoodsExceptionScanningDto();
            for(GoodsLoadScan glc : list) {
                resDto.setId(glc.getId());
                resDto.setTaskId(glc.getTaskId());
                resDto.setWaybillCode(glc.getWayBillCode());
                resDto.setLoadAmount(glc.getLoadAmount());
                resDto.setUnloadAmount(glc.getUnloadAmount());
                resDto.setForceAmount(glc.getForceAmount());
            }
            res.add(resDto);
        }
        return res;
    }

    @Override
    public boolean checkException(Long taskId) {
        List<String> goodsRecord = goodsLoadScanDao.findWaybillCodesByTaskId(taskId);
        if(goodsRecord == null || goodsRecord.size() <=0) {
            return false;
        }
        return true;
    }

}
