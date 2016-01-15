package com.jd.bluedragon.distribution.send.dao;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.send.domain.SendQuery;

public class SendQueryDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private SendQueryDao sendQueryDao;
	
	
	@Test
    public void testQueryBySendCode() {
        String sendMId = "Jone";
        sendQueryDao.queryBySendCode(sendMId);
    }
	
	@Test
    public void testAdd() {
        SendQuery parameter = new SendQuery();
        parameter.setSendCode("James");
        parameter.setCreateSiteCode(78);
        parameter.setCreateUserRealName("Jone");
        parameter.setCreateUserCode(302);
        parameter.setUpdateUserRealName("Mary");
        parameter.setUpdateUserCode(198);
        parameter.setCreateTime(new Date());
        parameter.setUpdateTime(new Date());
        parameter.setYn(499);
        parameter.setIpAddress("James");
        sendQueryDao.add(parameter);
    }
}
