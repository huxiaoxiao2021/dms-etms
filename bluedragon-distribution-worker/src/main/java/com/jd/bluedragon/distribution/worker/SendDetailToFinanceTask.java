package com.jd.bluedragon.distribution.worker;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.failqueue.service.IFailQueueService;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.taobao.pamirs.schedule.IScheduleTaskDealMulti;
import com.taobao.pamirs.schedule.TBScheduleManagerFactory;

public class SendDetailToFinanceTask implements IScheduleTaskDealMulti<SendDetail> {

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
	public Comparator<SendDetail> getComparator() {
		return new Comparator<SendDetail>() {
			public int compare(SendDetail o1, SendDetail o2) {
				if (null != o1 && null != o2 && o1.getSendDId() != null
				        && o1.getSendDId().equals(o2.getSendDId())) {
					return 0;
				}
				return 1;
			}
		};
	}

	@Override
	public List<SendDetail> selectTasks(String arg0, int queueNum, List<String> queryCondition,
	        int fetchNum) throws Exception {
		List<SendDetail> results = new ArrayList<SendDetail>();
		// if (queryCondition.size() == 0) {
		// return null;
		// }
		//
		// try {
		// if (queryCondition.size() != queueNum) {
		// fetchNum = fetchNum * queueNum / queryCondition.size();
		// }
		// Map<String,Object> param = new HashMap<String, Object>();
		// param.put("fetchNum", fetchNum);
		// log.info("调用 failQueueService.querySendDatail fetchNum:" +
		// fetchNum);
		// List<SendDetail> records = failQueueService.querySendDatail(param);
		// if (records != null) {
		// for (SendDetail record : records) {
		// if (!isMyTask(queueNum,record.getSendDId(), queryCondition)) {
		// continue;
		// }
		// results.add(record);
		// }
		// }
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		return results;
	}

	@Override
	public boolean execute(Object[] taskArray, String arg1) throws Exception {
		// if (taskArray == null) {
		// return false;
		// }
		// List<SendDetail> sendDatailList = new ArrayList<SendDetail>();
		// for(Object obj : taskArray){
		// SendDetail sendDatail = (SendDetail)obj;
		// sendDatailList.add(sendDatail);
		// }
		// log.info("调用 failQueueService.sendDatailNewData(sendDatailList) 数据量:"
		// + sendDatailList.size());
		// failQueueService.sendDatailNewData(sendDatailList);
		return false;
	}

	public void init() throws Exception {
		managerFactory.createTBScheduleManager(taskType, ownSign);
	}

}
