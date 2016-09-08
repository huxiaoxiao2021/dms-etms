package com.jd.bluedragon.distribution.popReveice.dao;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import com.jd.bluedragon.distribution.popReveice.domain.PopReceive;

public class PopReceiveDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private PopReceiveDao popReceiveDao;
	
	
	@Test
    public void testFindListByPopReceive() {
        Map parameter = new HashMap();
        // parameter.put("waybillCode", new Object());
        // parameter.put("thirdWaybillCode", new Object());
        // parameter.put("createSiteCode", new Object());
        // parameter.put("createSiteName", new Object());
        // parameter.put("operatorCode", new Object());
        // parameter.put("operatorName", new Object());
        // parameter.put("startTime", new Object());
        // parameter.put("endTime", new Object());
        // parameter.put("operateStartTime", new Object());
        // parameter.put("operateEndTime", new Object());
        popReceiveDao.findListByPopReceive(parameter);
    }
	
	@Test
    public void testFindPopReceiveList() {
        Map parameter = new HashMap();
        parameter.put("waybillCode", "James");
        parameter.put("thirdWaybillCode", "James");
        parameter.put("createSiteCode", 910);
        parameter.put("createSiteName","James");
        parameter.put("operatorCode", 910);
        parameter.put("operatorName", "James");
        parameter.put("startTime", new Date(100,10,28,10,12,50));
        parameter.put("endTime", new Date());
        parameter.put("operateStartTime", new Date(100,10,28,10,12,50));
        parameter.put("operateEndTime", new Date());
        parameter.put("end",  10);
        parameter.put("start", 0);
        Assert.assertTrue(popReceiveDao.findPopReceiveList(parameter).size() > 0);
    }
	
	@Test
    public void testFindByFingerPrint() {
        String fingerPrint = "James";
        Assert.assertNotNull(popReceiveDao.findByFingerPrint(fingerPrint));
    }
	
	@Test
    public void testCount() {
        Map parameter = new HashMap();
        parameter.put("waybillCode", "James");
        parameter.put("thirdWaybillCode", "James");
        parameter.put("createSiteCode", 910);
        parameter.put("createSiteName", "James");
        parameter.put("operatorCode", 910);
        parameter.put("operatorName", "James");
        parameter.put("startTime", new Date(100,10,28,10,12,50));
        parameter.put("endTime", new Date());
        parameter.put("operateStartTime", new Date(100,10,28,10,12,50));
        parameter.put("operateEndTime", new Date());
        popReceiveDao.count(parameter);
    }
	
	@Test
    public void testAdd() {
        PopReceive parameter = new PopReceive();
        parameter.setReceiveType(10);
        parameter.setWaybillCode("James");
        parameter.setThirdWaybillCode("James");
        parameter.setOriginalNum(3);
        parameter.setActualNum(1);
        parameter.setCreateSiteCode(910);
        parameter.setCreateSiteName("James");
        parameter.setOperatorCode(910);
        parameter.setOperatorName("James");
        parameter.setOperateTime(new Date());
        parameter.setIsReverse(1);
        parameter.setFingerPrint("James");
        Assert.assertTrue(popReceiveDao.add(popReceiveDao.getClass().getName(), parameter) > 0);
    }
	
	@Test
    public void testUpdate() {
        PopReceive parameter = new PopReceive();
        parameter.setThirdWaybillCode("James");
        parameter.setOriginalNum(1);
        parameter.setActualNum(1);
        parameter.setCreateSiteCode(910);
        parameter.setCreateSiteName("James");
        parameter.setOperatorCode(910);
        parameter.setOperatorName("James");
        parameter.setOperateTime(new Date());
        parameter.setWaybillCode("James");
        parameter.setFingerPrint("James");
        Assert.assertTrue(popReceiveDao.update(popReceiveDao.getClass().getName(), parameter)>0);
    }
}
