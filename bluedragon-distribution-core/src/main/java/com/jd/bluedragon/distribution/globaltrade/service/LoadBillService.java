package com.jd.bluedragon.distribution.globaltrade.service;

import java.util.List;
import java.util.Map;

import com.jd.bluedragon.distribution.globaltrade.domain.LoadBill;
import com.jd.bluedragon.distribution.globaltrade.domain.LoadBillReport;

public interface LoadBillService {

	int add(LoadBill loadBill);

	int update(LoadBill loadBill);

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
	void initialLoadBill(String sendCode, Integer userId, String userName);

}
