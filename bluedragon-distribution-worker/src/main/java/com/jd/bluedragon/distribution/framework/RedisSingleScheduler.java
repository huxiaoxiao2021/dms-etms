package com.jd.bluedragon.distribution.framework;

import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.log.BusinessLogProfilerBuilder;
import com.jd.bluedragon.utils.log.BusinessLogConstans;
import com.jd.dms.logger.external.LogEngine;
import com.jd.bluedragon.distribution.systemLog.domain.SystemLog;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.bluedragon.utils.SystemLogUtil;
import com.jd.dms.logger.external.BusinessLogProfiler;
import com.jd.fastjson.JSONObject;
import com.jd.tbschedule.dto.ScheduleQueue;
import com.jd.tbschedule.redis.template.TBRedisWorkerMultiTemplate;
import com.jd.tbschedule.redis.template.TaskEntry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public abstract class RedisSingleScheduler extends
		TBRedisWorkerMultiTemplate<Task> {

	protected final Logger log = LoggerFactory.getLogger(this.getClass());

	protected String desc;

	@Autowired
	private TaskHanlder redisTaskHanlder;
	
	private boolean initFinished = false;
	/**
	 * 标识是否处于激活状态，任务初始化成功并且spring容器初始化成功，才能正常运行
	 */
	private boolean isActive = false;
	
	@Autowired
	private BaseService baseService;

	@Autowired
	private LogEngine logEngine;

	//redis开关
	public static final String HANDLE_SWITCH = "HANDLE_SWITCH";
	
	@Override
	public void init() throws Exception {
		if(!initFinished){//如果还未初始化成功，则重新进行初始化，保证只成功初始化一次
			try{
				super.init();
				initFinished=true;
			}catch(Exception e){
				log.error("RedisSingleScheduler.init error", e);
			}
		}
	}
	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		//监听初始化、刷新事件，isActive标识为true
		if (event instanceof ContextRefreshedEvent) {
			log.warn("Spring初始化完成，任务[{}-{}]开始执行！", this.taskType, this.ownSign);
			isActive = true;
		}
		//监听关闭事件，停止异步缓冲任务
		if (event instanceof ContextClosedEvent) {
			log.warn("Spring关闭，任务[{}-{}]停止执行！", this.taskType, this.ownSign);
			isActive = false;
		}
	}	
	@Override
	public List<TaskEntry<Task>> selectTasks(String ownSign, int taskQueueNum,
			List<String> taskQueueList, int eachFetchDataNum) throws Exception {
		//初始化，内部逻辑保证只初始化一次
		init();
		if(initFinished && isActive){//初始化成功, 查询redis 
			return super.selectTasks(ownSign, taskQueueNum, taskQueueList, eachFetchDataNum);
		}else{
			log.warn("任务[{}-{}]未准备就绪，本次不抓取任务数据！", this.taskType, this.ownSign);
			return new ArrayList<TaskEntry<Task>>();
		}
	}
	
	
	/**
	 * 分拣中心由于使用PRE以及DMS而不是使用PRE以及BASE而需要做特殊化处理
	 */
	protected List<ScheduleQueue> getScheduleQueueList() {
		List<ScheduleQueue> rawQueueList = super.getScheduleQueueList();
		List<ScheduleQueue> validQueueList = new ArrayList<ScheduleQueue>();
		if (rawQueueList != null) {
			for (ScheduleQueue queue : rawQueueList) {
				if (StringHelper.isNotEmpty(queue.getBaseTaskType())) {
					validQueueList.add(queue);
				}
			}
		}
		return validQueueList;
	}

	public boolean handleTask(List<TaskEntry<Task>> tasks, String ownSign)
			throws Exception {
		log.info("[{}]worker 抓取到[{}]条任务待处理",desc,tasks.size());
		int dealDataFail = 0;
		for (TaskEntry<Task> task : tasks) {
			boolean result = handleSingleTask(task.getTask(), ownSign);
			if (!result) {
				dealDataFail++;
			}
			super.removeRedis(task, super.queueNumberMap);
		}

		log.info("[{}]worker 执行[{}]条任务完毕！",desc,tasks.size());
		if (dealDataFail > 0) {
			log.warn("[{}]worker 抓取{}条任务，{}条数据执行失败]！",desc,tasks.size(),dealDataFail);
			return false;
		} else {
			return true;
		}
	}

	private boolean handleSingleTask(Task task, String ownSign) throws Exception {
		long startTime=new Date().getTime();
		if (task == null) {
			return false;
		}
		boolean preResult = false;
		boolean result = false;
		try {
			//1.预处理
			preResult = redisTaskHanlder.preHandle(task);
			if (preResult) {
				//2. 执行单个任务，如果有错，则说明处理失败。
				try {
					result = executeSingleTask(task, ownSign);
				} catch (Exception e) {
					log.error("Redis任务执行失败:{}", JsonHelper.toJson(task), e);
				}

				//3. 根据任务处理结果进行数据落地，标明任务的执行状态(成功、失败)
				if (result) {
					List<SysConfig> configs = baseService.queryConfigByKeyWithCache(HANDLE_SWITCH);
					for (SysConfig sys : configs) {
						if (StringHelper.matchSiteRule(sys.getConfigContent(), "ON")) {
							redisTaskHanlder.handleSuccess(task);
						}
					}
				} else {
					redisTaskHanlder.handleError(task);
				}
			}
		} catch (Exception e) {
			
			/**
			 * 这里有两种情况
			 * 1. result = true  说明处理成功，但redisTaskHanlder.handleSuccess(task);抛出异常。那么由于任务已经执行完成，所以此任务数据可以抛弃。
			 * 2. result = false 说明处理失败，且redisTaskHanlder.handleError(task);数据落地失败。那么说明落地时异常，对于此部分数据进行日志保存。
			 */
			if(result){
				log.error("Redis任务数据处理成功, 落地失败, 数据可以丢弃!异常:{}",e.getMessage(), e);
			}else{
				log.error("Redis任务数据处理失败, 落地失败，数据存入System_Log表!异常:{}",e.getMessage(), e);
				//说明处理失败后落地又失败，这种情况需要记录日志，择日再执行了
				SystemLog systemLog = new SystemLog();
				systemLog.setKeyword1(task.getKeyword1());
				systemLog.setKeyword2(task.getKeyword2());
				systemLog.setKeyword3(task.getBoxCode());
				systemLog.setKeyword4(Task.TASK_STATUS_PARSE_ERROR.longValue());//表示任务执行失败
				systemLog.setType(task.getType().longValue());
				systemLog.setContent(task.getBody());

				long endTime = new Date().getTime();

				JSONObject request=new JSONObject();
				request.put("boxCode",task.getBoxCode());

				JSONObject response=new JSONObject();
				response.put("log", systemLog);

				BusinessLogProfiler businessLogProfiler=new BusinessLogProfilerBuilder()
						.operateTypeEnum(BusinessLogConstans.OperateTypeEnum.TASK_REDIS_TASK)
						.operateRequest(request)
						.operateResponse(response)
						.methodName("RedisSingleScheduler#handleSingleTask")
						.processTime(endTime,startTime)
						.build();

				logEngine.addLog(businessLogProfiler);
				
				int count = SystemLogUtil.log(systemLog);
				if(count<1){//说明此时数据库有异常，存不进去。向上抛出异常，防止redis 删除数据。
					log.error("Redis任务数据存入System_Log表失败, 数据库可能存在异常!");
					throw(e);
				}
			}
		}
		return result;
	}

	
	public Comparator<TaskEntry<Task>> getComparator() {
		return redisTaskHanlder.getComparator();
	}

	protected abstract boolean executeSingleTask(Task task, String ownSign) throws Exception;

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}


}
