package com.jd.bluedragon.distribution.task.service;

import java.util.*;

import com.jd.bluedragon.distribution.api.request.AutoSortingPackageDto;
import com.jd.bluedragon.distribution.api.request.SortingRequest;
import com.jd.bluedragon.distribution.auto.domain.UploadedPackage;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.inspection.domain.InspectionAS;
import com.jd.etms.basic.dto.BaseStaffSiteOrgDto;
import com.jd.etms.utils.cache.annotation.Cache;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.perf4j.aop.Profiled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.redis.TaskModeAgent;
import com.jd.bluedragon.distribution.api.request.TaskRequest;
import com.jd.bluedragon.distribution.task.dao.TaskDao;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.Md5Helper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;

public class TaskServiceImpl implements TaskService {

	private final Log logger = LogFactory.getLog(this.getClass());

	private static final String REDIS_SWITCH = "redis.switch";
	private static final String REDIS_SWITCH_ON = "1";

    private TaskDao taskDao;
    
    private TaskDao mysqlTaskDao;
    
    private Set mysqlTableSet;
    
    private RedisTaskService redisTaskService;
    
    private TaskModeAgent taskModeAgent;

	private SysConfigService sysConfigService;

    private BaseService baseService;

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void addBatch(List<Task> tasks) {
        for(Task task : tasks){
            add(task,false);
        }
    }

