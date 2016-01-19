package com.jd.bluedragon.distribution.departure.dao;

import org.junit.Assert;
import org.junit.Test;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.jd.bluedragon.distribution.departure.domain.DepartureCar;
import com.jd.bluedragon.distribution.api.request.DeparturePrintRequest;

public class DepartureCarDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private DepartureCarDao departureCarDao;
	
	
	//@Test
    public void testQueryDepartureInfoBySendCode() {
		List<String> sendCodes = new ArrayList<String>();
		sendCodes.add("123456789");
		sendCodes.add("1234567891");
        departureCarDao.queryDepartureInfoBySendCode(sendCodes);
    }
	
	//@Test
    public void testInsert() {
        DepartureCar parameter = new DepartureCar();
        parameter.setShieldsCarId((long)2);
        parameter.setCarCode("Jone");
        parameter.setShieldsCarCode("Jone");
        parameter.setSendUser("James");
        parameter.setSendUserCode(524);
        parameter.setCreateSiteCode(781);
        parameter.setCreateTime(new Date());
        parameter.setCreateUser("James");
        parameter.setCreateUserCode(977);
        parameter.setUpdateTime(new Date());
        parameter.setYn(1);
        parameter.setWeight(0.6503714941950163);
        parameter.setVolume(0.46586645818402495);
        parameter.setSendUserType(1);
        parameter.setFingerprint("Mary");
        parameter.setOldCarCode("Jax");
        parameter.setDepartType(1);
        parameter.setRunNumber(38);
        parameter.setReceiveSiteCodes("Joe");
        parameter.setCapacityCode("Jax");
        Assert.assertEquals(1, departureCarDao.insert(parameter));
    }
	
	//@Test
    public void testGetSeqNextVal() {
        departureCarDao.getSeqNextVal();
    }
	
	//@Test
    public void testUpdatePrintTime() {
        Long departureCarId = 1L;
        departureCarDao.updatePrintTime(departureCarId);
    }
	
	//@Test
    public void testQueryArteryBillingInfo() {
        Long carCode = (long)1;
        departureCarDao.queryArteryBillingInfo(carCode);
    }
	
	//@Test
    public void testQueryDeliveryInfoByOrderCode() {
        String orderCode = "123456789";
        departureCarDao.queryDeliveryInfoByOrderCode(orderCode);
    }
	
	//@Test
    public void testGetDepartureCarObj() {
        Long departureCarId = (long)1;
        departureCarDao.getDepartureCarObj(departureCarId);
    }
	
	//@Test
    public void testFindDepartureCarByFingerprint() {
		DepartureCar departureCar = new DepartureCar();
		departureCar.setFingerprint("123");
        departureCarDao.findDepartureCarByFingerprint(departureCar);
    }
	
	//@Test
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
	
	//@Test
    public void testQueryArteryBillingInfoByBoxCode() {
        String boxCode = "1111";
        departureCarDao.queryArteryBillingInfoByBoxCode(boxCode);
    }
}
