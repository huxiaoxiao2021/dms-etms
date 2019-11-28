package com.jd.bluedragon.distribution.worker;

import com.jd.bluedragon.distribution.failqueue.domain.TaskFailQueue;
import com.jd.bluedragon.distribution.failqueue.service.IFailQueueService;
import com.taobao.pamirs.schedule.IScheduleTaskDealMulti;
import com.taobao.pamirs.schedule.TBScheduleManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.util.*;

public class TaskFailQueueTask implements IScheduleTaskDealMulti<TaskFailQueue>{

	private final static Logger log = LoggerFactory.getLogger(SendDetailToFinanceTask.class);

	@Autowired
	IFailQueueService failQueueService;
	
	protected DataSource dataSource;
	protected String taskType;
	protected String ownSign;
	
	@Autowired
 	private TBScheduleManagerFactory managerFactory;

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public String getTaskType() {
		return taskType;
	}

	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}

	public String getOwnSign() {
		return ownSign;
	}

	public void setOwnSign(String ownSign) {
		this.ownSign = ownSign;
	}
	
	public TBScheduleManagerFactory getManagerFactory() {
		return managerFactory;
	}

	public void setManagerFactory(TBScheduleManagerFactory managerFactory) {
		this.managerFactory = managerFactory;
	}
	
	@Override
	public Comparator<TaskFailQueue> getComparator() {
		// TODO Auto-generated method stub
		 return new Comparator<TaskFailQueue>() {
	            public int compare(TaskFailQueue o1, TaskFailQueue o2) {
	                if (null != o1 && null != o2 && o1.getFailqueueId()!=null&&o1.getFailqueueId().equals(o2.getFailqueueId())) {
	                    return 0;
	                }
	                return 1;
	            }
	        };
	}

	@Override
	public List<TaskFailQueue> selectTasks(String arg0, int queueNum,
			List<String> queryCondition, int fetchNum) throws Exception {
		// TODO Auto-generated method stub
		List<TaskFailQueue> results = new ArrayList<TaskFailQueue>();
		if (queryCondition.size() == 0) {
			return null;
		}
		
		try {
			if (queryCondition.size() != queueNum) {
				fetchNum = fetchNum * queueNum / queryCondition.size();
			}
			Map<String,Object> param = new HashMap<String, Object>();
			param.put("fetchNum", fetchNum);
			List<TaskFailQueue> records = failQueueService.query(param);			
			if (records != null) {
				log.info("调用 failQueueService.query fetchNum:{} records.size:{}" ,fetchNum, records.size());
				for (TaskFailQueue record : records) {
					if (!isMyTask(queueNum,record.getFailqueueId(), queryCondition)) {
						continue;
					}
					results.add(record);
				}
			}else{
				log.info("调用 failQueueService.query fetchNum:{} records is null",fetchNum);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;
	}

	@Override
    public boolean execute(Object[] taskArray, String arg1) throws Exception {
        if (taskArray == null) {
            return false;
        }
        List<TaskFailQueue> failQueueList = new ArrayList<TaskFailQueue>();
        for(Object obj : taskArray){
        	TaskFailQueue failQueue = (TaskFailQueue)obj;
        	failQueueList.add(failQueue);
        }
        failQueueService.lock(failQueueList);
        failQueueService.failData(failQueueList);
		return true;
	}
	
	private boolean isMyTask(long taskCount, long id, List<?> subQueues) {
		if (taskCount == subQueues.size())
			return true;
		long m = id % taskCount;
		for (Object o : subQueues) {
			if (m == Long.parseLong(o.toString()))
				return true;
		}
		return false;
	}

	public void init() throws Exception {
		managerFactory.createTBScheduleManager(taskType, ownSign);
	}

}
