package com.jd.bluedragon.distribution.globaltrade.dao;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.List;
import com.jd.bluedragon.distribution.globaltrade.domain.LoadBill;

public class LoadBillDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private LoadBillDao loadBillDao;
	
	
	@Test
    public void testUpdateLoadBillStatus() {
        Map parameter = new HashMap();
        // parameter.put("approvalCode", new Object());
        // parameter.put("warehouseId", new Object());
        // parameter.put("item", new Object());
        // parameter.put("item", new Object());
        // parameter.put("item", new Object());
        loadBillDao.updateLoadBillStatus(parameter);
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
        parameter.setPackageBarcode("Jax");
        loadBillDao.update(parameter);
    }
	
	@Test
    public void testSelectLoadBillById() {
        List parameter = new ArrayList();
        // parameter.getItem(new Object());
        loadBillDao.selectLoadBillById(parameter);
    }
	
	@Test
    public void testFindByPackageBarcode() {
        String packageBarcode = "Jone";
        loadBillDao.findByPackageBarcode(packageBarcode);
    }
	
	@Test
    public void testUpdateCancelLoadBillStatus() {
        LoadBill parameter = new LoadBill();
        parameter.setApprovalCode(95);
        parameter.setWaybillCode("Joe");
        parameter.setId((long)5028);
        loadBillDao.updateCancelLoadBillStatus(parameter);
    }
	
	@Test
    public void testFindLoadbillByID() {
        Long id = (long)5224;
        loadBillDao.findLoadbillByID(id);
    }
	
	@Test
    public void testUpdateLoadBillById() {
        Map parameter = new HashMap();
        // parameter.put("loadId", new Object());
        // parameter.put("trunkNo", new Object());
        // parameter.put("approvalCode", new Object());
        // parameter.put("item", new Object());
        loadBillDao.updateLoadBillById(parameter);
    }
	
	@Test
    public void testAddBatch() {
        List parameter = new ArrayList();
        //set property for item.loadId
        //set property for item.warehouseId
        //set property for item.waybillCode
        //set property for item.packageBarcode
        //set property for item.packageAmount
        //set property for item.orderId
        //set property for item.boxCode
        //set property for item.dmsCode
        //set property for item.dmsName
        //set property for item.sendTime
        //set property for item.sendCode
        //set property for item.truckNo
        //set property for item.approvalCode
        //set property for item.approvalTime
        //set property for item.ctno
        //set property for item.gjno
        //set property for item.tpl
        //set property for item.weight
        //set property for item.createUserCode
        //set property for item.createUser
        //set property for item.packageUserCode
        //set property for item.packageUser
        //set property for item.packageTime
        //set property for item.remark
        loadBillDao.addBatch(parameter);
    }
	
	@Test
    public void testAdd() {
        LoadBill parameter = new LoadBill();
        parameter.setLoadId("Joe");
        parameter.setWarehouseId("Jim");
        parameter.setWaybillCode("James");
        parameter.setPackageBarcode("Mary");
        parameter.setPackageAmount(231);
        parameter.setOrderId("Jone");
        parameter.setBoxCode("Mary");
        parameter.setDmsCode(330);
        parameter.setDmsName("Jone");
        parameter.setSendTime(new Date());
        parameter.setSendCode("Jone");
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
        loadBillDao.selectPreLoadBillId();
    }
}
