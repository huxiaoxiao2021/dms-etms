package com.jd.bluedragon.distribution.globaltrade.service;

import com.jd.bluedragon.distribution.globaltrade.domain.LoadBill;
import com.jd.bluedragon.distribution.globaltrade.domain.LoadBillReport;

import java.util.List;

public interface LoadBillService {

	int add(LoadBill loadBill);

	int update(LoadBill loadBill);

	/**
	 * 将装载单的审批状态更新为  放行 或 未放行
	 * @param report
	 */
	void updateLoadBillStatusByReport(LoadBillReport report);

    /**
     * 获取装载单表 根据箱号或者订单号获取
     * @param report
     * @return
     */
    List<LoadBill> findWaybillInLoadBill(LoadBillReport report);

}
