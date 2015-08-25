package com.jd.bluedragon.distribution.globaltrade.service;

import java.util.List;
import java.util.Map;

import com.jd.bluedragon.distribution.globaltrade.domain.LoadBill;
import com.jd.bluedragon.distribution.globaltrade.domain.LoadBillReport;

public interface LoadBillService {

	/**
	 * 将装载单的审批状态更新为 放行 或 未放行
	 *
	 * @param report
	 */
	void updateLoadBillStatusByReport(LoadBillReport report);

	/**
	 * 分页查询装载单
	 *
	 * @param params
	 * @return
	 */
	List<LoadBill> findPageLoadBill(Map<String, Object> params);

	/**
	 * 查询装载单的数量
	 *
	 * @param params
	 * @return
	 */
	Integer findCountLoadBill(Map<String, Object> params);

	/**
	 * 根据批次号初始化装载单
	 *
	 * @param sendCode
	 * @param string
	 * @param integer
	 */
	int initialLoadBill(String sendCode, Integer userId, String userName);

    /**
     * 获取装载单表 根据箱号或者订单号获取
     * @param report
     * @return
     */
    List<LoadBill> findWaybillInLoadBill(LoadBillReport report);

}
