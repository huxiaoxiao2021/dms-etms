package com.jd.bluedragon.distribution.task.service;

import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.WaybillTraceManager;
import com.jd.bluedragon.core.redis.TaskModeAgent;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.enums.OperatorTypeEnum;
import com.jd.bluedragon.distribution.api.request.AutoSortingPackageDto;
import com.jd.bluedragon.distribution.api.request.SortingRequest;
import com.jd.bluedragon.distribution.api.request.TaskRequest;
import com.jd.bluedragon.distribution.api.response.InspectionECResponse;
import com.jd.bluedragon.distribution.api.response.TaskResponse;
import com.jd.bluedragon.distribution.auto.domain.UploadedPackage;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.box.domain.BoxRelation;
import com.jd.bluedragon.distribution.box.service.BoxRelationService;
import com.jd.bluedragon.distribution.inspection.InspectionBizSourceEnum;
import com.jd.bluedragon.distribution.inspection.domain.InspectionAS;
import com.jd.bluedragon.distribution.log.BusinessLogProfilerBuilder;
import com.jd.bluedragon.distribution.task.asynBuffer.DmsDynamicProducer;
import com.jd.bluedragon.distribution.task.dao.TaskDao;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.worker.service.TBTaskQueueService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.*;
import com.jd.bluedragon.utils.log.BusinessLogConstans;
import com.jd.dms.logger.external.BusinessLogProfiler;
import com.jd.dms.logger.external.LogEngine;
import com.jd.jsf.gd.util.RpcContext;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.framework.asynBuffer.producer.jmq.JmqTopicRouter;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.*;

import static com.jd.bluedragon.distribution.sorting.domain.SortingBizSourceEnum.AUTOMATIC_SORTING_MACHINE_SORTING;

