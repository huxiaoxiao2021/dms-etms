package com.jd.bluedragon.distribution.departure.dao;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.api.request.DepartureTmpRequest;

public class DepartureTmpDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private DepartureTmpDao departureTmpDao;
	
	
	@Test
    public void testQueryDepartureTmpByBatchCode() {
        String batchKey = "Jone";
        departureTmpDao.queryDepartureTmpByBatchCode(batchKey);
    }
	
	@Test
    public void testInsert() {
        DepartureTmpRequest parameter = new DepartureTmpRequest();
        parameter.setBatchCode("James");
        parameter.setSendCode("Jax");
        parameter.setOperateTime(new Date());
        parameter.setThirdWaybillCode("Jax");
        departureTmpDao.insert(parameter);
    }
}
