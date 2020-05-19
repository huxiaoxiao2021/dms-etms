package com.jd.bluedragon.distribution.abnormalwaybill.service;

import com.jd.bluedragon.distribution.abnormalwaybill.domain.AbnormalWayBill;

import java.util.List;

/**
 * 异常操作服务接口
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

    /**
     * 根据提报异常的条码号和站点获取信息
     * @param createSiteCode
     * @param qcValue
     * @return
     */
    AbnormalWayBill getAbnormalWayBillByQcValue(Integer createSiteCode, String qcValue);
    /**
     * 根据运单号查询提报的异常
     * @param waybillCode
     * @return
     */
    AbnormalWayBill queryAbnormalWayBillByWayBillCode(String waybillCode);
}
