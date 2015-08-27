package com.jd.bluedragon.distribution.globaltrade.service;

import java.util.List;
import java.util.Map;

import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.globaltrade.domain.LoadBill;
import com.jd.bluedragon.distribution.globaltrade.domain.LoadBillReport;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.domain.TaskResult;

import java.util.List;
import java.util.Map;

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
	 * 通过订单号取消预装载
	 * 
	 * */
	JdResponse cancelPreloaded(List<LoadBill> request);
	
	/**
	 * 通过id查询订单装载信息
	 * 
	 * */
	LoadBill findLoadbillByID(Long id);

    /**
     * 处理预装载任务，上传数据至卓志接口、
     * 如果卓志返回成功，则更新装载单状态
     * @param task
     * @return
     */
    TaskResult dealPreLoadBillTask(Task task);

    /**
     * 处理预装载数据
     * 首先根据这些id获取装载单数据
     * （1）如果其中有已经装载的，则直接提示失败；
     * （2）如果都是未装载状态，则调用卓志预装载接口，接口返回成功，更改装载单车次号和状态
     * @param id
     * @param trunkNo 车牌号
     * @throws Exception
     * @return 装载数量
     */
    Integer preLoadBill(List<Long> id, String trunkNo) throws Exception;
}
