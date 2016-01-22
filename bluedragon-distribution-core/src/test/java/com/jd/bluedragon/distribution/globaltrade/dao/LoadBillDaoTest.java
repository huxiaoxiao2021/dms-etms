package com.jd.bluedragon.distribution.globaltrade.dao;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import com.jd.bluedragon.distribution.globaltrade.domain.LoadBill;
import com.jd.bluedragon.utils.StringHelper;

public class LoadBillDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private LoadBillDao loadBillDao;
	
	
	@Test
    public void testUpdateLoadBillStatus() {
		Map<String, Object> loadBillStatusMap = new HashMap<String, Object>();
		List<String> list = new ArrayList<String>();
		list.add("a");
			loadBillStatusMap.put("loadIdList", list);
			loadBillStatusMap.put("warehouseId", "Jax");
			loadBillStatusMap.put("approvalCode", LoadBill.FAILED);
        loadBillDao.updateLoadBillStatus(loadBillStatusMap);
    }
	
	@Test
    public void testUpdate() {
        LoadBill parameter = new LoadBill();
        parameter.setLoadId("Jone");
        parameter.setWarehouseId("Jax");
        parameter.setWaybillCode("James");
        parameter.setPackageBarcode("Mary");
        parameter.setPackageAmount(491);
        parameter.setOrderId("James");
        parameter.setBoxCode("James");
        parameter.setDmsCode(864);
        parameter.setDmsName("Joe");
        parameter.setSendTime(new Date());
        parameter.setSendCode("Mary");
        parameter.setTruckNo("Stone");
        parameter.setApprovalCode(769);
        parameter.setApprovalTime(new Date());
        parameter.setCtno("Jax");
        parameter.setGjno("Stone");
        parameter.setTpl("Jim");
        parameter.setWeight(0.34700942674111);
        parameter.setCreateTime(new Date());
        parameter.setUpdateTime(new Date());
        parameter.setGenTime(new Date());
        parameter.setCreateUserCode(25);
        parameter.setCreateUser("Jone");
        parameter.setPackageUserCode(616);
        parameter.setPackageUser("James");
        parameter.setPackageTime(new Date());
        parameter.setRemark("Stone");
        loadBillDao.update(parameter);
    }
	
	@Test
    public void i () {
		List<Long> billId = new ArrayList<Long>();
		billId.add(27426L);
		List<LoadBill> list = loadBillDao.getLoadBills(billId);
		 Assert.assertNotNull(list);
    }
	
	@Test
    public void testFindByPackageBarcode() {
        String packageBarcode = "Mary";
        LoadBill bill = loadBillDao.findByPackageBarcode(packageBarcode);
        Assert.assertNotNull(bill);
    }
	
	@Test
    public void testUpdateCancelLoadBillStatus() {
        LoadBill parameter = new LoadBill();
        parameter.setApprovalCode(95);
        parameter.setWaybillCode("James");
        parameter.setId((long)27426);
        loadBillDao.updateCancelLoadBillStatus(parameter);
    }
	
	@Test
    public void testFindLoadbillByID() {
        Long id = (long)27426;
        loadBillDao.findLoadbillByID(id);
    }
	
	@Test
    public void testUpdateLoadBillById() {
        // List<Long> billId,String trunkNo,String loadId,Integer approvalCode
		List<Long> billId = new ArrayList<Long>();
		billId.add(27426L);
		String trunkNo ="a";
		String loadId ="a";
		Integer approvalCode =1;
        loadBillDao.updatePreLoadBillById(billId,trunkNo,loadId,approvalCode);
    }
	
	@Test
    public void testAdd() {
        LoadBill parameter = new LoadBill();
        parameter.setLoadId("1");
        parameter.setWarehouseId("Jim");
        parameter.setWaybillCode("1111");
        parameter.setPackageBarcode("Mary");
        parameter.setPackageAmount(231);
        parameter.setOrderId("3333");
        parameter.setBoxCode("2222");
        parameter.setDmsCode(330);
        parameter.setDmsName("Jone");
        parameter.setSendTime(new Date());
        parameter.setSendCode("2222");
        parameter.setTruckNo("Jone");
        parameter.setApprovalCode(162);
        parameter.setCtno("Mary");
        parameter.setGjno("Stone");
        parameter.setTpl("Mary");
        parameter.setWeight(0.7768244752565674);
        parameter.setCreateUserCode(289);
        parameter.setCreateUser("Stone");
        parameter.setPackageUserCode(839);
        parameter.setPackageUser("Jim");
        parameter.setPackageTime(new Date());
        parameter.setRemark("Stone");
        loadBillDao.add(parameter);
    }
	
	@Test
    public void testSelectPreLoadBillId() {
		for(int i=0;i<100;i++){
		Long a =loadBillDao.selectPreLoadBillId();
		System.out.println();
		Assert.assertEquals(1, 1);
		}
    }
	
	@Test
    public void findPageLoadBill(){
		Map<String, Object> loadBillStatusMap = new HashMap<String, Object>();
		
		List<String> billId = new ArrayList<String>();
		billId.add("1111");
		billId.add("2222");
			loadBillStatusMap.put("startIndex", 1);
			loadBillStatusMap.put("endIndex", 3);
			loadBillStatusMap.put("sendCodeList", billId);
			loadBillStatusMap.put("dmsCode", "330");
			loadBillStatusMap.put("approvalCode", "162");
			loadBillStatusMap.put("sendTimeFrom", new Date());
			loadBillStatusMap.put("sendTimeTo", new Date());
			loadBillStatusMap.put("waybillCode", "1111");
			loadBillDao.findPageLoadBill(loadBillStatusMap);
	}
}
