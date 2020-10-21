package com.jd.bluedragon.distribution.task.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.WaybillTraceManager;
import com.jd.bluedragon.core.redis.TaskModeAgent;
import com.jd.bluedragon.distribution.api.request.AutoSortingPackageDto;
import com.jd.bluedragon.distribution.api.request.SortingRequest;
import com.jd.bluedragon.distribution.api.request.TaskRequest;
import com.jd.bluedragon.distribution.auto.domain.UploadedPackage;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.inspection.domain.InspectionAS;
import com.jd.bluedragon.distribution.task.asynBuffer.DmsDynamicProducer;
import com.jd.bluedragon.distribution.task.dao.TaskDao;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.worker.service.TBTaskQueueService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.Md5Helper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.framework.asynBuffer.producer.jmq.JmqTopicRouter;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
        Assert.notNull(task, "task must not be null");
        if (isDynamicProducerOn(task)) {
            dynamicProducer.send(task);
            return 1;
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
		//检查订单是否已妥投
		if(waybillTraceManager.isWaybillFinished(waybillCode)){
			log.error("运单{}已妥投，不能再继续分拣", waybillCode);
			return;
		}
		add(toSortingTask(packageDtos));

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
}
