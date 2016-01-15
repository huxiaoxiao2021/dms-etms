package com.jd.bluedragon.distribution.departure.dao;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import com.jd.bluedragon.distribution.departure.domain.DepartureCar;
import com.jd.bluedragon.distribution.api.request.DeparturePrintRequest;

public class DepartureCarDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private DepartureCarDao departureCarDao;
	
	
	@Test
    public void testQueryDepartureInfoBySendCode() {
        String item = new Object();
        departureCarDao.queryDepartureInfoBySendCode(item);
    }
	
	@Test
    public void testInsert() {
        DepartureCar parameter = new DepartureCar();
        parameter.setShieldsCarId((long)8678);
        parameter.setCarCode("Jone");
        parameter.setShieldsCarCode("Jone");
        parameter.setSendUser("James");
        parameter.setSendUserCode(524);
        parameter.setCreateSiteCode(781);
        parameter.setCreateTime(new Date());
        parameter.setCreateUser("James");
        parameter.setCreateUserCode(977);
        parameter.setUpdateTime(new Date());
        parameter.setYn(282);
        parameter.setWeight(0.6503714941950163);
        parameter.setVolume(0.46586645818402495);
        parameter.setSendUserType(443);
        parameter.setFingerprint("Mary");
        parameter.setOldCarCode("Jax");
        parameter.setDepartType(285);
        parameter.setRunNumber(38);
        parameter.setReceiveSiteCodes("Joe");
        parameter.setCapacityCode("Jax");
        departureCarDao.insert(parameter);
    }
	
	@Test
    public void testGetSeqNextVal() {
        departureCarDao.getSeqNextVal();
    }
	
	@Test
    public void testUpdatePrintTime() {
        Long departureCarId = new Object();
        departureCarDao.updatePrintTime(departureCarId);
    }
	
	@Test
    public void testQueryArteryBillingInfo() {
        Long carCode = (long)702;
        departureCarDao.queryArteryBillingInfo(carCode);
    }
	
	@Test
    public void testQueryDeliveryInfoByOrderCode() {
        String orderCode = "Jim";
        departureCarDao.queryDeliveryInfoByOrderCode(orderCode);
    }
	
	@Test
    public void testGetDepartureCarObj() {
        Long departureCarId = (long)2098;
        departureCarDao.getDepartureCarObj(departureCarId);
    }
	
	@Test
    public void testFindDepartureCarByFingerprint() {
        Map parameter = new HashMap();
        // parameter.put("fingerprint", new Object());
        departureCarDao.findDepartureCarByFingerprint(parameter);
    }
	
	@Test
    public void testFindDepartureList() {
        DeparturePrintRequest parameter = new DeparturePrintRequest();
        parameter.setStartTime("Mary");
        parameter.setEndTime("Jone");
        parameter.setSendUser("Jim");
        parameter.setCarCode("Mary");
        parameter.setCreate_code("James");
        parameter.setThirdWaybillCode("James");
        departureCarDao.findDepartureList(parameter);
    }
	
	@Test
    public void testQueryArteryBillingInfoByBoxCode() {
        String boxCode = "Jone";
        departureCarDao.queryArteryBillingInfoByBoxCode(boxCode);
    }
}
