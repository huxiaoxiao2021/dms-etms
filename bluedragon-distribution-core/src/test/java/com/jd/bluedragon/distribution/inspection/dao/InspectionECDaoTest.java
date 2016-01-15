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
import com.jd.bluedragon.distribution.inspection.domain.InspectionEC;

public class InspectionECDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private InspectionECDao inspectionECDao;
	
	
	@Test
    public void testGet() {
        InspectionEC parameter = new InspectionEC();
        parameter.setCheckId((long)851);
        inspectionECDao.get(parameter);
    }
	
	@Test
    public void testAdd() {
        InspectionEC parameter = new InspectionEC();
        parameter.setCheckId((long)2968);
        parameter.setWaybillCode("Mary");
        parameter.setBoxCode("Jone");
        parameter.setPackageBarcode("Jone");
        parameter.setInspectionECType(967);
        parameter.setStatus(479);
        parameter.setInspectionType(222);
        parameter.setExceptionType("Mary");
        parameter.setCreateUser("James");
        parameter.setCreateUserCode(878);
        parameter.setCreateTime(new Date());
        parameter.setCreateSiteCode(656);
        parameter.setReceiveSiteCode(601);
        parameter.setUpdateUser("Mary");
        parameter.setUpdateUserCode(564);
        parameter.setUpdateTime(new Date());
        inspectionECDao.add(parameter);
    }
	
	@Test
    public void testTotalThirdByParams() {
        Map parameter = new HashMap();
        // parameter.put("boxCode", new Object());
        // parameter.put("receiveSiteCode", new Object());
        // parameter.put("createSiteCode", new Object());
        // parameter.put("receiveSiteCode", new Object());
        // parameter.put("createSiteCode", new Object());
        // parameter.put("inspectionECType", new Object());
        // parameter.put("boxCode", new Object());
        // parameter.put("waybillCode", new Object());
        // parameter.put("packageBarcode", new Object());
        // parameter.put("createUserCode", new Object());
        // parameter.put("updateUserCode", new Object());
        inspectionECDao.totalThirdByParams(parameter);
    }
	
	@Test
    public void testQueryByThird() {
        InspectionEC parameter = new InspectionEC();
        parameter.setReceiveSiteCode(626);
        parameter.setCreateSiteCode(342);
        parameter.setInspectionECType(224);
        parameter.setBoxCode("Joe");
        parameter.setBoxCode("Jim");
        parameter.setReceiveSiteCode(164);
        parameter.setCreateSiteCode(940);
        inspectionECDao.queryByThird(parameter);
    }
	
	@Test
    public void testUpdateForSorting() {
        InspectionEC parameter = new InspectionEC();
        parameter.setStatus(181);
        parameter.setBoxCode("Mary");
        parameter.setUpdateUser("Mary");
        parameter.setUpdateUserCode(978);
        parameter.setCreateSiteCode(710);
        parameter.setBoxCode("James");
        parameter.setInspectionECType(890);
        parameter.setPackageBarcode("Jone");
        parameter.setWaybillCode("Jone");
        parameter.setReceiveSiteCode(152);
        inspectionECDao.updateForSorting(parameter);
    }
	
	@Test
    public void testGetOneUntreated() {
        inspectionECDao.getOneUntreated();
    }
	
	@Test
    public void testUpdateStatusFuzzy() {
        InspectionEC parameter = new InspectionEC();
        parameter.setStatus(755);
        parameter.setUpdateUser("Jim");
        parameter.setUpdateUserCode(542);
        parameter.setUpdateTime(new Date());
        parameter.setCreateSiteCode(792);
        parameter.setPackageBarcode("James");
        parameter.setWaybillCode("Stone");
        parameter.setBoxCode("Jax");
        parameter.setReceiveSiteCode(266);
        inspectionECDao.updateStatusFuzzy(parameter);
    }
	
	@Test
    public void testInspectionCount() {
        InspectionEC parameter = new InspectionEC();
        parameter.setInspectionECType(856);
        parameter.setInspectionType(804);
        parameter.setYn(821);
        parameter.setBoxCode("Joe");
        parameter.setWaybillCode("Stone");
        parameter.setPackageBarcode("James");
        parameter.setCreateSiteCode(602);
        parameter.setReceiveSiteCode(218);
        inspectionECDao.inspectionCount(parameter);
    }
	
	@Test
    public void testQueryThirdByParams() {
        Map parameter = new HashMap();
        // parameter.put("boxCode", new Object());
        // parameter.put("receiveSiteCode", new Object());
        // parameter.put("createSiteCode", new Object());
        // parameter.put("receiveSiteCode", new Object());
        // parameter.put("createSiteCode", new Object());
        // parameter.put("inspectionECType", new Object());
        // parameter.put("boxCode", new Object());
        // parameter.put("waybillCode", new Object());
        // parameter.put("packageBarcode", new Object());
        // parameter.put("createUserCode", new Object());
        // parameter.put("updateUserCode", new Object());
        // parameter.put("endIndex", new Object());
        // parameter.put("startIndex", new Object());
        inspectionECDao.queryThirdByParams(parameter);
    }
	
	@Test
    public void testQueryExceptionsCore() {
        Map parameter = new HashMap();
        // parameter.put("boxCode", new Object());
        // parameter.put("receiveSiteCode", new Object());
        // parameter.put("createSiteCode", new Object());
        inspectionECDao.queryExceptionsCore(parameter);
    }
	
	@Test
    public void testUpdateBatch() {
        List parameter = new ArrayList();
        //set property for record.createSiteCode
        //set property for record.inspectionECType
        inspectionECDao.updateBatch(parameter);
    }
	
	@Test
    public void testUpdateOne() {
        InspectionEC parameter = new InspectionEC();
        parameter.setUpdateUser("Stone");
        parameter.setUpdateUserCode(641);
        parameter.setUpdateTime(new Date());
        parameter.setStatus(214);
        parameter.setWaybillCode("Joe");
        parameter.setBoxCode("Jax");
        parameter.setPackageBarcode("Jim");
        parameter.setInspectionType(699);
        parameter.setInspectionECType(632);
        parameter.setCreateSiteCode(14);
        parameter.setReceiveSiteCode(285);
        inspectionECDao.updateOne(parameter);
    }
	
	@Test
    public void testUpdate() {
        inspectionECDao.update();
    }
	
	@Test
    public void testExceptionCountByBox() {
        Map parameter = new HashMap();
        // parameter.put("boxCode", new Object());
        // parameter.put("receiveSiteCode", new Object());
        // parameter.put("createSiteCode", new Object());
        inspectionECDao.exceptionCountByBox(parameter);
    }
	
	@Test
    public void testUpdateStatus() {
        InspectionEC parameter = new InspectionEC();
        parameter.setStatus(46);
        parameter.setUpdateUser("Jone");
        parameter.setUpdateUserCode(189);
        parameter.setUpdateTime(new Date());
        parameter.setCreateSiteCode(183);
        parameter.setPackageBarcode("Jax");
        parameter.setWaybillCode("Mary");
        parameter.setBoxCode("Joe");
        parameter.setReceiveSiteCode(206);
        parameter.setInspectionECType(31);
        inspectionECDao.updateStatus(parameter);
    }
	
	@Test
    public void testCheckDispose() {
        InspectionEC parameter = new InspectionEC();
        parameter.setBoxCode("Jone");
        parameter.setReceiveSiteCode(887);
        parameter.setCreateSiteCode(251);
        parameter.setPackageBarcode("Joe");
        inspectionECDao.checkDispose(parameter);
    }
	
	@Test
    public void testQueryListByBox() {
        InspectionEC parameter = new InspectionEC();
        parameter.setBoxCode("Mary");
        parameter.setInspectionType(455);
        parameter.setCreateSiteCode(22);
        parameter.setReceiveSiteCode(552);
        inspectionECDao.queryListByBox(parameter);
    }
	
	@Test
    public void testSelectSelective() {
        InspectionEC parameter = new InspectionEC();
        parameter.setCheckId((long)8754);
        parameter.setWaybillCode("Jax");
        parameter.setBoxCode("Stone");
        parameter.setPackageBarcode("Jone");
        parameter.setInspectionECType(532);
        parameter.setInspectionType(446);
        parameter.setStatus(736);
        parameter.setExceptionType("Jim");
        parameter.setCreateUser("James");
        parameter.setCreateUserCode(50);
        parameter.setCreateTime(new Date());
        parameter.setCreateSiteCode(391);
        parameter.setReceiveSiteCode(899);
        parameter.setUpdateUser("Jone");
        parameter.setUpdateUserCode(125);
        parameter.setUpdateTime(new Date());
        parameter.setYn(474);
        parameter.setLimitNo(189);
        inspectionECDao.selectSelective(parameter);
    }
	
	@Test
    public void testUpdateYnByWaybillCode() {
        InspectionEC parameter = new InspectionEC();
        parameter.setUpdateUser("James");
        parameter.setUpdateUserCode(594);
        parameter.setUpdateTime(new Date());
        parameter.setBoxCode("Mary");
        parameter.setPackageBarcode("Stone");
        parameter.setWaybillCode("Jax");
        parameter.setCreateSiteCode(968);
        parameter.setReceiveSiteCode(963);
        inspectionECDao.updateYnByWaybillCode(parameter);
    }
	
	@Test
    public void testUpdateInspectionECType() {
        InspectionEC parameter = new InspectionEC();
        parameter.setInspectionECType(327);
        parameter.setPackageBarcode("Mary");
        parameter.setCreateSiteCode(664);
        parameter.setReceiveSiteCode(612);
        parameter.setStatus(684);
        inspectionECDao.updateInspectionECType(parameter);
    }
	
	@Test
    public void testQueryLast() {
        inspectionECDao.queryLast();
    }
	
	@Test
    public void testBoxUnInspection() {
        Map parameter = new HashMap();
        // parameter.put("boxCode", new Object());
        // parameter.put("receiveSiteCode", new Object());
        // parameter.put("createSiteCode", new Object());
        inspectionECDao.boxUnInspection(parameter);
    }
}
