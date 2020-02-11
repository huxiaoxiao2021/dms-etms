package com.jd.bluedragon.distribution.abnormalwaybill.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.abnormalwaybill.dao.AbnormalWayBillDao;
import com.jd.bluedragon.distribution.abnormalwaybill.domain.AbnormalWayBill;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 异常操作服务接口实现
 * Created by shipeilin on 2017/11/17.
 */
@Service("abnormalWayBillService")
public class AbnormalWayBillServiceImpl implements AbnormalWayBillService {

    @Autowired
    AbnormalWayBillDao abnormalWayBillDao;

    /**
     * 根据运单号查找异常处理记录
     * @param wayBillCode
     * @return
     */
    @Override
    public AbnormalWayBill getAbnormalWayBillByWayBillCode(String wayBillCode, Integer siteCode) {
        return abnormalWayBillDao.query(wayBillCode, siteCode);
    }

    /**
     * 新增运单的异常处理记录
     * @param abnormalWayBill
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.AbnormalWayBillService.insertAbnormalWayBill", mState = {JProEnum.TP})
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public int insertAbnormalWayBill(AbnormalWayBill abnormalWayBill) {
        return abnormalWayBillDao.insert(abnormalWayBill);
    }

    /**
     * 批量增加运单的异常处理记录
     * @param wayBillList
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.AbnormalWayBillService.insertBatchAbnormalWayBill", mState = {JProEnum.TP})
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public int insertBatchAbnormalWayBill(List<AbnormalWayBill> wayBillList) {
        return abnormalWayBillDao.addBatch(wayBillList);
    }

    /**
     * 根据提报异常的条码号和站点编号查询异常处理记录
     * @param createSiteCode
     * @param qcValue
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.AbnormalWayBillService.getAbnormalWayBillByQcValue", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP})
    public AbnormalWayBill getAbnormalWayBillByQcValue(Integer createSiteCode, String qcValue) {
        return abnormalWayBillDao.getAbnormalWayBillByQcValue(createSiteCode, qcValue);
    }
}
