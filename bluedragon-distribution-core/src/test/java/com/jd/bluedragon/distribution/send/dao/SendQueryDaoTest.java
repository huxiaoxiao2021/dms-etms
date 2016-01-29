package com.jd.bluedragon.distribution.send.dao;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.send.domain.SendQuery;

import java.util.Date;

public class SendQueryDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private SendQueryDao sendQueryDao;
	
	
	@Test
    public void testQueryBySendCode() {
        String sendMId = "James";
        Assert.assertTrue(sendQueryDao.queryBySendCode(sendMId)!=null);
    }


}
