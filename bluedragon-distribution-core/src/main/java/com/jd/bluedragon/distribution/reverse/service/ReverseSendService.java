package com.jd.bluedragon.distribution.reverse.service;

import java.util.Map;

import com.jd.bluedragon.distribution.reverse.domain.ReverseSendAsiaWms;
import com.jd.bluedragon.distribution.reverse.domain.ReverseSendWms;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.etms.basic.dto.BaseStaffSiteOrgDto;

public interface ReverseSendService {

	boolean findSendwaybillMessage(Task task) throws Exception;

	boolean sendReverseMessageToSpwms(SendM m, Integer orgId, String storeId) throws Exception;

	boolean sendReverseMessageToWms(SendM sendM, BaseStaffSiteOrgDto bDto) throws Exception;
	
	public void sendAsiaWMS(ReverseSendAsiaWms send, String wallBillCode, SendM sendM, Map.Entry entry, int lossCount,
			BaseStaffSiteOrgDto bDto, Map<String, String> isPackageFullMap) throws Exception;
	
	public void sendWMS(ReverseSendWms send, String wallBillCode, SendM sendM, Map.Entry entry, int lossCount,
			BaseStaffSiteOrgDto bDto) throws Exception;

}