package com.jd.bluedragon.distribution.worker.offline.redis;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.request.OfflineLogRequest;
import com.jd.bluedragon.distribution.framework.RedisSingleScheduler;
import com.jd.bluedragon.distribution.offline.domain.OfflineLog;
import com.jd.bluedragon.distribution.offline.service.OfflineLogService;
import com.jd.bluedragon.distribution.offline.service.OfflineService;
import com.jd.bluedragon.distribution.offline.service.OfflineSortingService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;

public class OfflineCoreRedisTask extends RedisSingleScheduler {

	@Autowired
	private OfflineLogService offlineLogService;

	@Resource(name = "offlineInspectionService")
	private OfflineService offlineInspectionService;

	@Resource(name = "offlineReceiveService")
	private OfflineService offlineReceiveService;

	@Resource(name = "offlineDeliveryService")
	private OfflineService offlineDeliveryService;

	@Resource(name = "offlineAcarAbillDeliveryService")
	private OfflineService offlineAcarAbillDeliveryService;

	@Resource(name = "offlinePopPickupService")
	private OfflineService offlinePopPickupService;

	@Autowired
	private OfflineSortingService offlineSortingService;

	@Override
	public boolean executeSingleTask(Task task, String ownSign)
			throws Exception {

		String body = task.getBody();
		if (StringUtils.isBlank(body)) {
			return false;
		}
		try {
			OfflineLogRequest[] offlineLogRequests = JsonHelper.jsonToArray(
					body, OfflineLogRequest[].class);

			for (OfflineLogRequest offlineLogRequest : offlineLogRequests) {

				int resultCode = 0;
				try {

					if (Task.TASK_TYPE_RECEIVE.equals(offlineLogRequest
							.getTaskType())) {
						// 分拣中心收货
						resultCode = this.offlineReceiveService
								.parseToTask(offlineLogRequest);
					} else if (Task.TASK_TYPE_INSPECTION
							.equals(offlineLogRequest.getTaskType())) {
						// 分拣中心验货
						resultCode = this.offlineInspectionService
								.parseToTask(offlineLogRequest);
					} else if (Task.TASK_TYPE_SORTING.equals(offlineLogRequest
							.getTaskType())) {
						// 分拣
						resultCode = this.offlineSortingService
								.insert(offlineLogRequest);
					} else if (Task.TASK_TYPE_SEAL_BOX.equals(offlineLogRequest
							.getTaskType())) {// 分拣封箱
						resultCode = this.offlineSortingService
								.insertSealBox(offlineLogRequest);
					} else if (Task.TASK_TYPE_OFFLINE_EXCEEDAREA
							.equals(offlineLogRequest.getTaskType())) {// 三方超区退货
						resultCode = this.offlineSortingService
								.exceedArea(offlineLogRequest);
					} else if (Task.TASK_TYPE_SEND_DELIVERY
							.equals(offlineLogRequest.getTaskType())) {
						// 发货
						resultCode = this.offlineDeliveryService
								.parseToTask(offlineLogRequest);
					} else if (Task.TASK_TYPE_ACARABILL_SEND_DELIVERY.equals(offlineLogRequest.getTaskType())) {
						// 一车一单发货
						resultCode = this.offlineAcarAbillDeliveryService.parseToTask(offlineLogRequest);
					} else if (Task.TASK_TYPE_BOUNDARY.equals(offlineLogRequest
							.getTaskType())) {
						// pop上门接货
						resultCode = this.offlinePopPickupService
								.parseToTask(offlineLogRequest);
					} else if (Task.CANCEL_SORTING.equals(offlineLogRequest
							.getTaskType())) {// 取消分拣
						resultCode = this.offlineSortingService
								.cancelSorting(offlineLogRequest);
					} else if (Task.CANCEL_THIRD_INSPECTION
							.equals(offlineLogRequest.getTaskType())) {// 取消三方验货
						resultCode = this.offlineSortingService
								.cancelThirdInspection(offlineLogRequest);
						if (resultCode < 1) {
							return true;
						}
					}

					if ((Task.TASK_TYPE_SEND_DELIVERY.equals(offlineLogRequest.getTaskType())
							|| Task.TASK_TYPE_ACARABILL_SEND_DELIVERY.equals(offlineLogRequest.getTaskType()))
							&& resultCode > 0) {
						// 日志已处理，无需再处理
						continue;
					}

				} catch (Exception e) {
					this.logger.error("OfflineCoreTask--> 服务处理异常：【" + body
							+ "】：", e);
					resultCode = 0;
				}

				try {

					OfflineLog offlineLog = requestToOffline(offlineLogRequest);
					if (resultCode > Constants.RESULT_FAIL) {
						// 正常
						offlineLog.setStatus(Constants.RESULT_SUCCESS);
					} else {
						offlineLog.setStatus(Constants.RESULT_FAIL);
					}
					this.offlineLogService.addOfflineLog(offlineLog);

				} catch (Exception e) {
					this.logger.error("OfflineCoreTask--> 插入日志异常：【" + body
							+ "】：", e);
				}
			}

		} catch (Exception e) {
			this.logger.error("OfflineCoreTask execute--> 转换body异常body【" + body
					+ "】：", e);
			return false;
		}

		return true;
	}

	private OfflineLog requestToOffline(OfflineLogRequest offlineLogRequest) {
		if (offlineLogRequest == null) {
			return null;
		}
		OfflineLog offlineLog = new OfflineLog();
		offlineLog.setBoxCode(offlineLogRequest.getBoxCode());
		offlineLog.setBusinessType(offlineLogRequest.getBusinessType());
		offlineLog.setCreateSiteCode(offlineLogRequest.getSiteCode());
		offlineLog.setCreateSiteName(offlineLogRequest.getSiteName());
		offlineLog.setCreateUser(offlineLogRequest.getUserName());
		offlineLog.setCreateUserCode(offlineLogRequest.getUserCode());
		offlineLog.setExceptionType(offlineLogRequest.getExceptionType());
		offlineLog.setOperateTime(DateHelper.parseDate(offlineLogRequest
				.getOperateTime(), Constants.DATE_TIME_MS_FORMAT));
		offlineLog.setOperateType(offlineLogRequest.getOperateType());

		offlineLog.setWaybillCode(offlineLogRequest.getWaybillCode());
		offlineLog.setPackageCode(offlineLogRequest.getPackageCode());
		offlineLog.setReceiveSiteCode(offlineLogRequest.getReceiveSiteCode());
		offlineLog.setSealBoxCode(offlineLogRequest.getSealBoxCode());
		offlineLog.setSendCode(offlineLogRequest.getBatchCode());
		offlineLog.setSendUserCode(offlineLogRequest.getSendUserCode());
		offlineLog.setSendUser(offlineLogRequest.getSendUser());
		offlineLog.setShieldsCarCode(offlineLogRequest.getShieldsCarCode());
		offlineLog.setTaskType(offlineLogRequest.getTaskType());
		offlineLog.setVehicleCode(offlineLogRequest.getCarCode());
		offlineLog.setVolume(offlineLogRequest.getVolume());
		offlineLog.setWeight(offlineLogRequest.getWeight());
		offlineLog.setTurnoverBoxCode(offlineLogRequest.getTurnoverBoxCode());

		return offlineLog;
	}

}
