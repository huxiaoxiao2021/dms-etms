package com.jd.bluedragon.distribution.batch.dao;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.batch.domain.BatchSend;
import com.jd.bluedragon.distribution.batch.domain.BatchSendRequest;

public class BatchSendDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private BatchSendDao batchSendDao;
	
	
	@Test
    public void testFindBatchSend() {
        BatchSendRequest parameter = new BatchSendRequest();
        parameter.setCreateSiteCode(840);
        parameter.setReceiveCodes("Joe");
        parameter.setBatchCode("Mary");
        parameter.setCreateTime(new Date());
        parameter.setUpdateTime(new Date());
        batchSendDao.findBatchSend(parameter);
    }
	
	@Test
    public void testInsertOne() {
        BatchSend parameter = new BatchSend();
        parameter.setBatchCode("Mary");
        parameter.setCreateSiteCode(664);
        parameter.setReceiveSiteCode(591);
        parameter.setSendCode("Jax");
        parameter.setCreateUser("Jax");
        parameter.setCreateUserCode(830);
        parameter.setUpdateUserCode(344);
        parameter.setUpdateUser("Jim");
        parameter.setSendStatus(72);
        batchSendDao.insertOne(parameter);
    }
	
	@Test
    public void testReadBySendCode() {
        String value = "Jax";
        batchSendDao.readBySendCode(value);
    }
	
	@Test
    public void testGetBatchSend() {
        BatchSend parameter = new BatchSend();
        parameter.setBatchCode("Joe");
        parameter.setReceiveSiteCode(901);
        batchSendDao.getBatchSend(parameter);
    }
	
	@Test
    public void testUpdateSendCarState() {
        BatchSend parameter = new BatchSend();
        parameter.setSendCarState(325);
        parameter.setSendCarOperateTime(new Date());
        parameter.setSendCode("Mary");
        parameter.setSendCarOperateTime(new Date());
        parameter.setSendCarState(364);
        batchSendDao.updateSendCarState(parameter);
    }
	
	@Test
    public void testBatchUpdateStatus() {
        BatchSend parameter = new BatchSend();
        parameter.setSendCode("Jim");
        batchSendDao.batchUpdateStatus(parameter);
    }
}
