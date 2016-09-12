package com.jd.bluedragon.distribution.inspection.dao;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.Date;
import java.util.List;
import com.jd.bluedragon.distribution.inspection.domain.InspectionEC;

public class InspectionECDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private InspectionECDao inspectionECDao;
    @Test
    public void testBoxUnInspection() {
        inspectionECDao.boxUnInspection(656, 601, "Jone");
    }
	
	@Test
    public void testGet() {
        inspectionECDao.get(InspectionECDao.namespace , (long)80);
    }
	
	@Test
    public void testAdd() {
        InspectionEC parameter = new InspectionEC();
        parameter.setCheckId((long)2968);
        parameter.setWaybillCode("Mary");
        parameter.setBoxCode("Jone");
        parameter.setPackageBarcode("Jone");
        parameter.setInspectionECType(1);
        parameter.setStatus(1);
        parameter.setInspectionType(1);
        parameter.setExceptionType("Mary");
        parameter.setCreateUser("James");
        parameter.setCreateUserCode(878);
        parameter.setCreateTime(new Date());
        parameter.setCreateSiteCode(656);
        parameter.setReceiveSiteCode(601);
        parameter.setUpdateUser("Mary");
        parameter.setUpdateUserCode(564);
        parameter.setUpdateTime(new Date());
        inspectionECDao.add(InspectionECDao.namespace ,parameter);
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
        parameter.setStatus(111);
        parameter.setUpdateUser("Jone1");
        parameter.setUpdateUserCode(189);
        parameter.setUpdateTime(new Date());
        parameter.setCreateSiteCode(656);
        parameter.setPackageBarcode("Jone");
        parameter.setWaybillCode("Mary");
        parameter.setBoxCode("Jone");
        parameter.setReceiveSiteCode(601);
        parameter.setInspectionECType(1);
        inspectionECDao.updateForSorting(parameter);
    }
	
	@Test
    public void testInspectionCount() {
        InspectionEC parameter = new InspectionEC();
        parameter.setBoxCode("Jone");
        parameter.setInspectionECType(1);
        parameter.setInspectionType(1);
        parameter.setYn(1);
        inspectionECDao.inspectionCount(parameter);
    }
	
	
	@Test
    public void testQueryExceptionsCore() {
        inspectionECDao.queryExceptionsCore(656, 601, "Jone");
    }
	
	@Test
    public void testUpdateOne() {
		InspectionEC parameter = new InspectionEC();
        parameter.setStatus(111);
        parameter.setUpdateUser("Jone1");
        parameter.setUpdateUserCode(189);
        parameter.setUpdateTime(new Date());
        parameter.setCreateSiteCode(656);
        parameter.setPackageBarcode("Jone");
        parameter.setWaybillCode("Mary");
        parameter.setBoxCode("Jone");
        parameter.setReceiveSiteCode(601);
        parameter.setInspectionECType(1);
        parameter.setInspectionType(1);
        inspectionECDao.updateOne(parameter);
    }
	
	@Test
    public void testExceptionCountByBox() {
        inspectionECDao.exceptionCountByBox(656, 601, "Jone");
    }
	
	@Test
    public void testUpdateStatus() {
        InspectionEC parameter = new InspectionEC();
        parameter.setStatus(46);
        parameter.setUpdateUser("Jone1");
        parameter.setUpdateUserCode(189);
        parameter.setUpdateTime(new Date());
        parameter.setCreateSiteCode(656);
        parameter.setPackageBarcode("Jone");
        parameter.setWaybillCode("Mary");
        parameter.setBoxCode("Jone");
        parameter.setReceiveSiteCode(601);
        parameter.setInspectionECType(1);
        inspectionECDao.updateStatus(parameter);
    }
	
	@Test
    public void testCheckDispose() {
        InspectionEC parameter = new InspectionEC();
        parameter.setBoxCode("Jone");
        parameter.setReceiveSiteCode(601);
        parameter.setCreateSiteCode(656);
        parameter.setPackageBarcode("Jone");
        inspectionECDao.checkDispose(parameter);
    }
	
	@Test
    public void testSelectSelective() {
        InspectionEC parameter = new InspectionEC();
        parameter.setStatus(1);
        parameter.setStartNo(1);
        parameter.setLimitNo(2);
        inspectionECDao.selectSelective(parameter);
    }
	
	@Test
    public void testUpdateYnByWaybillCode() {
		InspectionEC parameter = new InspectionEC();
        parameter.setStatus(1);
        parameter.setUpdateUser("Jone1");
        parameter.setUpdateUserCode(189);
        parameter.setUpdateTime(new Date());
        parameter.setCreateSiteCode(656);
        parameter.setPackageBarcode("Jone");
        parameter.setWaybillCode("Mary");
        parameter.setBoxCode("Jone");
        parameter.setReceiveSiteCode(601);
        parameter.setInspectionECType(1);
        inspectionECDao.updateYnByWaybillCode(parameter);
    }
	
	@Test
    public void testUpdateInspectionECType() {
		InspectionEC parameter = new InspectionEC();
        parameter.setStatus(111);
        parameter.setUpdateUser("Jone1");
        parameter.setUpdateUserCode(189);
        parameter.setUpdateTime(new Date());
        parameter.setCreateSiteCode(656);
        parameter.setPackageBarcode("Jone");
        parameter.setWaybillCode("Mary");
        parameter.setBoxCode("Jone");
        parameter.setReceiveSiteCode(601);
        parameter.setInspectionECType(1);
        parameter.setInspectionType(1);
        try {
			inspectionECDao.updateInspectionECType(parameter);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	

}
