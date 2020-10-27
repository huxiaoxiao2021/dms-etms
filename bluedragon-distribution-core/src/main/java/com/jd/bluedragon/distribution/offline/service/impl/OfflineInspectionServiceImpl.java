package com.jd.bluedragon.distribution.offline.service.impl;

import com.jd.bluedragon.distribution.inspection.InspectionBizSourceEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.request.InspectionRequest;
import com.jd.bluedragon.distribution.api.request.OfflineLogRequest;
import com.jd.bluedragon.distribution.api.request.TaskRequest;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.offline.service.OfflineService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.utils.JsonHelper;

@Service("offlineInspectionService")
public class OfflineInspectionServiceImpl implements OfflineService {
	@Autowired
	private TaskService taskService;
	@Autowired
	private BoxService boxService;

	@Override
	public int parseToTask(OfflineLogRequest offlineLogRequest) {
		InspectionRequest inspectionRequest = new InspectionRequest();
		inspectionRequest.setSealBoxCode("");
		inspectionRequest
				.setBoxCode((offlineLogRequest.getBoxCode() == null ? ""
						: offlineLogRequest.getBoxCode()));
		inspectionRequest.setPackageBarOrWaybillCode(getPackageBarOrWaybillCode(offlineLogRequest));
		inspectionRequest.setExceptionType((offlineLogRequest
				.getExceptionType() == null ? "" : offlineLogRequest
				.getExceptionType()));
		inspectionRequest
				.setOperateType((offlineLogRequest.getOperateType() == null ? Integer.valueOf(0)
						: offlineLogRequest.getOperateType()));
		
		if(Constants.BUSSINESS_TYPE_THIRD_PARTY == offlineLogRequest.getBusinessType().intValue()){
		Box box = boxService.findBoxByCode(offlineLogRequest.getBoxCode());
		inspectionRequest.setReceiveSiteCode(box.getReceiveSiteCode());
		inspectionRequest.setSiteCode(box.getCreateSiteCode());
		inspectionRequest.setSiteName(box.getCreateSiteName());
		}else{
        inspectionRequest.setReceiveSiteCode((offlineLogRequest
		.getReceiveSiteCode() == null ? Integer.valueOf(0) : offlineLogRequest
		.getReceiveSiteCode()));
        inspectionRequest.setSiteCode(offlineLogRequest.getSiteCode());
		inspectionRequest.setSiteName(offlineLogRequest.getSiteName());
		}
		
		inspectionRequest.setId(0);
		inspectionRequest.setBusinessType(offlineLogRequest.getBusinessType());
		inspectionRequest.setUserCode(offlineLogRequest.getUserCode());
		inspectionRequest.setUserName(offlineLogRequest.getUserName());
		
		inspectionRequest.setOperateTime(offlineLogRequest.getOperateTime());
		inspectionRequest.setBizSource(InspectionBizSourceEnum.OFFLINE_SELF_SUPPORT_INSPECTION.getCode());

		String eachJson = Constants.PUNCTUATION_OPEN_BRACKET
				+ JsonHelper.toJson(inspectionRequest)
				+ Constants.PUNCTUATION_CLOSE_BRACKET;

		TaskRequest taskRequest = new TaskRequest();
		taskRequest.setType(Task.TASK_TYPE_INSPECTION);
		taskRequest.setSiteCode(inspectionRequest.getSiteCode());
		taskRequest
				.setKeyword1(String.valueOf(inspectionRequest.getSiteCode()));
		taskRequest.setKeyword2(offlineLogRequest.getBoxCode());
		taskRequest.setBoxCode(offlineLogRequest.getBoxCode());
		taskRequest.setReceiveSiteCode(inspectionRequest.getReceiveSiteCode());
		this.taskService.add(this.taskService.toTask(taskRequest, eachJson));
		return Constants.RESULT_SUCCESS;
	}

	private String getPackageBarOrWaybillCode(OfflineLogRequest offlineLogRequest) {
		if(offlineLogRequest.getPackageCode() == null||"".equals(offlineLogRequest.getPackageCode().trim()))
		 {
			return  offlineLogRequest.getWaybillCode()==null?"":offlineLogRequest.getWaybillCode();
		 }else{
			return  offlineLogRequest.getPackageCode();
		 }
	}

}
