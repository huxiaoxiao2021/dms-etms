package com.jd.bluedragon.distribution.worker.offline;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
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
import com.jd.bluedragon.utils.Md5Helper;
import com.jd.etms.vos.dto.CommonDto;
import com.alibaba.fastjson.JSON;
import com.jd.jmq.common.exception.JMQException;
import com.jd.jmq.common.message.Message;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

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

    @Qualifier("dmsBusinessOperateOfflineTaskSendProducer")
    @Autowired
    private DefaultJMQProducer dmsBusinessOperateOfflineTaskSendProducer;

    @Autowired
    private UccPropertyConfiguration uccPropertyConfiguration;

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
            log.info("OfflineCoreTaskExecutor.execute taskContext: {}", JSON.toJSONString(taskContext));
            JSONArray taskList = JSONObject.parseArray(body);
            Integer taskType = taskList.getJSONObject(0).getInteger("taskType");
            Integer createSiteCode = taskContext.getTask().getCreateSiteCode();
            // 除了空铁以外的离线操作不被允许，
            if (!Objects.equals(taskType,Task.TASK_TYPE_AR_RECEIVE)
                    && !Objects.equals(taskType, Task.TASK_TYPE_AR_SEND_REGISTER)
                    && !Objects.equals(taskType, Task.TASK_TYPE_AR_RECEIVE_AND_SEND)
                    && !uccPropertyConfiguration.isOffLineAllowedSite(createSiteCode)) {
                log.info("OfflineCoreTaskExecutor.execute--> 不被允许的操作场地: {}", createSiteCode);
                return false;
            }

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

	private void batchSendOfflineTask2Mq(TaskContext<Task> taskContext){
        Task task = taskContext.getTask();
        String body = task.getBody();
        JSONArray taskList = JSONObject.parseArray(body);
        // 发出离线任务mq，后续消费此消息进行后续处理
        try {
            String businessId = String.format("%s_%s_%s", task.getType(), task.getCreateSiteCode(), task.getReceiveSiteCode());
            List<Message> messageList = new ArrayList<>();
            for (Object taskItem : taskList) {
                Message message = new Message();
                message.setBusinessId(businessId);
                message.setText(JSON.toJSONString(taskItem));
                log.info("batchSendOfflineTask2Mq text: {}", message.getText());
                message.setTopic(dmsBusinessOperateOfflineTaskSendProducer.getTopic());
                messageList.add(message);
            }
            dmsBusinessOperateOfflineTaskSendProducer.batchSend(messageList);
        } catch (JMQException e) {
            log.error("offlineCoreTaskExecutor batchSendOfflineTask2Mq exception :{}", e.getMessage(), e);
        }
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
        NewSealVehicleResponse returnCommonDto = newsealVehicleService.oneClickFerrySeal(convertSearCar(body));
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
                log.info("OfflineCoreTaskExecutor.offlineCore offlineLogRequest {}", JSON.toJSONString(offlineLogRequest));
                // 校验encrypt字段
                String encrypt = offlineLogRequest.getEncrypt();
                //BusinessDataType PackageCode WaybillCode BoxCode BatchNo UserNo
                String encryptStr = offlineLogRequest.getUserCode() +
                        offlineLogRequest.getPackageCode() +
                        offlineLogRequest.getWaybillCode() +
                        offlineLogRequest.getBoxCode() +
                        offlineLogRequest.getBatchCode() +
                        offlineLogRequest.getOperateTime();

                if (!Objects.equals(Md5Helper.encode(encryptStr), encrypt)) {
                    log.warn("OfflineCoreTaskExecutor.offlineCore-->校验encrypt字段失败，请排查数据来源,{}", JSON.toJSONString(offlineLogRequest));
                }

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
                    // 发货操作时间 +30 -> +5
                    offlineLogRequest.setOperateTime(DateHelper.formatDate(DateHelper.add(operateTime,
                            Calendar.MILLISECOND, Constants.DELIVERY_DELAY_TIME), Constants.DATE_TIME_MS_FORMAT));
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
                    offlineLog.setMethodName("OfflineCoreTaskExecutor#offlineCore");
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
            // 校验encrypt字段
            String encrypt = obj.getString("encrypt");
            //BusinessDataType PackageCode WaybillCode BoxCode BatchNo UserNo
            String encryptStr = obj.getString("userCode") +
                    obj.getString("packageCode") +
                    obj.getString("waybillCode") +
                    obj.getString("boxCode") +
                    obj.getString("batchCode");

            if (!Objects.equals(Md5Helper.encode(encryptStr), encrypt)) {
                log.warn("OfflineCoreTaskExecutor.convertSearCar-->校验encrypt字段失败，请排查数据来源,{}", JSON.toJSONString(obj));
            }
            sealCarDtos.add(scDto);
        }

        return sealCarDtos;
    }

    public static void main(String[] args) {
        System.out.println(Md5Helper.encode("17331JDX000227451346-1-2-JDX000227451346-1-2-910-39-202302031022336340001-01-01 00:00:00"));
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
