package com.jd.bluedragon.distribution.partnerWaybill.service;

import java.util.List;

import com.jd.bluedragon.distribution.partnerWaybill.domain.PartnerWaybill;
import com.jd.bluedragon.distribution.task.domain.Task;

public interface PartnerWaybillService {
	
	
	/**
	 * 添加运单关联包裹信息
	 * */
	public boolean doCreateWayBillCode(PartnerWaybill wayBillCode);
	
	
	/**
	 * 处理运单号关联包裹信息
	 * @param inspections
	 * @param receiveType
	 * @return
	 */
	public boolean doWayBillCodesProcessed(List<Task> task);
	
	/**
	 * 解析json数据 json 串to WayBillCode 对象
	 * 
	 * */
	public PartnerWaybill doParse(Task task);
	
}
