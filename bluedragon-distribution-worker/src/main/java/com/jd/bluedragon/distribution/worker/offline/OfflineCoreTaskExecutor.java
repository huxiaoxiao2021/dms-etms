package com.jd.bluedragon.distribution.worker.offline;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.request.OfflineLogRequest;
import com.jd.bluedragon.distribution.api.response.NewSealVehicleResponse;
import com.jd.bluedragon.distribution.offline.domain.OfflineLog;
import com.jd.bluedragon.distribution.offline.service.OfflineLogService;
import com.jd.bluedragon.distribution.offline.service.OfflineService;
import com.jd.bluedragon.distribution.offline.service.OfflineSortingService;
import com.jd.bluedragon.distribution.seal.service.NewSealVehicleService;
import com.jd.bluedragon.distribution.task.domain.DmsTaskExecutor;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.domain.TaskContext;
import com.jd.bluedragon.distribution.transport.service.ArSendRegisterService;
import com.jd.bluedragon.distribution.wss.dto.SealCarDto;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.vos.dto.CommonDto;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 
 * @ClassName: OfflineCoreTaskExecutor
 * @Description: 离线worker处理器
 * @author: wuyoude
 * @date: 2018年1月11日 下午11:26:46
 *
 */
@Service("offlineCoreTaskExecutor")
public class OfflineCoreTaskExecutor extends DmsTaskExecutor<Task> {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

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

    @Autowired
    private NewSealVehicleService newsealVehicleService;
    
	@Resource(name = "offlineArReceiveService")
	private OfflineService offlineArReceiveService;
    @Autowired
    private ArSendRegisterService arSendRegisterService;
    /**
     * 复合任务多个任务同时插入时，延迟的秒数，默认30s
     */
    private int delaySeconds = 30;
    
	@Override
	public Task parse(Task task, String ownSign) {
		return task;
	}
	@Override
	public boolean execute(TaskContext<Task> taskContext, String ownSign){
        boolean result = false;
		String body = taskContext.getTask().getBody();
		if (StringUtils.isBlank(body)) {
			return result;
		}
		try {
            Integer taskType = JSONObject.parseArray(body).getJSONObject(0).getInteger("taskType");
            if(Task.TASK_TYPE_SEAL_OFFLINE.equals(taskType)){
                result = offlineSeal(body);
            }else if (Task.TASK_TYPE_FERRY_SEAL_OFFLINE
                    .equals(taskType)){
                result = offlineSealFerry(body);
            } else if (Task.TASK_TYPE_AR_SEND_REGISTER.equals(taskType)){
                result = arSendRegisterService.executeOfflineTask(body);
            }else{
                result = offlineCore(body);
            }
		} catch (Exception e) {
			this.log.error("OfflineCoreTask execute--> 转换body异常body【{}】：",body, e);
		}
		return result;
	}

    /**
     * 离线封车
     * @param body
     * @return
     */
    private boolean offlineSeal(String body){
        boolean result = false;
        CommonDto<String> returnCommonDto = newsealVehicleService.offlineSeal(convertSearCar(body));
        if(returnCommonDto != null && Constants.RESULT_SUCCESS == returnCommonDto.getCode()){
            result = true;
        }
        return result;
    }

    /**
     * 离线传摆封车
     * @param body
     * @return
     */
    private boolean offlineSealFerry(String body){
        boolean result = false;
        NewSealVehicleResponse returnCommonDto = newsealVehicleService.offlineFerrySeal(convertSearCar(body));
        if(returnCommonDto != null && NewSealVehicleResponse.CODE_OK.equals(returnCommonDto.getCode())){
            result = true;
        }
        return result;
    }

