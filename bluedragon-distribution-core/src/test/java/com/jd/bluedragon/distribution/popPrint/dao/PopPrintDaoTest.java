package com.jd.bluedragon.distribution.popPrint.dao;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.popPrint.domain.PopPrint;
import java.util.Map;

public class PopPrintDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private PopPrintDao popPrintDao;
	
	
	@Test
    public void testFindAllByWaybillCode() {
        String waybillCode = "Joe";
        popPrintDao.findAllByWaybillCode(waybillCode);
    }
	
	@Test
    public void testFindTotalCount() {
        Map parameter = new HashMap();
        // parameter.put("waybillCode", new Object());
        // parameter.put("createSiteCode", new Object());
        // parameter.put("startTime", new Object());
        // parameter.put("endTime", new Object());
        // parameter.put("startTime", new Object());
        // parameter.put("endTime", new Object());
        popPrintDao.findTotalCount(parameter);
    }
	
	@Test
    public void testFindByWaybillCode() {
        String waybillCode = "James";
        popPrintDao.findByWaybillCode(waybillCode);
    }
	
	@Test
    public void testUpdateByWaybillOrPack() {
        PopPrint parameter = new PopPrint();
        parameter.setPrintPackCode(351);
        parameter.setPrintPackUser("Jim");
        parameter.setPrintPackTime(new Date());
        parameter.setPrintInvoiceCode(997);
        parameter.setPrintInvoiceUser("Jax");
        parameter.setPrintInvoiceTime(new Date());
        parameter.setCreateSiteCode(852);
        parameter.setCreateUserCode(176);
        parameter.setCreateUser("Jim");
        parameter.setPopSupId(197);
        parameter.setPopSupName("Jone");
        parameter.setQuantity(118);
        parameter.setCrossCode("Jim");
        parameter.setWaybillType(381);
        parameter.setPopReceiveType(44);
        parameter.setThirdWaybillCode("Jim");
        parameter.setQueueNo("James");
        parameter.setBoxCode("Joe");
        parameter.setDriverCode("Jax");
        parameter.setDriverName("Jax");
        parameter.setBusiId(238);
        parameter.setBusiName("Jax");
        parameter.setWaybillCode("Jim");
        parameter.setPackageBarcode("Stone");
        parameter.setOperateType(204);
        popPrintDao.updateByWaybillOrPack(parameter);
    }
	
	@Test
    public void testFindListNoReceive() {
        Map parameter = new HashMap();
        // parameter.put("waybillCode", new Object());
        // parameter.put("createSiteCode", new Object());
        // parameter.put("startTime", new Object());
        // parameter.put("endTime", new Object());
        // parameter.put("startTime", new Object());
        // parameter.put("endTime", new Object());
        // parameter.put("endIndex", new Object());
        // parameter.put("startIndex", new Object());
        popPrintDao.findListNoReceive(parameter);
    }
	
	@Test
    public void testFindSitePrintDetail() {
        Map parameter = new HashMap();
        // parameter.put("createSiteCode", new Object());
        // parameter.put("startTime", new Object());
        // parameter.put("endTime", new Object());
        // parameter.put("popReceiveType", new Object());
        // parameter.put("waybillType", new Object());
        // parameter.put("queueNo", new Object());
        // parameter.put("endIndex", new Object());
        // parameter.put("startIndex", new Object());
        popPrintDao.findSitePrintDetail(parameter);
    }
	
	@Test
    public void testFindLimitListNoReceive() {
        Map parameter = new HashMap();
        // parameter.put("limitMin", new Object());
        // parameter.put("limitHour", new Object());
        // parameter.put("fetchNum", new Object());
        popPrintDao.findLimitListNoReceive(parameter);
    }
	
	@Test
    public void testFindSitePrintDetailCount() {
        Map parameter = new HashMap();
        // parameter.put("createSiteCode", new Object());
        // parameter.put("startTime", new Object());
        // parameter.put("endTime", new Object());
        // parameter.put("popReceiveType", new Object());
        // parameter.put("waybillType", new Object());
        // parameter.put("queueNo", new Object());
        popPrintDao.findSitePrintDetailCount(parameter);
    }
	
	@Test
    public void testAdd() {
        PopPrint parameter = new PopPrint();
        parameter.setWaybillCode("Jax");
        parameter.setCreateSiteCode(981);
        parameter.setPrintPackCode(64);
        parameter.setPrintPackTime(new Date());
        parameter.setPrintInvoiceCode(791);
        parameter.setPrintInvoiceTime(new Date());
        parameter.setPrintPackUser("Jone");
        parameter.setPrintInvoiceUser("James");
        parameter.setPopSupId(111);
        parameter.setPopSupName("Jax");
        parameter.setQuantity(228);
        parameter.setCrossCode("Joe");
        parameter.setWaybillType(505);
        parameter.setPopReceiveType(501);
        parameter.setPrintCount(331);
        parameter.setThirdWaybillCode("Joe");
        parameter.setQueueNo("Jax");
        parameter.setPackageBarcode("Stone");
        parameter.setOperateType(191);
        parameter.setCreateUserCode(293);
        parameter.setCreateUser("Stone");
        parameter.setBoxCode("Jim");
        parameter.setDriverCode("Stone");
        parameter.setDriverName("Jone");
        parameter.setBusiId(151);
        parameter.setBusiName("Jim");
        popPrintDao.add(parameter);
    }
	
	@Test
    public void testUpdateByWaybillCode() {
        PopPrint parameter = new PopPrint();
        parameter.setPrintPackCode(644);
        parameter.setPrintPackUser("Jone");
        parameter.setPrintPackTime(new Date());
        parameter.setPrintCount(324);
        parameter.setPrintInvoiceCode(428);
        parameter.setPrintInvoiceUser("Jone");
        parameter.setPrintInvoiceTime(new Date());
        parameter.setCreateSiteCode(417);
        parameter.setPopSupId(682);
        parameter.setPopSupName("Stone");
        parameter.setQuantity(160);
        parameter.setCrossCode("Jim");
        parameter.setWaybillType(0);
        parameter.setPopReceiveType(828);
        parameter.setThirdWaybillCode("Mary");
        parameter.setQueueNo("Joe");
        parameter.setWaybillCode("Jim");
        popPrintDao.updateByWaybillCode(parameter);
    }
}
