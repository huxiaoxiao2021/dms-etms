package com.jd.bluedragon.distribution.abnormalwaybill.service;

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
 * Created by shipeilin on 2017/11/17.
 */
@Service("abnormalWayBillService")
public class AbnormalWayBillServiceImpl implements AbnormalWayBillService {

    @Autowired
    AbnormalWayBillDao abnormalWayBillDao;


    @Override
    public AbnormalWayBill getAbnormalWayBillByWayBillCode(String wayBillCode, Integer siteCode) {
        return abnormalWayBillDao.query(wayBillCode, siteCode);
    }

    @Override
    @JProfiler(jKey = "DMSWEB.AbnormalWayBillService.insertAbnormalWayBill", mState = {JProEnum.TP})
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public int insertAbnormalWayBill(AbnormalWayBill abnormalWayBill) {
        return abnormalWayBillDao.insert(abnormalWayBill);
    }

    @Override
    @JProfiler(jKey = "DMSWEB.AbnormalWayBillService.insertBatchAbnormalWayBill", mState = {JProEnum.TP})
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public int insertBatchAbnormalWayBill(List<AbnormalWayBill> wayBillList) {
        return abnormalWayBillDao.addBatch(wayBillList);
    }
}
