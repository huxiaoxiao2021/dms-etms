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
import com.jd.bluedragon.distribution.api.response.DeparturePrintResponse;

public class DepartureCarDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private DepartureCarDao departureCarDao;
	
	
	@Test
    public void testQueryDepartureInfoBySendCode() {
		List<String> sendCodes = new ArrayList<String>();
		sendCodes.add("1111");
		sendCodes.add("2222");
		List<DeparturePrintResponse> list = departureCarDao.queryDepartureInfoBySendCode(sendCodes);
		Assert.assertNotNull(list);
    }
	
	//@Test
    public void testInsert() {
        DepartureCar parameter = new DepartureCar();
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
        int  i = departureCarDao.insert(parameter);
        System.out.println(parameter.getShieldsCarId());
        Assert.assertEquals(1, i);
    }
	
	//@Test
    public void testUpdatePrintTime() {
        Long departureCarId = 1L;
        departureCarDao.updatePrintTime(departureCarId);
    }
	
	@Test
    public void testQueryArteryBillingInfo() {
        Long carCode = (long)234;
        departureCarDao.queryArteryBillingInfo(carCode);
    }
	
	//@Test
    public void testQueryDeliveryInfoByOrderCode() {
        String orderCode = "3333";
        List<DeparturePrintResponse> list = departureCarDao.queryDeliveryInfoByOrderCode(orderCode);
        Assert.assertNotNull(list);
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
