package com.jd.bluedragon.distribution.reverse.dao;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.reverse.domain.ReverseReceive;

import java.util.Date;

public class ReverseReceiveDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private ReverseReceiveDao reverseReceiveDao;
	
	
	@Test
    public void testFindByPackageCode() {
        String packageCode = "Joe";
        reverseReceiveDao.findByPackageCode(packageCode);
    }
	
	@Test
    public void testAdd() {
        ReverseReceive parameter = new ReverseReceive();
        parameter.setOperatorId("James");
        parameter.setOperatorName("James");
        parameter.setReceiveTime(new Date());
        parameter.setSendCode("James");
        parameter.setPackageCode("James");
        parameter.setCanReceive(1);
        parameter.setRejectCode(1);
        parameter.setRejectMessage("James");
        parameter.setReceiveType(30);
        reverseReceiveDao.add(ReverseReceiveDao.class.getName(),parameter);
    }
	
	@Test
    public void testFindByPackageCodeAndSendCode() {
        ReverseReceive parameter = new ReverseReceive();
        parameter.setPackageCode("James");
        parameter.setSendCode("James");
        parameter.setReceiveType(30);
        reverseReceiveDao.findByPackageCodeAndSendCode("James","James",30);
    }
	
	@Test
    public void testFindByWaybillCodeAndSendCode() {
        ReverseReceive parameter = new ReverseReceive();
        parameter.setOrderId("James");
        parameter.setSendCode("James");
        reverseReceiveDao.findByWaybillCodeAndSendCode("James","James");
    }
	
	@Test
    public void testUpdate() {
        ReverseReceive parameter = new ReverseReceive();
        parameter.setOperatorId("James");
        parameter.setOperatorName("James");
        parameter.setReceiveTime(new Date());
        parameter.setSendCode("James");
        parameter.setPackageCode("James");
        parameter.setCanReceive(1);
        parameter.setRejectCode(1);
        parameter.setRejectMessage("James");
        parameter.setReceiveType(30);
        parameter.setId((long)1);
        reverseReceiveDao.update(ReverseReceiveDao.class.getName(),parameter);
    }
}
