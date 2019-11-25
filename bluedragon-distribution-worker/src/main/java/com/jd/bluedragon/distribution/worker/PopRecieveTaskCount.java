package com.jd.bluedragon.distribution.worker;

import com.jd.bluedragon.distribution.popReveice.domain.TaskPopRecieveCount;
import com.jd.bluedragon.distribution.popReveice.service.TaskPopRecieveCountService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 
* 类描述： POP收货数据回传POP平台work
* 创建者： libin
* 项目名称： bluedragon-distribution-worker
* 创建时间： 2013-3-1 下午1:19:18
 */
public class PopRecieveTaskCount extends AbstractScheduler<TaskPopRecieveCount> {
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private TaskPopRecieveCountService taskPopRecieveCountService;
	@Autowired
	protected TaskService taskService;

	@Override
	public boolean execute(Object[] taskArray, String ownSign) throws Exception {

		List<TaskPopRecieveCount> tasks = new ArrayList<TaskPopRecieveCount>();
		for (Object task : taskArray) {
			if (task != null && task instanceof TaskPopRecieveCount) {
				tasks.add((TaskPopRecieveCount) task);
			}
		}
		this.log.info("tasks size is {}" , tasks.size());
		List<TaskPopRecieveCount> messageList = new ArrayList<TaskPopRecieveCount>();
		for (TaskPopRecieveCount task : tasks) {
			Task oldTask = new Task();
			oldTask.setType(task.getTaskType());
			oldTask.setId(task.getTaskId());
			oldTask.setExecuteCount(task.getExecuteCount());
			oldTask.setOwnSign(task.getOwnSign());
			oldTask.setYn(task.getYn());
			oldTask.setExecuteTime(task.getExecuteTime());
			try {
				this.log.info("task id is {}" , task.getTaskId());
				this.taskService.doLock(oldTask);
				messageList.add(task);
				oldTask.setExecuteCount(task.getExecuteCount());
				oldTask.setExecuteTime(task.getExecuteTime());
				this.taskService.doDone(oldTask);
				if (messageList.size() > 9) {
					sendMessage(messageList);
				}
			} catch (Exception e) {
				oldTask.setExecuteCount(task.getExecuteCount());
				oldTask.setExecuteTime(task.getExecuteTime());
				this.log.error("处理分拣任务发生异常，task id is {}" , task.getTaskId(), e);
				this.taskService.doError(oldTask);
			}
		}

		if (messageList.size() > 0) {
			sendMessage(messageList);
		}
		return Boolean.TRUE;

	}

	/**
	 * 
	* 方法描述 : 调用发送消息方法，发送到MQ
	* 创建者：libin 
	* 类名： PopRecieveTaskCount.java
	* 创建时间： 2013-3-1 下午1:20:03
	* @param messageList void
	 */
	public void sendMessage(List<TaskPopRecieveCount> messageList) {
		try {
			this.taskPopRecieveCountService.sendMessage(messageList);
			messageList.clear();
		} catch (Exception e) {
			log.error("向POP发送消息失败。",e);
		}
	}

	@Override
	public Comparator<TaskPopRecieveCount> getComparator() {
		return new Comparator<TaskPopRecieveCount>() {
			public int compare(TaskPopRecieveCount o1, TaskPopRecieveCount o2) {
				if (null != o1 && null != o2 && o1.getTaskId() != null && o1.getTaskId().equals(o2.getTaskId())) {
					return 0;
				} else {
					return 1;
				}
			}

		};

	}

	@Override
	public List<TaskPopRecieveCount> selectTasks(String ownSign, int queueNum, List<String> queryCondition, int fetchNum)
			throws Exception {
		List<TaskPopRecieveCount> taskLists = new ArrayList<TaskPopRecieveCount>();
		try {
			List<TaskPopRecieveCount> unhandles = taskPopRecieveCountService.findLimitedTasks(type, fetchNum, ownSign,queryCondition);
			for (TaskPopRecieveCount taskPopRecieveCount : unhandles) {
				if (this.isMyTask(queueNum, taskPopRecieveCount.taskId, queryCondition)) {
					taskLists.add(taskPopRecieveCount);
				}
			}
		} catch (Exception e) {
			this.log.error("查询未处理的分拣退货信息出现异常， 异常信息为：{}" , e.getMessage(), e);
		}
		return taskLists;

	}

}
