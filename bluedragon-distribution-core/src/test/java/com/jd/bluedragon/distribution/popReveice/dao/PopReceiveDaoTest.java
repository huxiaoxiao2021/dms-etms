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
        // parameter.put("end", new Object());
        // parameter.put("start", new Object());
        popReceiveDao.findPopReceiveList(parameter);
    }
	
	@Test
    public void testFindByFingerPrint() {
        String fingerPrint = "Jax";
        popReceiveDao.findByFingerPrint(fingerPrint);
    }
	
	@Test
    public void testCount() {
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
        popReceiveDao.count(parameter);
    }
	
	@Test
    public void testAdd() {
        PopReceive parameter = new PopReceive();
        parameter.setReceiveType(10);
        parameter.setWaybillCode("Stone");
        parameter.setThirdWaybillCode("Jone");
        parameter.setOriginalNum(595);
        parameter.setActualNum(698);
        parameter.setCreateSiteCode(139);
        parameter.setCreateSiteName("Joe");
        parameter.setOperatorCode(458);
        parameter.setOperatorName("Joe");
        parameter.setOperateTime(new Date());
        parameter.setIsReverse(483);
        parameter.setFingerPrint("Jim");
        popReceiveDao.add(popReceiveDao.getClass().getName(),parameter);
    }
	
	@Test
    public void testUpdate() {
        PopReceive parameter = new PopReceive();
        parameter.setThirdWaybillCode("Jim");
        parameter.setOriginalNum(440);
        parameter.setActualNum(936);
        parameter.setCreateSiteCode(940);
        parameter.setCreateSiteName("Jone");
        parameter.setOperatorCode(554);
        parameter.setOperatorName("James");
        parameter.setOperateTime(new Date());
        parameter.setWaybillCode("Stone");
        parameter.setFingerPrint("Jax");
        popReceiveDao.update(popReceiveDao.getClass().getName(),parameter);
    }
}
