package com.jd.bluedragon.distribution.batch.dao;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.batch.domain.BatchInfo;
import java.util.Map;

public class BatchInfoDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private BatchInfoDao batchInfoDao;
	
	
	@Test
    public void testFindAllBatchInfo() {
        BatchInfo parameter = new BatchInfo();
        parameter.setCreateSiteCode(906);
        parameter.setCreateTime(new Date());
        parameter.setUpdateTime(new Date());
        batchInfoDao.findAllBatchInfo(parameter);
    }
	
	@Test
    public void testFindBatchInfoByCode() {
        String batchCode = "Mary";
        batchInfoDao.findBatchInfoByCode(batchCode);
    }
	
	@Test
    public void testFindBatchInfo() {
        BatchInfo parameter = new BatchInfo();
        parameter.setCreateSiteCode(59);
        batchInfoDao.findBatchInfo(parameter);
    }
	
	@Test
    public void testAdd() {
        BatchInfo parameter = new BatchInfo();
        parameter.setBatchCode("Mary");
        parameter.setCreateSiteCode(388);
        parameter.setCreateUser("Stone");
        parameter.setCreateUserCode(473);
        parameter.setUpdateUserCode(139);
        parameter.setUpdateUser("Stone");
        parameter.setUpdateTime(new Date());
        batchInfoDao.add(parameter);
    }
	
	@Test
    public void testFindCurrent() {
        Map parameter = new HashMap();
        // parameter.put("createSiteCode", new Object());
        // parameter.put("operateTime", new Object());
        // parameter.put("operateTime", new Object());
        // parameter.put("minTime", new Object());
        batchInfoDao.findCurrent(parameter);
    }
	
	@Test
    public void testUpdate() {
        BatchInfo parameter = new BatchInfo();
        parameter.setUpdateTime(new Date());
        parameter.setBatchCode("Mary");
        batchInfoDao.update(parameter);
    }
	
	@Test
    public void testFindMaxCreateTimeBatchInfo() {
        BatchInfo parameter = new BatchInfo();
        parameter.setCreateSiteCode(833);
        parameter.setCreateSiteCode(99);
        batchInfoDao.findMaxCreateTimeBatchInfo(parameter);
    }
}
