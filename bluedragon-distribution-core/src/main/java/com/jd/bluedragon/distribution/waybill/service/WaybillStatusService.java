package com.jd.bluedragon.distribution.waybill.service;

import java.util.List;

import com.jd.bluedragon.distribution.task.domain.Task;

public interface WaybillStatusService {

	/** 将分拣系统各操作日志回传到运单系统 */
	void sendModifyWaybillStatusNotify(List<Task> tasks) throws Exception;
	/** 回传全程跟踪 */
	void sendModifyWaybillTrackNotify(List<Task> tasks) throws Exception;
	/** 置运单状态为妥投*/
	void sendModifyWaybillStatusFinished(Task task) throws Exception ;

}
