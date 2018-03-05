package com.jd.bluedragon.distribution.offline.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.request.ArReceiveRequest;
import com.jd.bluedragon.distribution.api.request.OfflineLogRequest;
import com.jd.bluedragon.distribution.api.request.TaskRequest;
import com.jd.bluedragon.distribution.offline.service.OfflineService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.utils.JsonHelper;

@Service("offlineArReceiveService")
public class OfflineArReceiveServiceImpl implements OfflineService {
	@Autowired
	private TaskService taskService;

	@Override
	public int parseToTask(OfflineLogRequest offlineLogRequest) {
		ArReceiveRequest receiveRequest = new ArReceiveRequest();
		receiveRequest
				.setShieldsCarCode((offlineLogRequest.getShieldsCarCode() == null ? ""
						: offlineLogRequest.getShieldsCarCode()));
		receiveRequest.setCarCode((offlineLogRequest.getCarCode() == null ? ""
				: offlineLogRequest.getCarCode()));
		receiveRequest
				.setPackOrBox(getPackOrBox(offlineLogRequest));
		receiveRequest.setId(0);
		receiveRequest.setBusinessType(offlineLogRequest.getBusinessType());
		receiveRequest.setUserCode(offlineLogRequest.getUserCode());
		receiveRequest.setUserName(offlineLogRequest.getUserName());
		receiveRequest.setSiteCode(offlineLogRequest.getSiteCode());
		receiveRequest.setSiteName(offlineLogRequest.getSiteName());
		receiveRequest.setOperateTime(offlineLogRequest.getOperateTime());
		receiveRequest
				.setSealBoxCode((offlineLogRequest.getSealBoxCode() == null ? ""
						: offlineLogRequest.getSealBoxCode()));
		receiveRequest.setTurnoverBoxCode((offlineLogRequest
				.getTurnoverBoxCode() == null ? "" : offlineLogRequest
				.getTurnoverBoxCode()));
		receiveRequest.setShuttleBusType(offlineLogRequest.getOperateType());
		receiveRequest.setShuttleBusNum(offlineLogRequest.getCarCode());
		receiveRequest.setRemark(offlineLogRequest.getDemo());
		String eachJson = Constants.PUNCTUATION_OPEN_BRACKET
				+ JsonHelper.toJson(receiveRequest)
				+ Constants.PUNCTUATION_CLOSE_BRACKET;

		TaskRequest taskRequest = new TaskRequest();
		taskRequest.setType(Task.TASK_TYPE_AR_RECEIVE);
		taskRequest.setSiteCode(offlineLogRequest.getSiteCode());
		taskRequest
				.setKeyword1(String.valueOf(offlineLogRequest.getSiteCode()));
		taskRequest.setKeyword2(offlineLogRequest.getBoxCode());
		taskRequest.setBoxCode(offlineLogRequest.getBoxCode());
		taskRequest.setReceiveSiteCode(offlineLogRequest.getReceiveSiteCode());

		this.taskService.add(this.taskService.toTask(taskRequest, eachJson));
		return Constants.RESULT_SUCCESS;
	}

	private String getPackOrBox(OfflineLogRequest offlineLogRequest) {
		if(offlineLogRequest.getBoxCode() == null||"".equals(offlineLogRequest.getBoxCode().trim())){
		return offlineLogRequest.getPackageCode()==null?"":offlineLogRequest.getPackageCode();
		}else{
		return offlineLogRequest.getBoxCode();
		}
	}

}
