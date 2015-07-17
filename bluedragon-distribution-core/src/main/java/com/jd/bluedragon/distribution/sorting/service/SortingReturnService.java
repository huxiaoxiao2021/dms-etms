package com.jd.bluedragon.distribution.sorting.service;

import com.jd.bluedragon.distribution.sorting.domain.SortingReturn;
import com.jd.bluedragon.distribution.task.domain.Task;

public interface SortingReturnService {

	/**
	 * 分拣退货数据保存成功
	 */
	public static final Integer ADDRETURN_OK = 0;

	/**
	 * 分拣退货数据失败-已经发货不允许退货
	 */
	public static final Integer ADDRETURN_ISSEND = 1;

	/**
	 * 分拣退货数据失败-调用DAO错误
	 */
	public static final Integer ADDRETURN_ERROR = -1;

	/**
	 * 反调度操作—操作反调度
	 */
	public static final Integer REDISPATCH_YES = 1;
	
	/**
	 * 反调度操作—未操作反调度
	 */
	public static final Integer REDISPATCH_NO = 2;
	
	/**
	 * 反调度操作—查询异常
	 */
	public static final Integer REDISPATCH_ERROR = 3;
	
	/**
	 * 根据分拣任务数据生成分拣退货操作信息
	 *
	 * @param task
	 */
	void doSortingReturn(Task task) throws Exception;

	/**
	 * 增加分拣退货数据
	 *
	 * @param SortingReturn
	 *            returns
	 */
	Integer doAddReturn(SortingReturn returns);
	
	/**
	 * 查询是否已经操作了分拣退货
	 * @param packageCodeOrWaybillCode
	 * @return
	 */
	Boolean exists(String packageCodeOrWaybillCode);
	
	
	/**
	 * 查询包裹是否进行过站点发调度操作
	 * @param packageCode
	 * @return
	 */
	Integer checkReDispatch(String packageCode);
}
