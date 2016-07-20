package com.jd.bluedragon.distribution.waybill.service;

import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.etms.waybill.dto.BigWaybillDto;

public interface WaybillService {

	BigWaybillDto getWaybill(String waybillCode);

	BigWaybillDto getWaybillProduct(String waybillCode);
	
	/**
	 * 获取运单状态接口
	 * */
    BigWaybillDto getWaybillState(String waybillCode);

    /***
     * 处理task_waybill的任务
     * @param task
     * @return
     */
    Boolean doWaybillStatusTask(Task task);

    /***
     * 处理task_pop的任务
     * @param task
     * @return
     */
    Boolean doWaybillTraceTask(Task task);
}
