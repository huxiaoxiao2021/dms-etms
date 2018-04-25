package com.jd.bluedragon.distribution.waybill.service;

import java.util.Date;
import java.util.List;

import com.jd.bluedragon.distribution.half.domain.PackageHalf;
import com.jd.bluedragon.distribution.half.domain.PackageHalfDetail;
import com.jd.bluedragon.distribution.task.domain.Task;

public interface WaybillStatusService {

	/** 将分拣系统各操作日志回传到运单系统 */
	void sendModifyWaybillStatusNotify(List<Task> tasks) throws Exception;
	/** 回传全程跟踪 */
	void sendModifyWaybillTrackNotify(List<Task> tasks) throws Exception;
	/** 置运单状态为妥投*/
	void sendModifyWaybillStatusFinished(Task task) throws Exception ;

	/**
	 * 包裹半收同步 运单状态
	 * @param packageHalfDetails
	 * @param waybillOpeType
	 * @param OperatorId
	 * @param OperatorName
	 * @param operateTime
	 * @return
	 */
	boolean batchUpdateWaybillPartByOperateType(PackageHalf packageHalf,List<PackageHalfDetail> packageHalfDetails, Integer waybillOpeType, Integer OperatorId, String OperatorName, Date operateTime,Integer orgId);

}
