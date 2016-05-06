package com.jd.bluedragon.distribution.batch.dao;

import org.junit.Assert;
import java.util.Date;
import org.junit.Test;
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
        parameter.setCreateSiteCode(664);
        parameter.setReceiveCodes("591");
        parameter.setBatchCode("123456789");
        parameter.setCreateTime(new Date());
        parameter.setUpdateTime(new Date());
        Assert.assertNotNull(batchSendDao.findBatchSend(parameter));
    }
	
	//@Test
    public void testInsertOne() {
        BatchSend parameter = new BatchSend();
        parameter.setBatchCode("1234567892");
        parameter.setCreateSiteCode(664);
        parameter.setReceiveSiteCode(591);
        parameter.setSendCode("1-1234567891");
        parameter.setCreateUser("Jax");
        parameter.setCreateUserCode(830);
        parameter.setUpdateUserCode(344);
        parameter.setUpdateUser("Jim");
        parameter.setSendStatus(1);
        Assert.assertEquals(1, batchSendDao.insert(parameter));
    }
	
	@Test
    public void testReadBySendCode() {
        String value = "1-123456789";
        Assert.assertNotNull(batchSendDao.readBySendCode(value));
    }
	
	@Test
    public void testGetBatchSend() {
        BatchSend parameter = new BatchSend();
        parameter.setBatchCode("123456789");
        parameter.setReceiveSiteCode(591);
        Assert.assertNotNull(batchSendDao.read("123456789",591));
    }
	
	@Test
    public void testUpdateSendCarState() {
        BatchSend parameter = new BatchSend();
        parameter.setSendCarState(325);
        parameter.setSendCarOperateTime(new Date());
        parameter.setSendCode("1-123456789");
        parameter.setSendCarOperateTime(new Date());
        parameter.setSendCarState(2);
        Assert.assertEquals(new Integer (1), batchSendDao.updateSendCarState(parameter));
    }
	
	@Test
    public void testBatchUpdateStatus() {
        BatchSend parameter = new BatchSend();
        parameter.setSendCode("1-123456789");
        Assert.assertEquals(new Integer (1), batchSendDao.batchUpdateStatus(parameter));
    }
}
