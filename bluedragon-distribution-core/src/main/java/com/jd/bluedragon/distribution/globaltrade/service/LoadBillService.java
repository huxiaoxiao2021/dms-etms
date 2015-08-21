package com.jd.bluedragon.distribution.globaltrade.service;

import com.jd.bluedragon.distribution.globaltrade.domain.LoadBill;
import com.jd.bluedragon.distribution.globaltrade.domain.LoadBillReport;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.domain.TaskResult;

public interface LoadBillService {

	int add(LoadBill loadBill);

	int update(LoadBill loadBill);

	/**
	 * 将装载单的审批状态更新为  放行 或 未放行
	 * @param report
	 */
	void updateLoadBillStatusByReport(LoadBillReport report);

    /**
     * 处理预装载任务，上传数据至卓志接口、
     * 如果卓志返回成功，则更新装载单状态
     * @param task
     * @return
     */
    TaskResult dealPreLoadBillTask(Task task);
}
