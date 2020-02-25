package com.jd.bluedragon.distribution.inspection.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.box.dao.BoxDao;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import com.jd.bluedragon.distribution.inspection.domain.Inspection;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

public class InspectionDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private InspectionDao inspectionDao;

    @Test
    public void testAdd() {
        Inspection parameter = new Inspection();
        parameter.setInspectionId((long)2738);
        parameter.setWaybillCode("Joe1");
        parameter.setBoxCode("Stone1");
        parameter.setPackageBarcode("Jim1");
        parameter.setExceptionType("1");
        parameter.setInspectionType(50);
        parameter.setOperateType(1);
        parameter.setCreateUser("Stone");
        parameter.setCreateUserCode(276);
        parameter.setCreateTime(new Date());
        parameter.setCreateSiteCode(593);
        parameter.setReceiveSiteCode(204);
        parameter.setUpdateUser("James");
        parameter.setUpdateUserCode(542);
        parameter.setCreateTime(new Date());
        parameter.setThirdWaybillCode("Mary");
        parameter.setPopFlag(1);
        parameter.setPopSupId(830);
        parameter.setPopSupName("Joe");
        parameter.setQuantity(285);
        parameter.setCrossCode("James");
        parameter.setWaybillType(1);
        parameter.setPopReceiveType(3);
        parameter.setQueueNo("Jim");
        parameter.setDriverCode("Stone");
        parameter.setDriverName("Jone");
        parameter.setBusiId(639);
        parameter.setBusiName("Stone");
        inspectionDao.add(InspectionDao.namespace, parameter);
    }


    @Test
    public void testFindBPopJoinTotalCount() {
        Map parameter = new HashMap();
        // parameter.put("createSiteCode", new Object());
        // parameter.put("thirdWaybillCode", new Object());
        // parameter.put("waybillType", new Object());
        // parameter.put("startTime", new Object());
        // parameter.put("endTime", new Object());
        // parameter.put("popSupId", new Object());
        // parameter.put("popSupName", new Object());
        // parameter.put("busiId", new Object());
        // parameter.put("busiName", new Object());
        // parameter.put("createUserCode", new Object());
        // parameter.put("createUser", new Object());
        // parameter.put("queueNo", new Object());
        // parameter.put("driverCode", new Object());
        // parameter.put("driverName", new Object());
        inspectionDao.findBPopJoinTotalCount(parameter);
    }
	
	@Test
    public void testUpdatePop() {
        Inspection parameter = new Inspection();
        parameter.setExceptionType("Jax");
        parameter.setCreateUser("Joe");
        parameter.setCreateUserCode(384);
        parameter.setUpdateUser("Jim");
        parameter.setUpdateUserCode(71);
        parameter.setUpdateTime(new Date());
        parameter.setThirdWaybillCode("James");
        parameter.setPopSupId(622);
        parameter.setPopSupName("James");
        parameter.setQuantity(973);
        parameter.setCrossCode("Jax");
        parameter.setWaybillType(281);
        parameter.setPopReceiveType(248);
        parameter.setQueueNo("Mary");
        parameter.setDriverCode("Mary");
        parameter.setDriverName("James");
        parameter.setInspectionType(117);
        parameter.setWaybillCode("Jim");
        parameter.setCreateSiteCode(688);
        parameter.setPackageBarcode("Jim");
        inspectionDao.updatePop(parameter);
    }
	

	@Test
    public void testFindBPopJoinList() {
        Map parameter = new HashMap();
        // parameter.put("createSiteCode", new Object());
        // parameter.put("thirdWaybillCode", new Object());
        // parameter.put("waybillType", new Object());
        // parameter.put("startTime", new Object());
        // parameter.put("endTime", new Object());
        // parameter.put("popSupId", new Object());
        // parameter.put("popSupName", new Object());
        // parameter.put("busiId", new Object());
        // parameter.put("busiName", new Object());
        // parameter.put("createUserCode", new Object());
        // parameter.put("createUser", new Object());
        // parameter.put("queueNo", new Object());
        // parameter.put("driverCode", new Object());
        // parameter.put("driverName", new Object());
        // parameter.put("endIndex", new Object());
        // parameter.put("startIndex", new Object());
        inspectionDao.findBPopJoinList(parameter);
    }
	
	@Test
    public void testFindPopJoinTotalCount() {
        Map parameter = new HashMap();
        parameter.put("createSiteCode", 596);
        parameter.put("popReceiveType", 0);
         parameter.put("startTime", new Date());
         parameter.put("endTime", new Date());
         parameter.put("popSupName", "J");
         inspectionDao.findPopJoinTotalCount(parameter);
    }
	
	@Test
    public void testInspectionCount() {
        Inspection parameter = new Inspection();
        parameter.setBoxCode("Stone");
        parameter.setInspectionType(50);
        parameter.setCreateSiteCode(596);
        parameter.setReceiveSiteCode(204);
        inspectionDao.inspectionCount(parameter);
    }
	
	@Test
    public void testFindPopJoinList() {
        Map parameter = new HashMap();
       
        parameter.put("pageSize", 2);
        parameter.put("startIndex", 1);
        
        parameter.put("createSiteCode", 596);
        parameter.put("popReceiveType", 0);
         parameter.put("startTime", new Date());
         parameter.put("endTime", new Date());
         parameter.put("popSupName", "J");
        inspectionDao.findPopJoinList(parameter);
    }
	
	@Test
    public void testHaveInspection() {
        Inspection parameter = new Inspection();
        parameter.setPackageBarcode("James");
        parameter.setCreateSiteCode(1);
        inspectionDao.haveInspection(parameter);
    }
	
	@Test
    public void testUpdate() {
        Inspection parameter = new Inspection();
        parameter.setExceptionType("2");
        parameter.setUpdateUser("Jone");
        parameter.setUpdateUserCode(368);
        parameter.setUpdateTime(new Date());
        parameter.setBoxCode("Stone");
        parameter.setPackageBarcode("Jim");
        parameter.setCreateSiteCode(596);
        parameter.setReceiveSiteCode(204);
        parameter.setInspectionType(40);
        inspectionDao.update(InspectionDao.namespace, parameter);
    }
	
	@Test
    public void testFindPopByWaybillCodes() {
		List<String> parameter = new ArrayList<String>();
		parameter.add("123");
        inspectionDao.findPopByWaybillCodes(parameter);
    }
	
	@Test
    public void testUpdateYnByPackageFuzzy() {
        Inspection parameter = new Inspection();
        parameter.setUpdateUser("Jone");
        parameter.setUpdateUserCode(189);
        parameter.setInspectionType(987);
        parameter.setPackageBarcode("Jone");
        parameter.setCreateSiteCode(718);
        parameter.setReceiveSiteCode(724);
        parameter.setBoxCode("Jax");
        inspectionDao.updateYnByPackageFuzzy(parameter);
    }
	
	@Test
    public void testUpdateYnByPackage() {
        Inspection parameter = new Inspection();
        parameter.setUpdateUser("Stone");
        parameter.setUpdateUserCode(784);
        parameter.setInspectionType(948);
        parameter.setPackageBarcode("Jim");
        parameter.setCreateSiteCode(127);
        parameter.setReceiveSiteCode(741);
        parameter.setBoxCode("Jax");
        inspectionDao.updateYnByPackage(parameter);
    }

    @Test
    public void testSelectSelective() {
        testAdd();
        Inspection inspection = new Inspection();
        inspection.setWaybillCode("joe1");
        inspection.setPackageBarcode("jim1");
        Assert.assertEquals(1,inspectionDao.queryByCondition(inspection).size());
    }

    @Test
    public void testSelectCountSelective() {
        testAdd();
        Inspection inspection = new Inspection();
        inspection.setWaybillCode("joe1");
        inspection.setPackageBarcode("jim1");
        Assert.assertEquals(new Integer(8), inspectionDao.queryCountByCondition(inspection));
    }

    @Test
    public void testfindInspectionGather(){
        testAdd();
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("startTime","2020-02-20 00:00:00");
        paramMap.put("endTime","2020-02-20 23:59:59");
        paramMap.put("waybillCode","Joe1");
        Assert.assertEquals(1,inspectionDao.findInspectionGather(paramMap).size());
    }

    @Test
    public void testFindWaybillInspectionList(){
        testAdd();
        Inspection inspection = new Inspection();
        inspection.setWaybillCode("Joe1");
        inspection.setCreateSiteCode(593);
        Assert.assertEquals(new Integer(1),inspectionDao.verifyReverseInspectionGather(inspection));
    }

}
