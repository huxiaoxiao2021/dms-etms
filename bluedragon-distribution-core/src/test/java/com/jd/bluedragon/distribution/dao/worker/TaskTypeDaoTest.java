package com.jd.bluedragon.distribution.dao.worker;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import com.jd.bluedragon.distribution.worker.dao.TBTaskTypeDao;
import com.jd.bluedragon.distribution.worker.domain.TBTaskType;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TaskTypeDaoTest  extends AbstractDaoIntegrationTest{

	@Autowired
	private TBTaskTypeDao taskTypeDao;
	
	//@Test
	public void insertSingle(){
		for(int i=1 ;i<10 ;i++){
			TBTaskType domain = new TBTaskType();
			domain.setBaseTaskType("baseTaskType"+i);
			domain.setDealBeanName("dealBeanName"+i);
			domain.setExecuteNumber(i);
			domain.setExpireOwnSignInterval(i);
			domain.setFetchDataNumber(i);
			domain.setHeartBeatRate(i);
			domain.setJudgeDeadInterval(i);
			domain.setLastAssignUuid("1"+i);
			domain.setPermitRunEndTime("1"+i);
			domain.setPermitRunStartTime("1"+i);
		    domain.setProcessorType("1"+i);
		    domain.setSleepTimeInterval(i);
		    domain.setSleepTimeNoData(i);
		    domain.setTaskQueueNumber(i);
		    domain.setThreadNumber(i);
		    domain.setVersion(i);
			taskTypeDao.insertSingle(domain);
		}
	}
	
	//@Test
	public void selectByNameUsePager(){
		Pager<String> pager = new Pager<String>();
		pager.setStartIndex(1);
		pager.setPageSize(5);
		List<TBTaskType> list = taskTypeDao.selectByNameUsePager(pager);
		System.out.println(list.size());
    }
	
	//@Test
	public void  selectCountByName(){
		Pager<String> pager = new Pager<String>();
		pager.setData("baseTaskType");
		int num = taskTypeDao.selectCountByName(pager);
		System.out.println(num);
	}
	
	//@Test
	public void updateSingleById(){
		TBTaskType domain = new TBTaskType();
		domain.setId(133);
		domain.setHeartBeatRate(133);
		taskTypeDao.updateSingleById(domain);
	}
}
