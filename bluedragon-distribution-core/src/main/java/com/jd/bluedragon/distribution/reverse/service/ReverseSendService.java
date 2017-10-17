package com.jd.bluedragon.distribution.reverse.service;

import java.util.Map;

import com.jd.bluedragon.distribution.reverse.domain.ReverseSendAsiaWms;
import com.jd.bluedragon.distribution.reverse.domain.ReverseSendWms;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;

public interface ReverseSendService {

	boolean findSendwaybillMessage(Task task) throws Exception;

	boolean sendReverseMessageToSpwms(SendM m, Integer orgId, String storeId) throws Exception;

	boolean sendReverseMessageToWms(SendM sendM, BaseStaffSiteOrgDto bDto,String taskId) throws Exception;
	
	public boolean sendAsiaWMS(ReverseSendAsiaWms send, String wallBillCode, SendM sendM, Map.Entry entry, int lossCount,
			BaseStaffSiteOrgDto bDto, Map<String, String> isPackageFullMap) throws Exception;
	
	public boolean sendWMS(ReverseSendWms send, String wallBillCode, SendM sendM, Map.Entry entry, int lossCount,
			BaseStaffSiteOrgDto bDto,String taskId) throws Exception;

}