    /**
     * @param task
     * @param ifCheckTaskMode 
     * @return
     */
    @Profiled(tag = "TaskService.add")
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Integer add(Task task, boolean ifCheckTaskMode) {
        Assert.notNull(task, "task must not be null");

		TaskDao routerDao = taskDao;    	
    	if(mysqlTableSet.contains(task.getTableName())){
    		routerDao = mysqlTaskDao;
    	}
    	
    	
        if( Task.TASK_TYPE_PDA.equals(task.getType()) ){
        	logger.info(" pda logs , box_code: "+task.getBoxCode()+" [body]: "+task.getBody());
        	return 0;
        }
		logger.error("redis switch : " + isRedisSwitchON());
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
					logger.error("保存任务失败：" + task.toString());
				}
				if (isRedisSucc) {
					return 1;
				}
			}
		}

        if ( this.isWaybillTask(task) || this.isSendTask(task) || Task.TASK_TYPE_SORTING.equals(task.getType())
        		|| Task.TASK_TYPE_RECEIVE.equals(task.getType()) || Task.TASK_TYPE_INSPECTION.equals(task.getType())
        		|| Task.TASK_TYPE_REVERSE_SPWARE.equals(task.getType()) || Task.TASK_TYPE_OFFLINE.equals(task.getType())
                || Task.TASK_TYPE_PUSH_MQ.equals(task.getType()) || Task.TASK_TYPE_AUTO_INSPECTION_PREPARE.equals(task.getType())
				|| Task.TASK_TYPE_AUTO_SORTING_PREPARE.equals(task.getType()) || Task.TASK_TYPE_SORTING_EXCEPTION.equals(task.getType())
                || Task.TASK_TYPE_GLOBAL_TRADE.equals(task.getType())) {     // 增加干线计费信息MQ去重
        	if(!this.has(task)){
        		return routerDao.add(TaskDao.namespace, task);
        	}else{
	        	logger.error(" Duplicate task: "+task.getBody());
	        }
        }else{
            return routerDao.add(TaskDao.namespace, task);
        }
        return 0;
    }

	@JProfiler(jKey = "Bluedragon_dms_center.dms.method.task.redisSwitch", mState = {
			JProEnum.TP, JProEnum.FunctionError })
	public Boolean isRedisSwitchON(){
		//加入监控，开始
		CallerInfo info = Profiler.registerInfo("Bluedragon_dms_center.dms.method.task.redisSwitchOn", false, true);
		SysConfig redisSwitch = getSwitchForRedis(REDIS_SWITCH);
		//加入监控结束
		Profiler.registerInfoEnd(info);
		if(null == redisSwitch || StringHelper.isEmpty(redisSwitch.getConfigContent())
				|| redisSwitch.getConfigContent().trim().equals(REDIS_SWITCH_ON)){
			return true;
		}
		return false;

	}

	@Cache(key = "TaskServiceImpl.getSwitchForRedis@args0", memoryEnable = true, memoryExpiredTime = 5 * 1000)
	public SysConfig getSwitchForRedis(String conName){
		try {
			SysConfig sysConfig = new SysConfig();
			sysConfig.setConfigName(conName);
			List<SysConfig> sysConfigs = sysConfigService.getList(sysConfig);
			if (null == sysConfigs || sysConfigs.size() <= 0) {
				return null;
			} else {
				return sysConfigs.get(0);
			}
		} catch (Throwable ex) {
			return null;
		}
	}

	@Profiled(tag = "TaskService.add")
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Integer add(Task task) {
		return add(task, false);
	}


	@Profiled(tag = "TaskService.findTasks")
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Task> findTasks(Integer type) {
        Assert.notNull(type, "type must not be null");
        TaskDao routerDao = taskDao;    	
    	if(mysqlTableSet.contains(Task.getTableName(type))){
    		routerDao = mysqlTaskDao;
    	}
    	
        return routerDao.findTasks(type);
    }

    @Profiled(tag = "TaskService.findTasks.ownSign")
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Task> findTasks(Integer type, String ownSign) {
        Assert.notNull(type, "type must not be null");
        TaskDao routerDao = taskDao;    	
    	if(mysqlTableSet.contains(Task.getTableName(type))){
    		routerDao = mysqlTaskDao;
    	}
    	
        return routerDao.findTasks(type, ownSign);
    }

    @Profiled(tag = "TaskService.findLimitedTasks.waybill")
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Task> findLimitedTasks(Integer fetchNum) {
        Assert.notNull(fetchNum, "fetchNum must not be null");
        TaskDao routerDao = taskDao;    	
    	if(mysqlTableSet.contains(Task.getTaskWaybillTableName())){
    		routerDao = mysqlTaskDao;
    	}
       
        return routerDao.findLimitedTasks(fetchNum);
    }

    @Profiled(tag = "TaskService.findLimitedTasks")
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Task> findLimitedTasks(Integer type, Integer fetchNum) {
        Assert.notNull(type, "type must not be null");
        Assert.notNull(fetchNum, "fetchNum must not be null");
        TaskDao routerDao = taskDao;    	
    	if(mysqlTableSet.contains(Task.getTableName(type))){
    		routerDao = mysqlTaskDao;
    	}
       
        return routerDao.findLimitedTasks(type, fetchNum);
    }

    @Profiled(tag = "TaskService.findLimitedTasks.ownSign")
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Task> findLimitedTasks(Integer type, Integer fetchNum, String ownSign) {
        Assert.notNull(type, "type must not be null");
        Assert.notNull(fetchNum, "fetchNum must not be null");
        TaskDao routerDao = taskDao;    	
    	if(mysqlTableSet.contains(Task.getTableName(type))){
    		routerDao = mysqlTaskDao;
    	}
       
        return routerDao.findLimitedTasks(type, fetchNum, ownSign);
    }

	@Profiled(tag = "TaskService.findSpecifiedTasks.ownSign")
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public List<Task> findSpecifiedTasks(Integer type, Integer fetchNum, String ownSign) {
		Assert.notNull(type, "type must not be null");
		Assert.notNull(fetchNum, "fetchNum must not be null");
		TaskDao routerDao = taskDao;    	
    	if(mysqlTableSet.contains(Task.getTableName(type))){
    		routerDao = mysqlTaskDao;
    	}
    	
		return routerDao.findSpecifiedTasks(type, fetchNum, ownSign);
	}

    @Profiled(tag = "TaskService.findTasksByFingerprint")
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Task> findTasksByFingerprint(Task task) {
        Assert.notNull(task.getFingerprint(), "fingerprint must not be null");
        TaskDao routerDao = taskDao;    	
    	if(mysqlTableSet.contains(task.getTableName())){
    		routerDao = mysqlTaskDao;
    	}
    	
        return routerDao.findTasksByFingerprint(task);
    }

    @JProfiler(jKey = "Bluedragon_dms_center.dms.method.task.update",mState = {JProEnum.TP,JProEnum.FunctionError})
    @Profiled(tag = "TaskService.updateBySelective")
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Boolean updateBySelective(Task task) {
        TaskDao routerDao = taskDao;    	
    	if(mysqlTableSet.contains(task.getTableName())){
    		routerDao = mysqlTaskDao;
    	}
    	
        routerDao.updateBySelective(task);
        return Boolean.TRUE;
    }

    @Profiled(tag = "TaskService.doLock")
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Boolean doLock(Task task) {
        task.setStatus(Task.TASK_STATUS_PROCESSING);
        return this.updateBySelective(task);
    }

    @Profiled(tag = "TaskService.doRevert")
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Boolean doRevert(Task task) {
        task.setStatus(Task.TASK_STATUS_UNHANDLED);
        task.setExecuteCount(this.getExecuteCount(task));
        task.setExecuteTime(this.getExecuteTime(task));
        return this.updateBySelective(task);
    }

    @Profiled(tag = "TaskService.doDone")
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Boolean doDone(Task task) {
        task.setStatus(Task.TASK_STATUS_FINISHED);
        task.setExecuteCount(this.getExecuteCount(task));
        task.setExecuteTime(this.getExecuteTime(task));
        return this.updateBySelective(task);
    }

    @Profiled(tag = "TaskService.doAddWithStatus")
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    @Override
    public Integer doAddWithStatus(Task task) {
        TaskDao routerDao = taskDao;    	
    	if(mysqlTableSet.contains(task.getTableName())){
    		routerDao = mysqlTaskDao;
    	}
    	
        return routerDao.addWithStatus(task);
    }
    
    @Profiled(tag = "TaskService.doError")
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
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
	@Profiled(tag = "TaskService.findTasks")
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public List<Task> findTasks(Task task) {
        TaskDao routerDao = taskDao;    	
    	if(mysqlTableSet.contains(task.getTableName())){
    		routerDao = mysqlTaskDao;
    	}
    	
		return routerDao.findTasks(task);
	}
	
	@Profiled(tag = "TaskService.findSendTasks")
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public List<Task> findSendTasks(Integer type, Integer fetchNum, String key) {
		Assert.notNull(type, "type must not be null");
		Assert.notNull(fetchNum, "fetchNum must not be null");
		TaskDao routerDao = taskDao;    	
    	if(mysqlTableSet.contains(Task.getTableName(type))){
    		routerDao = mysqlTaskDao;
    	}
    	
		return routerDao.findSendTasks(type, fetchNum, key);
	}

	@Profiled(tag = "TaskService.findReverseSendTask")
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public Task findReverseSendTask(String sendCode) {
        TaskDao routerDao = taskDao;    	
    	if(mysqlTableSet.contains("task_send")){
    		routerDao = mysqlTaskDao;
    	}
    	
		return routerDao.findReverseSendTask(sendCode);
	}
	
	@Profiled(tag = "TaskService.findWaybillSendTask")
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public Task findWaybillSendTask(String sendCode) {
        TaskDao routerDao = taskDao;    	
    	if(mysqlTableSet.contains("task_send")){
    		routerDao = mysqlTaskDao;
    	}
    	
		return this.taskDao.findWaybillSendTask(sendCode);
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
	
	@Profiled(tag = "TaskService.findTasksNumsByType")
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public Integer findTasksNumsByType(Integer type, String ownSign) {
		TaskDao routerDao = taskDao;    	
    	if(mysqlTableSet.contains(Task.getTableName(type))){
    		routerDao = mysqlTaskDao;
    	}
    	
		return routerDao.findTasksNumsByType(type, ownSign);
	}
	
	@Profiled(tag = "TaskService.findFailTasksNumsByType")
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public Integer findFailTasksNumsByType(Integer type, String ownSign) {
		TaskDao routerDao = taskDao;    	
    	if(mysqlTableSet.contains(Task.getTableName(type))){
    		routerDao = mysqlTaskDao;
    	}
    	
		return routerDao.findFailTasksNumsByType(type, ownSign);
	}

	@Profiled(tag = "TaskService.addInspectSortingTask")
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
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
		this.logger.info("总部接口把接收到的交接数据存入 交接 Task");

		add(task);

		// 分拣任务
		task.setType(Task.TASK_TYPE_AUTO_SORTING_PREPARE);
		// 分拣比交接晚一秒，修复全程跟踪分拣和交接顺序问题
		try{
			uPackage.setTimeStamp(addOneSecond(uPackage.getTimeStamp()));
			task.setBody(JsonHelper.toJson(uPackage));
		}catch(Throwable e){
			logger.warn("分拣任务全程跟踪乱序加一秒钟失败，原因" + e);
		}
		fringerprint = uPackage.getSortCenterNo() + "_" + uPackage.getBarcode()
				+ "_" + uPackage.getTimeStamp() + "_" + task.getType();
		task.setFingerprint(Md5Helper.encode(fringerprint));
		this.logger.info("总部接口把接收到的交接数据存入 分拣 Task");

		add(task);
	}


    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void addInspectSortingTaskDirectly(AutoSortingPackageDto packageDtos) throws Exception{
        if (add(toInspectionTask(packageDtos)) <= 0 || add(toSortingTask(packageDtos)) <= 0) {
            throw new Exception("智能分拣线生成交接、分拣任务出错，两个之中有一个可能失败");
        }
    }

    private Task toSortingTask(AutoSortingPackageDto dto){
        BaseStaffSiteOrgDto site = baseService.queryDmsBaseSiteByCode(dto.getSiteCode());
        Assert.notNull(site,"智能分拣线生成分拣任务出错，获取站点信息失败"); //这里主动抛出异常是为了让事务回滚
        Task taskSorting=new Task();
        taskSorting.setKeyword1(String.valueOf(dto.getDistributeID()));
        taskSorting.setKeyword2(dto.getWaybillCode());
        taskSorting.setCreateSiteCode(dto.getDistributeID());
        taskSorting.setReceiveSiteCode(Integer.valueOf(dto.getSiteCode()));
        taskSorting.setCreateTime(new Date());
        taskSorting.setType(Task.TASK_TYPE_SORTING);
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
        request.setWaybillCode(BusinessHelper.getWaybillCodeByPackageBarcode(dto.getWaybillCode()));
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

	public TaskDao getMysqlTaskDao() {
		return mysqlTaskDao;
	}

	public void setMysqlTaskDao(TaskDao mysqlTaskDao) {
		this.mysqlTaskDao = mysqlTaskDao;
	}

	public Set getMysqlTableSet() {
		return mysqlTableSet;
	}

	public void setMysqlTableSet(Set mysqlTableSet) {
		this.mysqlTableSet = mysqlTableSet;
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




    }
