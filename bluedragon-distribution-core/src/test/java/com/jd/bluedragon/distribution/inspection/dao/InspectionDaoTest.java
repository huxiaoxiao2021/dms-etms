package com.jd.bluedragon.distribution.inspection.dao;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.List;
import com.jd.bluedragon.distribution.inspection.domain.Inspection;

public class InspectionDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private InspectionDao inspectionDao;
	
	
	@Test
    public void testQueryListByBox() {
        Inspection parameter = new Inspection();
        parameter.setBoxCode("Jone");
        parameter.setInspectionType(953);
        parameter.setCreateSiteCode(318);
        parameter.setReceiveSiteCode(613);
        inspectionDao.queryListByBox(parameter);
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
    public void testAdd() {
        Inspection parameter = new Inspection();
        parameter.setInspectionId((long)2738);
        parameter.setWaybillCode("Joe");
        parameter.setBoxCode("Stone");
        parameter.setPackageBarcode("Jim");
        parameter.setExceptionType("Jax");
        parameter.setInspectionType(946);
        parameter.setOperateType(486);
        parameter.setCreateUser("Stone");
        parameter.setCreateUserCode(276);
        parameter.setCreateTime(new Date());
        parameter.setCreateSiteCode(596);
        parameter.setReceiveSiteCode(204);
        parameter.setUpdateUser("James");
        parameter.setUpdateUserCode(542);
        parameter.setCreateTime(new Date());
        parameter.setThirdWaybillCode("Mary");
        parameter.setPopFlag(284);
        parameter.setPopSupId(830);
        parameter.setPopSupName("Joe");
        parameter.setQuantity(285);
        parameter.setCrossCode("James");
        parameter.setWaybillType(282);
        parameter.setPopReceiveType(546);
        parameter.setQueueNo("Jim");
        parameter.setDriverCode("Stone");
        parameter.setDriverName("Jone");
        parameter.setBusiId(639);
        parameter.setBusiName("Stone");
        inspectionDao.add(parameter);
    }
	
	@Test
    public void testQueryForObject() {
        Inspection parameter = new Inspection();
        parameter.setThirdWaybillCode("Jax");
        parameter.setInspectionId((long)1709);
        parameter.setWaybillCode("Jim");
        parameter.setBoxCode("Jone");
        parameter.setPackageBarcode("Jone");
        parameter.setStatus(168);
        parameter.setExceptionType("Joe");
        parameter.setInspectionType(325);
        parameter.setCreateUser("Jone");
        parameter.setCreateUserCode(145);
        parameter.setCreateTime(new Date());
        parameter.setCreateSiteCode(377);
        parameter.setReceiveSiteCode(26);
        parameter.setUpdateUser("James");
        parameter.setUpdateUserCode(282);
        parameter.setUpdateTime(new Date());
        parameter.setYn(358);
        inspectionDao.queryForObject(parameter);
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
        // parameter.put("createSiteCode", new Object());
        // parameter.put("thirdWaybillCode", new Object());
        // parameter.put("waybillType", new Object());
        // parameter.put("startTime", new Object());
        // parameter.put("endTime", new Object());
        // parameter.put("popSupId", new Object());
        // parameter.put("popSupName", new Object());
        // parameter.put("createUserCode", new Object());
        // parameter.put("createUser", new Object());
        // parameter.put("queueNo", new Object());
        inspectionDao.findPopJoinTotalCount(parameter);
    }
	
	@Test
    public void testSelectSelective() {
        Inspection parameter = new Inspection();
        parameter.setThirdWaybillCode("Stone");
        parameter.setInspectionId((long)8216);
        parameter.setWaybillCode("Mary");
        parameter.setBoxCode("Jax");
        parameter.setPackageBarcode("Jone");
        parameter.setStatus(602);
        parameter.setExceptionType("Mary");
        parameter.setInspectionType(704);
        parameter.setCreateUser("James");
        parameter.setCreateUserCode(222);
        parameter.setCreateTime(new Date());
        parameter.setCreateSiteCode(823);
        parameter.setReceiveSiteCode(949);
        parameter.setUpdateUser("Jim");
        parameter.setUpdateUserCode(207);
        parameter.setUpdateTime(new Date());
        parameter.setYn(276);
        parameter.setLimitNo(125);
        inspectionDao.selectSelective(parameter);
    }
	
	@Test
    public void testInspectionCount() {
        Inspection parameter = new Inspection();
        parameter.setBoxCode("Jone");
        parameter.setInspectionType(974);
        parameter.setCreateSiteCode(204);
        parameter.setReceiveSiteCode(796);
        inspectionDao.inspectionCount(parameter);
    }
	
	@Test
    public void testSelectByPrimaryKey() {
        Long inspectionId = (long)6650;
        inspectionDao.selectByPrimaryKey(inspectionId);
    }
	
	@Test
    public void testFindPopJoinList() {
        Map parameter = new HashMap();
        // parameter.put("createSiteCode", new Object());
        // parameter.put("thirdWaybillCode", new Object());
        // parameter.put("waybillType", new Object());
        // parameter.put("startTime", new Object());
        // parameter.put("endTime", new Object());
        // parameter.put("popSupId", new Object());
        // parameter.put("popSupName", new Object());
        // parameter.put("createUserCode", new Object());
        // parameter.put("createUser", new Object());
        // parameter.put("queueNo", new Object());
        // parameter.put("endIndex", new Object());
        // parameter.put("startIndex", new Object());
        inspectionDao.findPopJoinList(parameter);
    }
	
	@Test
    public void testHaveInspection() {
        Inspection parameter = new Inspection();
        parameter.setPackageBarcode("James");
        inspectionDao.haveInspection(parameter);
    }
	
	@Test
    public void testUpdate() {
        Inspection parameter = new Inspection();
        parameter.setBoxCode("Joe");
        parameter.setExceptionType("Mary");
        parameter.setCreateSiteCode(701);
        parameter.setReceiveSiteCode(91);
        parameter.setUpdateUser("Jone");
        parameter.setUpdateUserCode(368);
        parameter.setUpdateTime(new Date());
        parameter.setBoxCode("Stone");
        parameter.setOperateType(458);
        parameter.setInspectionType(56);
        parameter.setWaybillCode("Jax");
        parameter.setPackageBarcode("Mary");
        parameter.setCreateSiteCode(215);
        parameter.setReceiveSiteCode(662);
        inspectionDao.update(parameter);
    }
	
	@Test
    public void testFindPopByWaybillCodes() {
        inspectionDao.findPopByWaybillCodes();
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
    public void testUpdateStatusBatchByPrimaryKey() {
        List parameter = new ArrayList();
        //set property for record.inspectionId
        inspectionDao.updateStatusBatchByPrimaryKey(parameter);
    }
	
	@Test
    public void testUpdateByBoxPackageBarcode() {
        Map parameter = new HashMap();
        // parameter.put("waybillCode", new Object());
        // parameter.put("status", new Object());
        // parameter.put("exceptionType", new Object());
        // parameter.put("inspectionType", new Object());
        // parameter.put("createSiteCode", new Object());
        //set property for record.receiveSiteCode
        // parameter.put("updateUser", new Object());
        // parameter.put("updateUserCode", new Object());
        // parameter.put("updateTime", new Object());
        // parameter.put("yn", new Object());
        // parameter.put("boxCode", new Object());
        // parameter.put("packageBarcode", new Object());
        inspectionDao.updateByBoxPackageBarcode(parameter);
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
}
