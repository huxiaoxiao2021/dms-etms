package com.jd.bluedragon.distribution.popPrint.dao;

import org.junit.Assert;

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.api.request.PopQueueQuery;
import com.jd.bluedragon.distribution.popPrint.domain.PopQueue;

public class PopQueueDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private PopQueueDao popQueueDao;
	
	
	@Test
    public void testGetCount() {
        PopQueueQuery parameter = new PopQueueQuery();
        parameter.setQueueNo("James");
        parameter.setCreateSiteCode("Jax");
        parameter.setQueueStatus(829);
        parameter.setExpressCode("Joe");
        parameter.setPrintStartTime("James");
        parameter.setPrintEndTime("Jim");
        popQueueDao.getCount(parameter);
    }
	
	@Test
    public void testGetCurrentWaitNo() {
        Integer createSiteCode = 973;
        popQueueDao.getCurrentWaitNo(createSiteCode);
    }
	
	@Test
    public void testGetPopQueueList() {
        PopQueueQuery parameter = new PopQueueQuery();
        parameter.setQueueNo("Joe");
        parameter.setCreateSiteCode("Joe");
        parameter.setQueueStatus(15);
        parameter.setExpressCode("Joe");
        parameter.setPrintStartTime("Joe");
        parameter.setPrintEndTime("James");
        parameter.setEnd(737);
        parameter.setStart(752);
        popQueueDao.getPopQueueList(parameter);
    }
	
	@Test
    public void testUpdatePopQueue() {
        PopQueue parameter = new PopQueue();
        parameter.setQueueStatus(396);
        parameter.setStartTime(new Date());
        parameter.setEndTime(new Date());
        parameter.setQueueNo("Mary");
        popQueueDao.updatePopQueue(parameter);
    }
	
	@Test
    public void testGetPopQueueByQueueNo() {
        String queueNo = "Stone";
        popQueueDao.getPopQueueByQueueNo(queueNo);
    }
	
	@Test
    public void testInsertPopQueue() {
        PopQueue parameter = new PopQueue();
        parameter.setQueueNo("Mary");
        parameter.setCreateSiteCode(507);
        parameter.setCreateSiteName("Stone");
        parameter.setQueueType(1);
        parameter.setExpressCode("Mary");
        parameter.setExpressName("Mary");
        parameter.setWaitNo(580);
        parameter.setCreateUserCode(905);
        parameter.setCreateUser("Stone");
        parameter.setOperateTime(new Date());
        popQueueDao.insertPopQueue(parameter);
    }
}
