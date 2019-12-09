package com.jd.bluedragon.distribution.offline.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.distribution.api.request.OfflineLogRequest;
import com.jd.bluedragon.distribution.api.request.PopPickupRequest;
import com.jd.bluedragon.distribution.api.request.TaskRequest;
import com.jd.bluedragon.distribution.offline.service.OfflineService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.utils.JsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service("offlinePopPickupService")
public class OfflinePopPickupServiceImpl implements OfflineService {
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private TaskService taskService;
	
	@Autowired
	private WaybillCommonService waybillCommonService;
	
	public int parseToTask(OfflineLogRequest offlineLogRequest) {
		// 验证运单号
		Waybill waybill = waybillCommonService.findByWaybillCode(offlineLogRequest.getWaybillCode());
		if (waybill == null || waybill.getPopSupId()==null || waybill.getQuantity()==null) {
			this.log.warn("OfflinePopPickupServiceImpl--> 根据运单号【{}】验证pop商家ID和数量失败",offlineLogRequest.getWaybillCode());
			return Constants.RESULT_FAIL;
		}
		PopPickupRequest popPickupRequest =new PopPickupRequest();
		popPickupRequest.setPopBusinessCode(String.valueOf(waybill.getPopSupId()));
		popPickupRequest.setPopBusinessName(waybill.getPopSupName());
		popPickupRequest.setWaybillCode(offlineLogRequest.getWaybillCode());
		popPickupRequest.setPackageBarcode((offlineLogRequest.getPackageCode()==null?"":offlineLogRequest.getPackageCode()));
		popPickupRequest.setPackageNumber(waybill.getQuantity());
		popPickupRequest.setIsCancel((offlineLogRequest.getId()==null?Integer.valueOf(0):offlineLogRequest.getId()));
		popPickupRequest.setId(offlineLogRequest.getId());
		popPickupRequest.setBusinessType(offlineLogRequest.getBusinessType());
		popPickupRequest.setUserCode(offlineLogRequest.getUserCode());
		popPickupRequest.setUserName(offlineLogRequest.getUserName());
		popPickupRequest.setSiteCode(offlineLogRequest.getSiteCode());
		popPickupRequest.setSiteName(offlineLogRequest.getSiteName());
		popPickupRequest.setOperateTime(offlineLogRequest.getOperateTime());
		popPickupRequest.setCarCode(offlineLogRequest.getCarCode());
		
		String eachJson = Constants.PUNCTUATION_OPEN_BRACKET
				+ JsonHelper.toJson(popPickupRequest)
				+ Constants.PUNCTUATION_CLOSE_BRACKET;

		TaskRequest taskRequest = new TaskRequest();
		taskRequest.setType(Task.TASK_TYPE_BOUNDARY);
		taskRequest.setSiteCode(offlineLogRequest.getSiteCode());
		taskRequest
				.setKeyword1("offline");
		taskRequest.setKeyword2(String.valueOf(offlineLogRequest.getSiteCode()));
		taskRequest.setReceiveSiteCode(offlineLogRequest.getReceiveSiteCode());
		this.taskService.add(this.taskService.toTask(taskRequest, eachJson));
		
		return Constants.RESULT_SUCCESS;
	}

}