@Service("taskService")
public class TaskServiceImpl implements TaskService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	/**
	 * 批量提交 一次提交数量
	 */
	private final static int batchSize = 1000;

	@Autowired
    private TaskDao taskDao;

	@Autowired
    private RedisTaskService redisTaskService;

	@Autowired
    private TaskModeAgent taskModeAgent;

	@Autowired
	private SysConfigService sysConfigService;

	@Autowired
    private BaseService baseService;

    @Autowired
    private JmqTopicRouter taskJmqTopicRouter;

    @Autowired
    private DmsDynamicProducer dynamicProducer;

	@Resource
	private UccPropertyConfiguration uccPropertyConfiguration;

	@Autowired
    private TBTaskQueueService tbTaskQueueService;

	@Autowired
	private WaybillTraceManager waybillTraceManager;

	@Autowired
	private BoxRelationService boxRelationService;

	@Autowired
	private LogEngine logEngine;

	@Autowired
	private AsynBufferDemotionUtil asynBufferDemotionUtil;

	@Value("${auto.task.upload.rpc.timeout:900}")
	private int autoTaskUploadRpcTimeOut;

	@JProfiler(jKey = "DMS.WEB.TaskServiceImpl.add", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public TaskResponse add(TaskRequest request) {
		//加入监控，开始
		CallerInfo info = Profiler.registerInfo("Bluedragon_dms_center.dms.method.task.add", false, true);

		Assert.notNull(request, "request must not be null");
		if (log.isInfoEnabled()) {
			log.info("TaskRequest [{}]",JsonHelper.toJson(request));
		}

		TaskResponse response = null;
		if (StringUtils.isBlank(request.getBody())) {
			response = new TaskResponse(JdResponse.CODE_PARAM_ERROR,
					JdResponse.MESSAGE_PARAM_ERROR);
			return response;
		}
		String json = request.getBody();
		Object[] array = JsonHelper.jsonToArray(json, Object[].class);

		if (array.length > 200) {
			return new TaskResponse(
					InspectionECResponse.CODE_PARAM_UPPER_LIMIT,
					InspectionECResponse.MESSAGE_PARAM_UPPER_LIMIT);
		}

		//加入监控结束
		Profiler.registerInfoEnd(info);
		CallerInfo info2 = Profiler.registerInfo("Bluedragon_dms_center.dms.method.task.add2", false, true);
		for (Object element : array) {
			if (Task.TASK_TYPE_REVERSE_SPWARE.equals(request.getType())) {
				Map<String, Object> reverseSpareMap = (Map<String, Object>) element;
				List<Map<String, Object>> reverseSpareDtos = (List<Map<String, Object>>) reverseSpareMap
						.get("data");
				for (Map<String, Object> dto : reverseSpareDtos) {
					List<Map<String, Object>> tempReverseSpareDtos = Arrays
							.asList(dto);
					reverseSpareMap.put("data", tempReverseSpareDtos);
					request.setKeyword2(String.valueOf(dto.get("spareCode")));
					String eachJson = Constants.PUNCTUATION_OPEN_BRACKET
							+ JsonHelper.toJson(reverseSpareMap)
							+ Constants.PUNCTUATION_CLOSE_BRACKET;
					this.taskAssemblingAndSave(request, eachJson);
				}
			} else if (Task.TASK_TYPE_OFFLINE.equals(request.getType())) {

				//离线限流
				if(asynBufferDemotionUtil.isDemotionOfSite(request.getSiteCode(),request.getBody())){
					//限流
					return new TaskResponse(
							JdResponse.CODE_BUSY,
							JdResponse.MESSAGE_BUSY);
				}


				Map<String, Object> itemTask = (Map<String, Object>) element;
				String operateTime = (String) itemTask.get("operateTime");
				String dateFormat = DateHelper.getDateFormat(operateTime);

				if (StringHelper.isNotEmpty(dateFormat)) {
					// 离线时间处理
					if(!offlineTimeDeal(itemTask)){
						continue;
					}
					String eachJson = Constants.PUNCTUATION_OPEN_BRACKET
							+ JsonHelper.toJson(itemTask)
							+ Constants.PUNCTUATION_CLOSE_BRACKET;
					this.taskAssemblingAndSave(request, eachJson);
				} else {
					log.warn("未知的离线任务时间格式【{}】，请注意代码适配问题。",operateTime);
				}

				Integer innerTaskType = null;
				try {
					if (null != itemTask.get("taskType")) {
						innerTaskType = Integer.valueOf(String.valueOf(itemTask.get("taskType")));
					}
				}
				catch (Exception ex) {
					log.error("转换taskType失败.", ex);
				}

				if (Task.TASK_TYPE_AR_RECEIVE.equals(innerTaskType)) {

					this.dealAirRecvRelationTask(request, itemTask);
				}
			}
			// 处理收货任务
			else if (Task.TASK_TYPE_RECEIVE.equals(request.getType())) {

				String eachJson = Constants.PUNCTUATION_OPEN_BRACKET
						+ JsonHelper.toJson(element)
						+ Constants.PUNCTUATION_CLOSE_BRACKET;
				this.taskAssemblingAndSave(request, eachJson);

				// BC箱号收货时，判断dms_box_relation是否存在绑定的箱号，同步处理收货
				this.dealRelationBoxReceiveTask(request, element);

			}
			else {
				String eachJson = Constants.PUNCTUATION_OPEN_BRACKET
						+ JsonHelper.toJson(element)
						+ Constants.PUNCTUATION_CLOSE_BRACKET;
				this.taskAssemblingAndSave(request, eachJson);
			}
		}
		Profiler.registerInfoEnd(info2);
		return new TaskResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK,
				DateHelper.formatDateTime(new Date()));
	}


	/**
	 * 离线时间处理
	 *  1、操作时间在系统时间之前且在96h之前，则返回false（此离线任务为超时无效数据丢弃不处理），否则返回true
	 *  2、操作时间在系统时间之后且在24h之后，则记录操作时间为系统时间，否则记录原始操作时间
	 * @param itemTask
	 */
	private boolean offlineTimeDeal(Map<String, Object> itemTask) {
		//离线任务 处理操作时间
		long startTime = System.currentTimeMillis();

		String operateTime = (String) itemTask.get("operateTime");
		String dateFormat = DateHelper.getDateFormat(operateTime);

		Date operateTimeDate = DateHelper.parseDate(operateTime, dateFormat);
		Date systemTimeDate = new Date();
		if(operateTimeDate == null){
			log.warn("操作时间格式不正确!");
			return false;
		}
		String newOperateTime = operateTime;
		if(operateTimeDate.before(systemTimeDate)){
			// 操作时间在系统时间之前
			int offlineBeforeNowLimit = uccPropertyConfiguration.getOfflineTaskOperateTimeBeforeNowLimitHours();
			Date limitDate = DateHelper.newTimeRangeHoursAgo(systemTimeDate, offlineBeforeNowLimit);
			if(operateTimeDate.before(limitDate)){
				log.error("离线任务的操作时间【{}】超过了系统时间【{}】设定上传时间范围【{}h】,此任务为超时无效数据，过滤不接收!",
						operateTime,DateHelper.formatDateTime(systemTimeDate),offlineBeforeNowLimit);
				recordBusinessLog(itemTask, operateTime, newOperateTime, startTime);
				return false;
			}
		} else {
			// 操作时间在系统时间之后
			int offlineAfterNowLimit = uccPropertyConfiguration.getOfflineTaskOperateTimeCorrectHours();
			newOperateTime = DateHelper.formatDate(
					DateHelper.adjustTimeToNow(DateHelper.parseDate(operateTime, dateFormat),offlineAfterNowLimit),
					dateFormat
			);
			if (StringHelper.isNotEmpty(newOperateTime) && !newOperateTime.equals(operateTime)) {
				log.warn("离线任务的操作时间【{}】超过了设定上传时间范围【{}】，已经被重置当前系统时间【{}】",
						operateTime, offlineAfterNowLimit, newOperateTime);
				log.warn("离线任务上传操作时间纠正，原始任务消息为：{}", JsonHelper.toJson(itemTask));

				// 设置操作时间为系统时间
				itemTask.put("operateTime", newOperateTime);
			}
		}
		recordBusinessLog(itemTask, operateTime, newOperateTime, startTime);
		return true;
	}

	/**
	 * 记录businessLog日志
	 * @param itemTask
	 * @param operateTime
	 * @param newOperateTime
	 * @param startTime
	 */
	private void recordBusinessLog(Map<String, Object> itemTask, String operateTime, String newOperateTime,long startTime) {
		try {
			JSONObject logRequest=new JSONObject();
			logRequest.putAll(itemTask);

			JSONObject logResponse=new JSONObject();
			logResponse.put("originOperateTime", operateTime);
			logResponse.put("correctOperateTime", newOperateTime);

			BusinessLogProfiler businessLogProfiler=new BusinessLogProfilerBuilder()
					.operateTypeEnum(BusinessLogConstans.OperateTypeEnum.OTHER_OTHER_OFFLINE)
					.methodName("TaskResource#add")
					.operateRequest(logRequest)
					.operateResponse(logResponse)
					.processTime(System.currentTimeMillis(),startTime)
					.build();

			logEngine.addLog(businessLogProfiler);
		}catch (Exception e){
			log.error("记录businessLog日志异常!",e);
		}
	}


	/**
	 * 从离线任务请求里获取箱号字段
	 * @param itemTask
	 * @return
	 */
	private String getBoxCodeFromOfflineRequest(Map<String, Object> itemTask) {
		String boxCode = String.valueOf(itemTask.get("boxCode"));
		if (StringUtils.isNotBlank(boxCode) && Constants.SPARE_CODE_PREFIX_DEFAULT.equalsIgnoreCase(boxCode)) {
			if (BusinessUtil.isBoxcode(boxCode)) {
				return boxCode;
			}
		}
		String packageCode = String.valueOf(itemTask.get("packageCode"));
		if (StringUtils.isNotBlank(packageCode) && Constants.SPARE_CODE_PREFIX_DEFAULT.equalsIgnoreCase(packageCode)) {
			if (BusinessUtil.isBoxcode(packageCode)) {
				return packageCode;
			}
		}

		return null;
	}

	/**
	 * 获取箱号关联的箱号
	 * @param packOrBox
	 * @return
	 */
	private List<BoxRelation> getBoxRelations(String packOrBox) {
		if (BusinessUtil.isBoxcode(packOrBox)) {
			InvokeResult<List<BoxRelation>> sr = boxRelationService.getRelationsByBoxCode(packOrBox);
			if (sr.codeSuccess() && CollectionUtils.isNotEmpty(sr.getData())) {
				return sr.getData();
			}
		}

		return null;
	}

	/**
	 * 空铁收货时，同步生成关联箱号的收货任务
	 * @param request
	 * @param itemTask
	 */
	private void dealAirRecvRelationTask(TaskRequest request, Map<String, Object> itemTask) {

		String boxCode = this.getBoxCodeFromOfflineRequest(itemTask);
		List<BoxRelation> boxRelations = this.getBoxRelations(boxCode);

		if (CollectionUtils.isNotEmpty(boxRelations)) {

			// 加入UMP监控
			CallerInfo info = Profiler.registerInfo("dms.etms.addReceiveTask.dealAirRecvRelationTask", false, true);

			for (BoxRelation boxRelation : boxRelations) {

				if (log.isInfoEnabled()) {
					log.info("插入关联箱号收货任务. boxCode:{}, relationBoxCode:{}", boxRelation.getBoxCode(), boxRelation.getRelationBoxCode());
				}
				// 收货任务只修改箱号字段，其它字段不变
				itemTask.put("boxCode", boxRelation.getRelationBoxCode()); // 当前为WJ箱号
				String eachJson = Constants.PUNCTUATION_OPEN_BRACKET
						+ JsonHelper.toJson(itemTask)
						+ Constants.PUNCTUATION_CLOSE_BRACKET;

				this.taskAssemblingAndSave(request, eachJson);
			}

			Profiler.registerInfoEnd(info);
		}
	}


	/**
	 * BC箱号收货时，绑定的WJ箱号同步收货
	 * @param request
	 * @param element
	 */
	private void dealRelationBoxReceiveTask(TaskRequest request, Object element) {

		@SuppressWarnings("unchecked")
		Map<String, Object> receiveMap = (Map<String, Object>) element;
		String packOrBox = String.valueOf(receiveMap.get("packOrBox"));

		List<BoxRelation> boxRelations = this.getBoxRelations(packOrBox);

		if (CollectionUtils.isNotEmpty(boxRelations)) {

			// 加入UMP监控
			CallerInfo info = Profiler.registerInfo("dms.etms.addReceiveTask.dealRelationBoxReceiveTask", false, true);

			for (BoxRelation boxRelation : boxRelations) {

				if (log.isInfoEnabled()) {
					log.info("插入关联箱号收货任务. boxCode:{}, relationBoxCode:{}", boxRelation.getBoxCode(), boxRelation.getRelationBoxCode());
				}
				// 收货任务只修改箱号和keyword2字段，其它字段不变
				receiveMap.put("packOrBox", boxRelation.getRelationBoxCode()); // 当前为WJ箱号
				String eachJson = Constants.PUNCTUATION_OPEN_BRACKET
						+ JsonHelper.toJson(receiveMap)
						+ Constants.PUNCTUATION_CLOSE_BRACKET;

				request.setKeyword2(boxRelation.getRelationBoxCode());

				this.taskAssemblingAndSave(request, eachJson);
			}

			Profiler.registerInfoEnd(info);
		}
	}

	private void taskAssemblingAndSave(TaskRequest request, String jsonStr) {
		Task task = toTask(request, jsonStr);
		if (task.getBoxCode() != null && task.getBoxCode().length() > Constants.BOX_CODE_DB_COLUMN_LENGTH_LIMIT) {
			log.warn("箱号超长，无法插入任务，参数：{}" , JsonHelper.toJson(task));
		} else {
			add(task, true);
		}
	}

	@Override
	public void addBatch(List<Task> tasks) {
		this.addBatch(tasks, false);
	}

	@Override
	public void addBatch(List<Task> tasks, boolean ifCheckTaskMode) {
		if (tasks != null && !tasks.isEmpty()) {
			Task firstTask = tasks.get(0);
			if (isDynamicProducerOn(firstTask)) {
				int size = tasks.size();
				if (size > batchSize) {
					int mod = size % batchSize;
					int times = size / batchSize;
					if (mod > 0) {
						times++;
					}
					for (int i = 0; i < times; i++) {
						int start = i * batchSize;
						int end = start + batchSize;
						if (end > size) {
							end = size;
						}
						dynamicProducer.send(tasks.subList(start, end));
					}
				} else {
					dynamicProducer.send(tasks);
				}
				return;
			}
			for (Task task : tasks) {
				doAddTask(task, false);
			}
		}
	}

    /**
     * @param task
     * @param ifCheckTaskMode
     * @return
     */
    @JProfiler(jKey= "DMSCORE.TaskService.add",mState = {JProEnum.TP})
	@Override
    public Integer add(Task task, boolean ifCheckTaskMode) {
    	if(log.isDebugEnabled()) {
        	log.debug("addTask：{} " ,JsonHelper.toJson(task));
        }
        Assert.notNull(task, "task must not be null");
		if (isDynamicProducerOn(task)) {
			String topic = taskJmqTopicRouter.getTopic(task);
			String umpKey = new StringBuilder("DMSCORE.TaskService.add.").append(topic==null?StringUtils.EMPTY:topic).toString();
			CallerInfo info = Profiler.registerInfo(umpKey, Constants.UMP_APP_NAME_DMSWEB, false, true);
			try {
				dynamicProducer.send(task);
				return 1;
			} finally {
				Profiler.registerInfoEnd(info);
			}
		}
		return doAddTask(task, ifCheckTaskMode);
    }


    public Boolean isDynamicProducerOn(Task task) {
        return null != taskJmqTopicRouter.getTopic(task) && !dynamicProducer.getProducerType().name().equals("TBSCHEDULE");
    }

    @Override
    public Integer doAddTask(Task task, boolean ifCheckTaskMode) {
        TaskDao routerDao = taskDao;
        if(Task.TASK_TYPE_PDA.equals(task.getType()) ){
            log.warn(" pda logs , box_code: {} [body]: {}",task.getBoxCode(),task.getBody());
            return 0;
        }

        if (ifCheckTaskMode && isRedisSwitchON()) {
			/* 如果task支持redis模式，首先考虑redis模式，如果redis模式失败则考虑数据库模式 */
            if (taskModeAgent.isRedisTaskModeSupported(task)) {
                boolean isRedisSucc = false;
                try {
                    //在插入redis前给executetime赋值，防止Task.getExecuteTime()给赋系统时间造成，redis中重复任务json串不同问题
                    task.setExecuteTime(new Date(0));
                    if (redisTaskService.add(task)) {
                        isRedisSucc = true;
                    }
                } catch (Exception e) {
                    log.error("保存任务失败：{}" , task.toString());
                }
                if (isRedisSucc) {
                    return 1;
                }
            }
        }

        //超长校验
		if (StringHelper.isNotEmpty(task.getBody()) && task.getBody().length() > 2000) {
			log.error("插入任务失败，body字段超长，参数：{}", JsonHelper.toJson(task));
			return 0;
		}

        //获取当前任务类型队列数量
        //随机生成队列数
        Map<String, Integer> allQueueSize = tbTaskQueueService.findAllQueueSize();
        int queueSize = task.findTaskQueueSize(allQueueSize);
        task.setQueueId(new Random().nextInt(queueSize));
        if ( this.isWaybillTask(task) || this.isSendTask(task) || Task.TASK_TYPE_SORTING.equals(task.getType())
                || Task.TASK_TYPE_RECEIVE.equals(task.getType()) || Task.TASK_TYPE_INSPECTION.equals(task.getType())
                || Task.TASK_TYPE_REVERSE_SPWARE.equals(task.getType()) || Task.TASK_TYPE_OFFLINE.equals(task.getType())
                || Task.TASK_TYPE_PUSH_MQ.equals(task.getType()) || Task.TASK_TYPE_AUTO_INSPECTION_PREPARE.equals(task.getType())
                || Task.TASK_TYPE_AUTO_SORTING_PREPARE.equals(task.getType()) || Task.TASK_TYPE_SORTING_EXCEPTION.equals(task.getType()) || Task.TASK_TYPE_ZHIPEI_SORTING.equals(task.getType())
                || Task.TASK_TYPE_GLOBAL_TRADE.equals(task.getType())||Task.TASK_TYPE_POP_PRINT_INSPECTION.equals(task.getType())) {     // 增加干线计费信息MQ去重
            if(!this.has(task)){
                return routerDao.add(TaskDao.namespace, task);
            }else{
                log.warn(" Duplicate task: {}",task.getBody());
            }
        }else{
            return routerDao.add(TaskDao.namespace, task);
        }
        return 0;
    }
	public boolean isRedisSwitchON(){
		return Constants.STRING_FLG_TRUE.equals(this.uccPropertyConfiguration.getRedisSwitchOn());
	}
	public Integer add(Task task) {
		return add(task, false);
	}


    public List<Task> findTasks(Integer type) {
        Assert.notNull(type, "type must not be null");
        TaskDao routerDao = taskDao;
        return routerDao.findTasks(type);
    }

    public List<Task> findTasks(Integer type, String ownSign) {
        Assert.notNull(type, "type must not be null");
        TaskDao routerDao = taskDao;
        return routerDao.findTasks(type, ownSign);
    }

    public List<Task> findLimitedTasks(Integer fetchNum,List<String> queueIds,String ownSign) {
        Assert.notNull(fetchNum, "fetchNum must not be null");
        TaskDao routerDao = taskDao;
        return routerDao.findLimitedTasks(fetchNum,queueIds,ownSign);
    }

    public List<Task> findLimitedTasks(Integer type, Integer fetchNum,List<String> queueIds) {
        Assert.notNull(type, "type must not be null");
        Assert.notNull(fetchNum, "fetchNum must not be null");
        TaskDao routerDao = taskDao;
        return routerDao.findLimitedTasks(type, fetchNum, queueIds);
    }

    public List<Task> findLimitedTasks(Integer type, Integer fetchNum, String ownSign,List<String> queueIds) {
        Assert.notNull(type, "type must not be null");
        Assert.notNull(fetchNum, "fetchNum must not be null");
        TaskDao routerDao = taskDao;
        if(isTableWithoutFetchFailed(type)) {
            return routerDao.findLimitedTasksWithoutFailed(type, fetchNum, ownSign,queueIds);
        }
        return routerDao.findLimitedTasks(type, fetchNum, ownSign,queueIds);
    }


    public String getFetchWithoutFailedTableName() {
	    try {
            return uccPropertyConfiguration.getWorkerFetchWithoutFailedTable();
        } catch (Throwable e) {
	        return null;
        }

    }


    public boolean isTableWithoutFetchFailed(Integer type) {
	    String tableName = Task.getTableName(type);
        String withoutFailedTableName = this.getFetchWithoutFailedTableName();
        if(withoutFailedTableName==null || withoutFailedTableName.length()==0){
            return false;
        }
        String[] sa = withoutFailedTableName.split(";");
        for(String s:sa){
            if(s.equals(tableName)){
                return true;
            }
        }
        return false;
    }


	public List<Task> findSpecifiedTasks(Integer type, Integer fetchNum, String ownSign ,List<String> queueIds) {
		Assert.notNull(type, "type must not be null");
		Assert.notNull(fetchNum, "fetchNum must not be null");
		TaskDao routerDao = taskDao;
		return routerDao.findSpecifiedTasks(type, fetchNum, ownSign,queueIds);
	}

    public List<Task> findTasksByFingerprint(Task task) {
        Assert.notNull(task.getFingerprint(), "fingerprint must not be null");
        TaskDao routerDao = taskDao;
        return routerDao.findTasksByFingerprint(task);
    }

    @JProfiler(jKey = "Bluedragon_dms_center.dms.method.task.update",mState = {JProEnum.TP,JProEnum.FunctionError})
    public Boolean updateBySelective(Task task) {
        TaskDao routerDao = taskDao;
        routerDao.updateBySelective(task);
        return Boolean.TRUE;
    }

    public Boolean doLock(Task task) {
        task.setStatus(Task.TASK_STATUS_PROCESSING);
        return this.updateBySelective(task);
    }

    public Boolean doRevert(Task task) {
        task.setStatus(Task.TASK_STATUS_UNHANDLED);
        task.setExecuteCount(this.getExecuteCount(task));
        task.setExecuteTime(this.getExecuteTime(task));
        return this.updateBySelective(task);
    }

    public Boolean doDone(Task task) {
        task.setStatus(Task.TASK_STATUS_FINISHED);
        task.setExecuteCount(this.getExecuteCount(task));
        task.setExecuteTime(this.getExecuteTime(task));
        return this.updateBySelective(task);
    }

    @Override
    public Integer doAddWithStatus(Task task) {
		if (StringHelper.isNotEmpty(task.getBody()) && task.getBody().length() > 2000) {
			log.error("插入任务失败，body字段超长，参数：{}", JsonHelper.toJson(task));
			return 0;
		}
        TaskDao routerDao = taskDao;
		//随机生成队列数
		Map<String, Integer> allQueueSize = tbTaskQueueService.findAllQueueSize();
		int queueSize = task.findTaskQueueSize(allQueueSize);
		task.setQueueId(new Random().nextInt(queueSize));
        return routerDao.addWithStatus(task);
    }

    public Boolean doError(Task task) {
        task.setStatus(Task.TASK_STATUS_PARSE_ERROR);
        task.setExecuteCount(this.getExecuteCount(task));
        task.setExecuteTime(this.getExecuteTime(task));
        return this.updateBySelective(task);
    }

    @Override
	public Boolean doRepeat(Task task) {
		task.setStatus(Task.TASK_STATUS_FAILED);
		task.setExecuteCount(this.getExecuteCount(task));
		task.setExecuteTime(this.getExecuteTime(task));
		return this.updateBySelective(task);
	}

	private Integer getExecuteCount(Task task) {
		return task.getExecuteCount() + Task.STEP;
	}

	private Date getExecuteTime(Task task) {
		return DateHelper.add(task.getExecuteTime(), Calendar.MINUTE, Task.INTERVAL_TIME);
	}

	/**
	 * 根据箱号查找指定类型任务是否执行完成
	 *
	 * @return true没有执行完成
	 */
	public List<Task> findTasks(Task task) {
        TaskDao routerDao = taskDao;
		return routerDao.findTasks(task);
	}

	public List<Task> findSendTasks(Integer type, Integer fetchNum, String key,List<String> queueIds,String ownSign, List<String> ownSigns) {
		Assert.notNull(type, "type must not be null");
		Assert.notNull(fetchNum, "fetchNum must not be null");
		TaskDao routerDao = taskDao;
		return routerDao.findSendTasks(type, fetchNum, key,queueIds, ownSign, ownSigns);
	}

	@Override
	public List<Task> findTasksUnderOptimizeSendTask(Integer type, Integer fetchNum, String key, List<String> queueIds, String ownSign, List<String> ownSigns,Integer executeCount) {
		Assert.notNull(type, "type must not be null");
		Assert.notNull(fetchNum, "fetchNum must not be null");
		TaskDao routerDao = taskDao;
		return routerDao.findTasksUnderOptimizeSendTask(type, fetchNum, key,queueIds, ownSign, ownSigns,executeCount);
	}

	public Task findReverseSendTask(String sendCode) {
        TaskDao routerDao = taskDao;
		return routerDao.findReverseSendTask(sendCode);
	}

	public Task findWaybillSendTask(String sendCode) {
        TaskDao routerDao = taskDao;
		return routerDao.findWaybillSendTask(sendCode);
    	}

	private Boolean isWaybillTask(Task task) {
		if (Task.TABLE_NAME_WAYBILL.equalsIgnoreCase(task.getTableName())) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	private Boolean isSendTask(Task task) {
		if (Task.TABLE_NAME_SEND.equalsIgnoreCase(task.getTableName())) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	private Boolean has(Task task) {
		if (StringHelper.isEmpty(task.getFingerprint())) {
			return Boolean.FALSE;
		}

		List<Task> tasks = this.findTasksByFingerprint(task);
		if (!tasks.isEmpty() && tasks.size() > 0) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	public Task toTask(TaskRequest request, String jsonVal) {
		Task task = new Task();
		task.setTableName(Task.getTableName(request.getType()));
		task.setSequenceName(Task.getSequenceName(task.getTableName()));
		task.setType(request.getType());
		task.setCreateSiteCode(request.getSiteCode());
		task.setKeyword1(request.getKeyword1());
		task.setKeyword2(request.getKeyword2());
		// insert keyword1 keyword2 businessType operateTime
		this.initOthers(jsonVal, task);

		task.setBody(jsonVal);
		if (StringUtils.isNotBlank(request.getBoxCode())) {
			task.setBoxCode(request.getBoxCode());
		}
		if (Task.TASK_TYPE_OFFLINE.equals(task.getType())
				&& StringUtils.isNotBlank(task.getBoxCode())
				&& task.getBoxCode().split(Constants.SEPARATOR_COMMA).length > 1) {
			task.setBoxCode(null);
		}
		task.setReceiveSiteCode(request.getReceiveSiteCode());
		String ownSign = BusinessHelper.getOwnSign();
		task.setOwnSign(ownSign);
		// insert finger print
		this.initFingerPrint(task);

		return task;
	}

	private void initOthers(String jsonVal, Task task) {
		String arrayToObject = jsonVal.substring(1, jsonVal.length() - 1);
		Map<String, Object> map = JsonHelper.json2MapNormal(arrayToObject);
		Object packageBarcode = map.get("packageBarcode");
		packageBarcode = null == packageBarcode ? map.get("packageCode") : packageBarcode;
		packageBarcode = null == packageBarcode ? map.get("packageBarOrWaybillCode")
				: packageBarcode;
		packageBarcode = null == packageBarcode ? map.get("packOrBox") : packageBarcode;

		if (null != map.get("boxCode") && map.get("boxCode") instanceof String
				&& StringUtils.isNotBlank(map.get("boxCode").toString())) {
			task.setBoxCode(map.get("boxCode").toString());
		}

		if (null != packageBarcode && StringUtils.isNotBlank(packageBarcode.toString())) {
			task.setKeyword2(packageBarcode.toString());
		}

		Object businessType = map.get("businessType");
		if (null != businessType && StringUtils.isNotBlank(businessType.toString())) {
			task.setBusinessType(Integer.valueOf(businessType.toString()));
		}

		Object operateTime = map.get("operateTime");
		if(null != operateTime){
			task.setOperateTime(StringUtils.isNotBlank(operateTime.toString()) ? DateHelper
					.getSeverTime(operateTime.toString()) : new Date());
		}

		if (Task.TASK_TYPE_INSPECTION.equals(task.getType())
				&& Constants.BUSSINESS_TYPE_REVERSE == task.getBusinessType()
				&& null != map.get("operateType") && map.get("operateType") instanceof Integer) {
			try {
				task.setOperateType(Integer.valueOf(map.get("operateType").toString()));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
	}

	public void initFingerPrint(Task task) {
		StringBuilder fingerprint = new StringBuilder("");
		if (!(Task.TASK_TYPE_SORTING.equals(task.getType())
				|| Task.TASK_TYPE_REVERSE_SPWARE.equals(task.getType())
				|| Task.TASK_TYPE_OFFLINE.equals(task.getType())
				|| Task.TASK_TYPE_INSPECTION.equals(task.getType()) || Task.TASK_TYPE_RECEIVE
				.equals(task.getType()) ||Task.TASK_TYPE_SORTING_EXCEPTION.equals(task.getType()))) {
			return;
		}
		if (task.getCreateSiteCode() < 0 || task.getBusinessType() < 0
				|| null == task.getOperateTime()) {
			return;
		}

		if (Task.TASK_TYPE_SORTING.equals(task.getType())
				|| Task.TASK_TYPE_REVERSE_SPWARE.equals(task.getType())
				|| Task.TASK_TYPE_OFFLINE.equals(task.getType())
				|| Task.TASK_TYPE_INSPECTION.equals(task.getType())
                || Task.TASK_TYPE_SORTING_EXCEPTION.equals(task.getType())) {

			fingerprint.append(task.getCreateSiteCode()).append("_")
			.append(task.getReceiveSiteCode()).append("_").append(task.getBusinessType())
			.append("_").append(task.getBoxCode()).append("_").append(task.getKeyword2())
			.append("_").append(DateHelper.formatDateTimeMs(task.getOperateTime()));
		}
		if (Task.TASK_TYPE_INSPECTION.equals(task.getType()) && null != task.getOperateType()) {
			fingerprint.append("_").append(task.getOperateType());
		}
		if (Task.TASK_TYPE_RECEIVE.equals(task.getType())
				|| Task.TASK_TYPE_PARTNER_WAY_BILL.equals(task.getType())) {
			fingerprint.append(task.getCreateSiteCode()).append("_").append(task.getKeyword2())
			.append("_").append(task.getBusinessType()).append("_")
			.append(DateHelper.formatDateTimeMs(task.getOperateTime()));
		}
		if (StringUtils.isNotBlank(fingerprint.toString())) {
			task.setFingerprint(Md5Helper.encode(fingerprint.toString()));
		}
	}

	public Integer findTasksNumsByType(Integer type, String ownSign) {
		TaskDao routerDao = taskDao;
		return routerDao.findTasksNumsByType(type, ownSign);
	}

	public Integer findFailTasksNumsByType(Integer type, String ownSign,Integer keyword1) {
		TaskDao routerDao = taskDao;
		return routerDao.findFailTasksNumsByType(type, ownSign,keyword1);
	}

	public Integer findTasksNumsIgnoreType(Integer type, String ownSign) {
		TaskDao routerDao = taskDao;
		return routerDao.findTasksNumsIgnoreType(type, ownSign);
	}

	public Integer findFailTasksNumsIgnoreType(Integer type, String ownSign) {
		TaskDao routerDao = taskDao;
		return routerDao.findFailTasksNumsIgnoreType(type, ownSign);
	}

	public void addInspectSortingTask(TaskRequest request) {
		UploadedPackage uPackage = JsonHelper.fromJson(request.getBody(),UploadedPackage.class);
		// 交接任务
		Task task = new Task();
		task.setCreateSiteCode(uPackage.getSortCenterNo());
		task.setKeyword1(String.valueOf(uPackage.getSortCenterNo()));
		task.setKeyword2(uPackage.getBarcode());

		task.setType(Task.TASK_TYPE_AUTO_INSPECTION_PREPARE);
		String fringerprint = uPackage.getSortCenterNo() + "_" + uPackage.getBarcode()
				+ "_" + uPackage.getTimeStamp() + "_" + task.getType();
		task.setFingerprint(Md5Helper.encode(fringerprint));
		task.setTableName(Task.getTableName(task.getType()));
		task.setSequenceName(Task.getSequenceName(task.getTableName()));

		task.setBody(request.getBody());
		task.setCreateTime(new Date());
		task.setExecuteCount(0);
		task.setOwnSign(BusinessHelper.getOwnSign());
		task.setStatus(Task.TASK_STATUS_UNHANDLED);

		add(task);

		// 分拣任务
		task.setType(Task.TASK_TYPE_AUTO_SORTING_PREPARE);
		// 分拣比交接晚一秒，修复全程跟踪分拣和交接顺序问题
		try{
			uPackage.setTimeStamp(addOneSecond(uPackage.getTimeStamp()));
			task.setBody(JsonHelper.toJson(uPackage));
		}catch(Throwable e){
			log.warn("分拣任务全程跟踪乱序加一秒钟失败，原因:" , e);
		}
		fringerprint = uPackage.getSortCenterNo() + "_" + uPackage.getBarcode()
				+ "_" + uPackage.getTimeStamp() + "_" + task.getType();
		task.setFingerprint(Md5Helper.encode(fringerprint));
		this.log.info("总部接口把接收到的交接数据存入 分拣 Task");

		add(task);
	}


    @Override
    public void addInspectSortingTaskDirectly(AutoSortingPackageDto packageDtos) throws Exception{
		String waybillCode = WaybillUtil.getWaybillCode(packageDtos.getWaybillCode());
		if(StringUtils.isBlank(waybillCode)){
			log.error("{}非包裹号或运单号", packageDtos.getWaybillCode());
			return;
		}
		//设置jsf超时时间
		RpcContext.getContext().setAttachment(com.jd.jsf.gd.util.Constants.HIDDEN_KEY_REQUEST_TIMEOUT, autoTaskUploadRpcTimeOut);
		//检查订单是否已妥投
		if(waybillTraceManager.isWaybillFinished(waybillCode)){
			log.error("运单{}已妥投，不能再继续分拣", waybillCode);
			return;
		}
		add(toSortingTask(packageDtos));

		//不需要验货直接返回
		if(packageDtos.isAddInspection() != null && !packageDtos.isAddInspection()){
			return;
		}

		//modified by zhanglei 20171127  这里做一下处理，如果是智配中心上传的数据，不再添加验货任务
		BaseStaffSiteOrgDto distribution = baseService.queryDmsBaseSiteByCode(String.valueOf(packageDtos.getDistributeID()));
		if(distribution != null && distribution.getSiteType().intValue() != 4){
			add(toInspectionTask(packageDtos));
		}
    }

    private Task toSortingTask(AutoSortingPackageDto dto){
        BaseStaffSiteOrgDto site = baseService.queryDmsBaseSiteByCode(dto.getSiteCode());
        Assert.notNull(site,"智能分拣线生成分拣任务出错，获取站点信息失败"); //这里主动抛出异常是为了让事务回滚
        BaseStaffSiteOrgDto distribution = baseService.queryDmsBaseSiteByCode(String.valueOf(dto.getDistributeID()));
        Assert.notNull(distribution,"智能分拣线生成分拣任务出错，获取分拣/智配中心信息失败");
        Task taskSorting=new Task();
        taskSorting.setOwnSign(BusinessHelper.getOwnSign());
        taskSorting.setKeyword1(String.valueOf(dto.getDistributeID()));
        taskSorting.setKeyword2(dto.getWaybillCode());
        taskSorting.setCreateSiteCode(dto.getDistributeID());
        taskSorting.setReceiveSiteCode(Integer.valueOf(dto.getSiteCode()));
        taskSorting.setCreateTime(new Date());
        //根据distributeId来判断是否是智配中心类型设置为1250  分拣中心类型是1200
        if(distribution.getSiteType() == 4){
        	taskSorting.setType(Task.TASK_TYPE_ZHIPEI_SORTING);
        }else{
        	taskSorting.setType(Task.TASK_TYPE_SORTING);
        }
        taskSorting.setBoxCode(dto.getBoxCode());
        taskSorting.setTableName(Task.getTableName(taskSorting.getType()));
        taskSorting.setSequenceName(Task.getSequenceName(taskSorting.getTableName()));
        StringBuilder fingerprint = new StringBuilder("");
        fingerprint.append(taskSorting.getCreateSiteCode()).append("_")
                .append(taskSorting.getReceiveSiteCode()).append("_").append(taskSorting.getBusinessType())
                .append("_").append(taskSorting.getBoxCode()).append("_").append(taskSorting.getKeyword2())
                .append("_").append(DateHelper.formatDateTimeMs(taskSorting.getOperateTime()));
        taskSorting.setFingerprint(Md5Helper.encode(fingerprint.toString()));
        List<SortingRequest> list=new ArrayList<SortingRequest>(1);

        SortingRequest request=new SortingRequest();
        request.setBoxCode(dto.getBoxCode());
        request.setFeatureType(0);
        request.setIsCancel(0);
        request.setIsLoss(0);
        request.setPackageCode(dto.getWaybillCode());
        request.setReceiveSiteCode(site.getSiteCode());
        request.setReceiveSiteName(site.getSiteName());
        request.setWaybillCode(WaybillUtil.getWaybillCode(dto.getWaybillCode()));
        request.setBusinessType(Constants.BUSSINESS_TYPE_POSITIVE);
        request.setOperateTime(addOneSecond(dto.getCreateTime()));
        request.setSiteCode(dto.getDistributeID());
        request.setSiteName(dto.getDistributeName());
        request.setUserCode(dto.getOperatorID());
        request.setUserName(dto.getOperatorName());
        request.setBizSource(AUTOMATIC_SORTING_MACHINE_SORTING.getCode());
        request.setOperatorTypeCode(OperatorTypeEnum.AUTO_MACHINE.getCode());
        request.setOperatorId(dto.getMachineCode());
        list.add(request);
        taskSorting.setBody(JsonHelper.toJson(list));
        return taskSorting;
    }

    private Task toInspectionTask(AutoSortingPackageDto dto){
        Task taskInsp = new Task();
        taskInsp.setCreateSiteCode(dto.getDistributeID());
        taskInsp.setKeyword1(String.valueOf(dto.getDistributeID()));
        taskInsp.setKeyword2(dto.getWaybillCode());
        taskInsp.setType(Task.TASK_TYPE_INSPECTION);
        taskInsp.setTableName(Task.getTableName(taskInsp.getType()));
        taskInsp.setSequenceName(Task.getSequenceName(taskInsp.getTableName()));
        taskInsp.setBody(JsonHelper.toJson(toInspectionAS(dto)));
        taskInsp.setCreateTime(new Date());
        taskInsp.setExecuteCount(0);
        taskInsp.setOwnSign(BusinessHelper.getOwnSign());
        taskInsp.setStatus(Task.TASK_STATUS_UNHANDLED);
        StringBuilder fingerprint = new StringBuilder("");
        fingerprint.append(taskInsp.getCreateSiteCode()).append("_")
                .append(taskInsp.getReceiveSiteCode()).append("_")
                .append(taskInsp.getBoxCode()).append("_").append(dto.getWaybillCode())
                .append("_").append(dto.getCreateTime());
        taskInsp.setFingerprint(Md5Helper.encode(fingerprint.toString()));
        return taskInsp;
    }


    public List<InspectionAS> toInspectionAS(AutoSortingPackageDto uPackage){
        List<InspectionAS> inspectionASes = new ArrayList<InspectionAS>();
        InspectionAS inspectionAS = new InspectionAS();
        inspectionAS.setBoxCode("");
        inspectionAS.setExceptionType("");
        inspectionAS.setId(0);
        inspectionAS.setOperateTime(uPackage.getCreateTime());
        inspectionAS.setOperateType(0);
        inspectionAS.setPackageBarOrWaybillCode(uPackage.getWaybillCode());
        inspectionAS.setReceiveSiteCode(0);
        inspectionAS.setSiteCode(uPackage.getDistributeID());
        inspectionAS.setSiteName(uPackage.getDistributeName());
        inspectionAS.setUserCode(uPackage.getOperatorID());
        inspectionAS.setUserName(uPackage.getOperatorName());
        inspectionAS.setBusinessType(50);
		inspectionAS.setMachineCode(uPackage.getMachineCode());
        inspectionAS.setBizSource(InspectionBizSourceEnum.AUTOMATIC_SORTING_MACHINE_INSPECTION.getCode());
        inspectionAS.setOperatorTypeCode(OperatorTypeEnum.AUTO_MACHINE.getCode());
        inspectionAS.setOperatorId(uPackage.getMachineCode());
        inspectionASes.add(inspectionAS);
        return inspectionASes;
    }


    /**
     *  时间加一秒
     *  @Param oldDateString 原来的日期字符串，格式"yyyy-MM-dd HH:mm:ss"
     *  @Return 返回新日期字符串
     * */

        private static String addOneSecond(String oldDateString){
            Date sortTime = DateHelper.parseDate(oldDateString, Constants.DATE_TIME_FORMAT);
            return DateHelper.formatDate(DateHelper.add(sortTime, Calendar.SECOND, 1),Constants.DATE_TIME_FORMAT);
        }

	public TaskDao getTaskDao() {
		return taskDao;
	}

	public void setTaskDao(TaskDao taskDao) {
		this.taskDao = taskDao;
	}

	public RedisTaskService getRedisTaskService() {
		return redisTaskService;
	}

	public void setRedisTaskService(RedisTaskService redisTaskService) {
		this.redisTaskService = redisTaskService;
	}

	public TaskModeAgent getTaskModeAgent() {
		return taskModeAgent;
	}

	public void setTaskModeAgent(TaskModeAgent taskModeAgent) {
		this.taskModeAgent = taskModeAgent;
	}

	public SysConfigService getSysConfigService() {
		return sysConfigService;
	}

	public void setSysConfigService(SysConfigService sysConfigService) {
		this.sysConfigService = sysConfigService;
	}

	public BaseService getBaseService() {
		return baseService;
	}

	public void setBaseService(BaseService baseService) {
		this.baseService = baseService;
	}


    public JmqTopicRouter getTaskJmqTopicRouter() {
        return taskJmqTopicRouter;
    }

    public void setTaskJmqTopicRouter(JmqTopicRouter taskJmqTopicRouter) {
        this.taskJmqTopicRouter = taskJmqTopicRouter;
    }

    public DmsDynamicProducer getDynamicProducer() {
        return dynamicProducer;
    }

    public void setDynamicProducer(DmsDynamicProducer dynamicProducer) {
        this.dynamicProducer = dynamicProducer;
    }

    /**
     * xumei
     */
    @Override
	public List<Task> findTaskTypeByStatus(Integer type, int fetchNum ,List<String> queueIds) {
		return taskDao.findTaskTypeByStatus(type, fetchNum,queueIds);
	}
    public Integer updateTaskStatus(Task task) {
       return taskDao.updateTaskStatus(task);
    }

	public List<Task> findDeliveryToFinanceConvertTasks(Integer type,Integer fetchNum,List<String> queueIds){
		Assert.notNull(type, "type must not be null");
		Assert.notNull(fetchNum, "fetchNum must not be null");
		TaskDao routerDao = taskDao;
		return routerDao.findDeliveryToFinanceConvertTasks(type, fetchNum,queueIds);
	}

	/**
	 * 虚拟组板自动关闭任务
	 * @param type
	 * @param fetchNum
	 * @param ownSign
	 * @param queueIds
	 * @return
	 */
	@Override
	public List<Task> findVirtualBoardTasks(Integer type, Integer fetchNum, String ownSign, List<String> queueIds, Integer lazyExecuteDays) {
		Assert.notNull(type, "type must not be null");
		Assert.notNull(fetchNum, "fetchNum must not be null");
		Assert.notNull(ownSign, "ownSign must not be null");
		Assert.notNull(lazyExecuteDays, "lazyExecuteDays must not be null");
		TaskDao routerDao = taskDao;
		return routerDao.findVirtualBoardTasks(type, fetchNum, ownSign, queueIds, lazyExecuteDays);
	}
}
