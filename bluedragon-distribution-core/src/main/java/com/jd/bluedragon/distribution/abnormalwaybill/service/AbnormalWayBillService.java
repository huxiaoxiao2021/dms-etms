package com.jd.bluedragon.distribution.abnormalwaybill.service;

import com.jd.bluedragon.distribution.abnormalwaybill.domain.AbnormalWayBill;

import java.util.List;

/**
 * Created by shipeilin on 2017/11/17.
 */
public interface AbnormalWayBillService {

    /**
     * 根据运单号查找异常处理记录
     * @param wayBillCode
     * @return
     */
    AbnormalWayBill getAbnormalWayBillByWayBillCode(String wayBillCode, Integer siteCode);

    /**
     * 新增运单的异常处理记录
     * @param abnormalWayBill
     * @return
     */
    int insertAbnormalWayBill(AbnormalWayBill abnormalWayBill);

    /**
     * 批量增加运单的异常处理记录
     * @param wayBillList
     * @return
     */
    int insertBatchAbnormalWayBill(List<AbnormalWayBill> wayBillList);

}
