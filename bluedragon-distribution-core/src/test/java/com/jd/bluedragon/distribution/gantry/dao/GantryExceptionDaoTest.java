package com.jd.bluedragon.distribution.gantry.dao;

import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import com.jd.bluedragon.distribution.gantry.domain.GantryException;
import com.jd.bluedragon.distribution.gantry.domain.GantryDeviceConfig;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dudong on 2016/9/22.
 */
public class GantryExceptionDaoTest extends AbstractDaoIntegrationTest{

    @Autowired
    private GantryExceptionDao gantryExceptionDao;

    @Test
    public void testAddGantryException() {
        for (long i =0; i < 20; i++) {
            GantryException gantryException = new GantryException();
            gantryException.setMachineId("mId" + i);
            gantryException.setPackageCode("pcode" + i);
            gantryException.setChuteCode("cCode" + i);
            gantryException.setBarCode("bar"+i);
            gantryException.setWaybillCode("123");
            gantryException.setCreateSiteCode(11L);
            gantryException.setCreateSiteName("123");
            gantryException.setVolume(1.0);
            gantryException.setType(1);
            gantryException.setOperateTime(new Date());
            gantryException.setCreateTime(new Date());
            gantryException.setUpdateTime(new Date());
            Assert.assertEquals(1, gantryExceptionDao.addGantryException(gantryException));
        }

    }

//    @Test
//    public void testQueryGantryExceptionCount() {
//
//        HashMap<String, Object> param = new HashMap();
////        param.put("machineId", new Long(0));
//        param.put("endTime", new Date());
//        Assert.assertEquals(new Integer(21), gantryExceptionDao.getGantryExceptionCount(param));
//    }
//    @Test
//    public void testQueryGantryExceptionPage() {
//
//        HashMap<String, Object> param = new HashMap();
//
//        Assert.assertEquals(1, gantryExceptionDao.getGantryExceptionPage(param));
//    }
//    @Test
//    public void testAddGantryConfig() {
//        GantryDeviceConfig gantryDeviceConfig = new GantryDeviceConfig();
//        gantryDeviceConfig.setBusinessType(1);
//        gantryDeviceConfig.setBusinessTypeRemark("验货");
//        gantryDeviceConfig.setCreateSiteCode(697);
//        gantryDeviceConfig.setCreateSiteName("南京配送中心");
//        gantryDeviceConfig.setDbTime(new Date());
//        gantryDeviceConfig.setEndTime(new Date());
//        gantryDeviceConfig.setGantrySerialNumber("11111");
//        gantryDeviceConfig.setLockStatus(0);
//        gantryDeviceConfig.setLockUserErp("james5");
//        gantryDeviceConfig.setLockUserName("james");
//        gantryDeviceConfig.setMachineId(11);
//        gantryDeviceConfig.setOperateUserErp("james5");
//        gantryDeviceConfig.setOperateUserName("james");
//        gantryDeviceConfig.setUpdateUserErp("james5");
//        gantryDeviceConfig.setUpdateUserName("james");
//        gantryDeviceConfig.setSendCode("201-134-123431243243");
//        gantryDeviceConfig.setStartTime(new Date());
//        Assert.assertEquals(new Integer(1),gantryDeviceConfigDao.add(GantryDeviceConfigDao.namespace,gantryDeviceConfig));
//
//    }
//
//
//    @Test
//    public void testFindGantryDeviceConfigByOperateTime () {
//        Assert.assertEquals(1, gantryDeviceConfigDao.findGantryDeviceConfigByOperateTime(11,new Date()).size());
//    }
//
//
//    @Test
//    public void testAddUseJavaTime () {
//        GantryDeviceConfig gantryDeviceConfig = new GantryDeviceConfig();
//        gantryDeviceConfig.setBusinessType(2);
//        gantryDeviceConfig.setBusinessTypeRemark("验货");
//        gantryDeviceConfig.setCreateSiteCode(697);
//        gantryDeviceConfig.setCreateSiteName("南京配送中心");
//        gantryDeviceConfig.setDbTime(new Date());
//        gantryDeviceConfig.setEndTime(new Date());
//        gantryDeviceConfig.setGantrySerialNumber("22222");
//        gantryDeviceConfig.setLockStatus(0);
//        gantryDeviceConfig.setLockUserErp("james8");
//        gantryDeviceConfig.setLockUserName("james");
//        gantryDeviceConfig.setMachineId(11);
//        gantryDeviceConfig.setOperateUserErp("james8");
//        gantryDeviceConfig.setOperateUserName("james");
//        gantryDeviceConfig.setUpdateUserErp("james5");
//        gantryDeviceConfig.setUpdateUserName("james");
//        gantryDeviceConfig.setSendCode("33334-4444-123431243243");
//        gantryDeviceConfig.setStartTime(new Date());
//        Assert.assertEquals(new Integer(1),gantryDeviceConfigDao.add(GantryDeviceConfigDao.namespace,gantryDeviceConfig));
//    }
//
//    @Test
//    public void testFindMaxStartTimeGantryDeviceConfigByMachineId() {
//        Assert.assertNotNull(gantryDeviceConfigDao.findMaxStartTimeGantryDeviceConfigByMachineId(11));
//    }
//
//
//    @Test
//    public void testFindAllGantryDeviceCurrentConfig() {
//        Assert.assertEquals(2,gantryDeviceConfigDao.findAllGantryDeviceCurrentConfig(697).size());
//    }
//
//
//    @Test
//    public void testCheckSendCode() {
//        Assert.assertNotNull(gantryDeviceConfigDao.checkSendCode("33334-4444-123431243243"));
//    }
//
//
//    @Test
//    public void testUpdateLockStatus() {
//        GantryDeviceConfig gantryDeviceConfig = new GantryDeviceConfig();
//        gantryDeviceConfig.setId(77L);
//        gantryDeviceConfig.setLockUserName("zhangsan");
//        gantryDeviceConfig.setLockUserErp("bjxings");
//        gantryDeviceConfig.setLockStatus(1);
//        Assert.assertEquals(1,gantryDeviceConfigDao.updateLockStatus(gantryDeviceConfig));
//    }
//
//    @Test
//    public void testUpdateBusinessType(){
//        GantryDeviceConfig gantryDeviceConfig = new GantryDeviceConfig();
//        gantryDeviceConfig.setId(78L);
//        gantryDeviceConfig.setBusinessType(5);
//        Assert.assertEquals(1,gantryDeviceConfigDao.updateBusinessType(gantryDeviceConfig));
//    }
//
//    @Test
//    public void testUpdateGantryDeviceById() {
//        GantryDevice gantryDevice = new GantryDevice();
//        gantryDevice.setMachineId(635L);
//        gantryDevice.setSerialNumber("23423432432");
//        gantryDevice.setSupplier("xinshijing");
//        Assert.assertEquals(1,gantryDeviceDao.updateGantryById(gantryDevice));
//    }
//
//
//    @Test
//    public void testDelGantryDevice() {
//        Map<String,Object> param = new HashMap<String, Object>();
//        param.put("machineId",new Integer(635));
//        Assert.assertEquals(1,gantryDeviceDao.delGantry(param));
//    }
}