    /**
     * 核心分拣相关业务离线操作
     * @param body
     * @return
     */
    private boolean offlineCore(String body){
        OfflineLogRequest[] offlineLogRequests = JsonHelper.jsonToArray(body, OfflineLogRequest[].class);
        for (OfflineLogRequest offlineLogRequest : offlineLogRequests) {
            int resultCode = 0;
            try {
                if (Task.TASK_TYPE_RECEIVE.equals(offlineLogRequest.getTaskType())) {
                    // 分拣中心收货
                    resultCode = this.offlineReceiveService.parseToTask(offlineLogRequest);
                } else if (Task.TASK_TYPE_INSPECTION.equals(offlineLogRequest.getTaskType())) {
                    // 分拣中心验货
                    resultCode = this.offlineInspectionService.parseToTask(offlineLogRequest);
                } else if (Task.TASK_TYPE_SORTING.equals(offlineLogRequest.getTaskType())) {
                    // 分拣
                    resultCode = this.offlineSortingService.insert(offlineLogRequest);
                } else if (Task.TASK_TYPE_SEAL_BOX.equals(offlineLogRequest.getTaskType())) {
                    // 分拣封箱
                    resultCode = this.offlineSortingService.insertSealBox(offlineLogRequest);
                } else if (Task.TASK_TYPE_OFFLINE_EXCEEDAREA.equals(offlineLogRequest.getTaskType())) {
                    // 三方超区退货
                    resultCode = this.offlineSortingService.exceedArea(offlineLogRequest);
                } else if (Task.TASK_TYPE_SEND_DELIVERY.equals(offlineLogRequest.getTaskType())) {
                    // 发货
                    resultCode = this.offlineDeliveryService.parseToTask(offlineLogRequest);
                } else if (Task.TASK_TYPE_ACARABILL_SEND_DELIVERY.equals(offlineLogRequest.getTaskType())) {
                    // 一车一单发货
                    resultCode = this.offlineAcarAbillDeliveryService.parseToTask(offlineLogRequest);
                }else if (Task.TASK_TYPE_BOUNDARY.equals(offlineLogRequest.getTaskType())) {
                    // pop上门接货
                    resultCode = this.offlinePopPickupService.parseToTask(offlineLogRequest);
                } else if (Task.CANCEL_SORTING.equals(offlineLogRequest.getTaskType())) {
                    // 取消分拣
                    resultCode = this.offlineSortingService.cancelSorting(offlineLogRequest);
                } else if (Task.CANCEL_THIRD_INSPECTION.equals(offlineLogRequest.getTaskType())) {
                    // 取消三方验货
                    resultCode = this.offlineSortingService.cancelThirdInspection(offlineLogRequest);
                    if (resultCode < 1) {
                        return true;
                    }
                } else if (Task.TASK_TYPE_AR_RECEIVE.equals(offlineLogRequest.getTaskType())) {
                	//空铁提货
                	resultCode = offlineArReceiveService.parseToTask(offlineLogRequest);
                } else if (Task.TASK_TYPE_AR_RECEIVE_AND_SEND.equals(offlineLogRequest.getTaskType())) {
                    //空铁提货并发货
                    offlineArReceiveService.parseToTask(offlineLogRequest);
                    Date operateTime = DateHelper.parseAllFormatDateTime(offlineLogRequest.getOperateTime());
                    // 发货操作时间 +30
                    offlineLogRequest.setOperateTime(DateHelper.formatDate(DateHelper.add(operateTime, Calendar.SECOND, delaySeconds), Constants.DATE_TIME_MS_FORMAT));
                    resultCode = this.offlineAcarAbillDeliveryService.parseToTask(offlineLogRequest);
                }
                if ((Task.TASK_TYPE_SEND_DELIVERY.equals(offlineLogRequest.getTaskType()) || Task.TASK_TYPE_ACARABILL_SEND_DELIVERY.equals(offlineLogRequest.getTaskType())) && resultCode > 0) {
                    // 日志已处理，无需再处理
                    continue;
                }
            } catch (Exception e) {
                this.log.error("OfflineCoreTask--> 服务处理异常：【{}】",body, e);
                resultCode = 0;
            }

            try {
                OfflineLog offlineLog = requestToOffline(offlineLogRequest);
                // 正常
                if (resultCode > Constants.RESULT_FAIL) {
                    offlineLog.setStatus(Constants.RESULT_SUCCESS);
                } else {
                    offlineLog.setStatus(Constants.RESULT_FAIL);
                }
                if (offlineLog.getBoxCode() != null && offlineLog.getBoxCode().length() > Constants.BOX_CODE_DB_COLUMN_LENGTH_LIMIT) {
                    this.log.warn("箱号超长，OfflineCoreTask插入日志失败：【{}】",body);
                } else {
                    this.offlineLogService.addOfflineLog(offlineLog);
                }
            } catch (Exception e) {
                this.log.error("OfflineCoreTask--> 插入日志异常：【{}】",body, e);
            }
            if (resultCode == Constants.RESULT_FAIL){
                return false;
            }
        }
        return true;
    }


    /**
     * 业务数据转换为操作记录实体
     * @param offlineLogRequest
     * @return
     */
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
		offlineLog.setOperateTime(DateHelper.parseDate(offlineLogRequest.getOperateTime(),
                Constants.DATE_TIME_MS_FORMAT,Constants.DATE_TIME_FORMAT));
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

    /**
     * 适配离线封车JSON数据
     * @param body
     * @return
     */
    private List<SealCarDto> convertSearCar(String body){
        JSONArray temp = JSONObject.parseArray(body);
        List<SealCarDto> sealCarDtos = new ArrayList<SealCarDto>(temp.size());
        for (int i = 0; i < temp.size(); i++) {
            JSONObject obj = temp.getJSONObject(i);
            SealCarDto scDto = new SealCarDto();
            scDto.setBatchCodes(Arrays.asList(obj.getString("batchCode").split(Constants.SEPARATOR_COMMA)));
            scDto.setSealCarTime(obj.getString("operateTime"));
            scDto.setSealCodes(Arrays.asList(obj.getString("shieldsCarCode").split(Constants.SEPARATOR_COMMA)));
            scDto.setSealSiteId(obj.getInteger("receiveSiteCode"));
            scDto.setSealSiteName(obj.getString("siteName"));
            scDto.setSealUserCode(obj.getInteger("userCode").toString());
            scDto.setSealUserName(obj.getString("userName"));
            scDto.setSource(Constants.SEAL_SOURCE);
            scDto.setTransportCode(obj.getString("sealBoxCode"));
            scDto.setVehicleNumber(obj.getString("carCode"));
            scDto.setVolume(obj.getDouble("volume"));
            scDto.setWeight(obj.getDouble("weight"));
            sealCarDtos.add(scDto);
        }

        return sealCarDtos;
    }
	/**
	 * @return the delaySeconds
	 */
	public int getDelaySeconds() {
		return delaySeconds;
	}
	/**
	 * @param delaySeconds the delaySeconds to set
	 */
	public void setDelaySeconds(int delaySeconds) {
		this.delaySeconds = delaySeconds;
	}
}
