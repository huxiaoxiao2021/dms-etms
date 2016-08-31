package com.jd.bluedragon.distribution.batch.dao;

import java.util.Date;

import com.jd.bluedragon.distribution.basic.IntegerCellValidate;
import org.junit.Assert;
import org.junit.Test;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import com.jd.bluedragon.distribution.batch.domain.BatchInfo;

public class BatchInfoDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private BatchInfoDao batchInfoDao;

	@Test
    public void testAdd() {
        BatchInfo parameter = new BatchInfo();
        parameter.setBatchCode("12345678");
        parameter.setCreateSiteCode(388);
        parameter.setCreateUser("Stone");
        parameter.setCreateUserCode(473);
        parameter.setUpdateUserCode(139);
        parameter.setUpdateUser("Stone");
        parameter.setUpdateTime(new Date());
        Assert.assertEquals(new Integer (1), batchInfoDao.add(BatchInfoDao.namespace ,parameter));
    }
	
	@Test
    public void testFindCurrent() {
		Assert.assertNotNull(batchInfoDao.findCurrent(388,new Date()));
    }
	
	@Test
    public void testUpdate() {
        BatchInfo parameter = new BatchInfo();
        parameter.setUpdateTime(new Date());
        parameter.setBatchCode("12345678");
        Assert.assertEquals(new Integer (1),batchInfoDao.update(BatchInfoDao.namespace ,parameter));
    }
	
	@Test
    public void testFindMaxCreateTimeBatchInfo() {
        BatchInfo parameter = new BatchInfo();
        parameter.setCreateSiteCode(388);
        Assert.assertNotNull(batchInfoDao.findMaxCreateTimeBatchInfo(parameter));
    }


    @Test
    public void testFindAllBatchInfo() {
        BatchInfo parameter = new BatchInfo();
        parameter.setCreateSiteCode(388);
        parameter.setCreateTime(new Date());
        parameter.setUpdateTime(new Date());
        Assert.assertNotNull(batchInfoDao.findAllBatchInfo(parameter));
    }

    @Test
    public void testFindBatchInfoByCode() {
        String batchCode = "12345678";
        Assert.assertNotNull(batchInfoDao.findBatchInfoByCode(batchCode));
    }

    @Test
    public void testFindBatchInfo() {
        BatchInfo parameter = new BatchInfo();
        parameter.setCreateSiteCode(388);
        Assert.assertNotNull(batchInfoDao.findBatchInfo(parameter));
    }
}
