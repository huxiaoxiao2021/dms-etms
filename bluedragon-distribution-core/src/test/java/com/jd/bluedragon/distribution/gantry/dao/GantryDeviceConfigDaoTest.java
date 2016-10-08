package com.jd.bluedragon.distribution.gantry.dao;

import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import com.jd.bluedragon.distribution.gantry.domain.GantryDevice;
import com.jd.bluedragon.distribution.gantry.domain.GantryDeviceConfig;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dudong on 2016/9/22.
 */
public class GantryDeviceConfigDaoTest extends AbstractDaoIntegrationTest{

    @Autowired
    private GantryDeviceConfigDao gantryDeviceConfigDao;

    @Autowired
    private GantryDeviceDao gantryDeviceDao;

    @Test
    public void testAddGantryInfo() {
        GantryDevice gantryDevice = new GantryDevice();
        gantryDevice.setCreateTime(new Date());
        gantryDevice.setOperateName("james5");
        gantryDevice.setMachineId(11L);
        gantryDevice.setMark("hello world");
        gantryDevice.setModelNumber("99997777");
        gantryDevice.setOrgCode(645);
        gantryDevice.setOrgName("西安分公司");
        gantryDevice.setSiteCode(697);
        gantryDevice.setSiteName("南京配送中心");
        gantryDevice.setSerialNumber("11111");
        gantryDevice.setSupplier("xinshijiang");
        gantryDevice.setToken("ERHWQEJRJWERDFDFHEWREW");
        Assert.assertEquals(1, gantryDeviceDao.addGantry(gantryDevice));
    }

    @Test
    public void testAddGantryConfig() {
        GantryDeviceConfig gantryDeviceConfig = new GantryDeviceConfig();
        gantryDeviceConfig.setBusinessType(1);
        gantryDeviceConfig.setBusinessTypeRemark("验货");
        gantryDeviceConfig.setCreateSiteCode(697);
        gantryDeviceConfig.setCreateSiteName("南京配送中心");
        gantryDeviceConfig.setDbTime(new Date());
        gantryDeviceConfig.setEndTime(new Date());
        gantryDeviceConfig.setGantrySerialNumber("11111");
        gantryDeviceConfig.setLockStatus(0);
        gantryDeviceConfig.setLockUserErp("james5");
        gantryDeviceConfig.setLockUserName("james");
        gantryDeviceConfig.setMachineId(11);
        gantryDeviceConfig.setOperateUserErp("james5");
        gantryDeviceConfig.setOperateUserName("james");
        gantryDeviceConfig.setUpdateUserErp("james5");
        gantryDeviceConfig.setUpdateUserName("james");
        gantryDeviceConfig.setSendCode("201-134-123431243243");
        gantryDeviceConfig.setStartTime(new Date());
        Assert.assertEquals(new Integer(1),gantryDeviceConfigDao.add(GantryDeviceConfigDao.namespace,gantryDeviceConfig));

    }


    @Test
    public void testFindGantryDeviceConfigByOperateTime () {
        Assert.assertEquals(1, gantryDeviceConfigDao.findGantryDeviceConfigByOperateTime(11,new Date()).size());
    }


    @Test
    public void testAddUseJavaTime () {
        GantryDeviceConfig gantryDeviceConfig = new GantryDeviceConfig();
        gantryDeviceConfig.setBusinessType(2);
        gantryDeviceConfig.setBusinessTypeRemark("验货");
        gantryDeviceConfig.setCreateSiteCode(697);
        gantryDeviceConfig.setCreateSiteName("南京配送中心");
        gantryDeviceConfig.setDbTime(new Date());
        gantryDeviceConfig.setEndTime(new Date());
        gantryDeviceConfig.setGantrySerialNumber("22222");
        gantryDeviceConfig.setLockStatus(0);
        gantryDeviceConfig.setLockUserErp("james8");
        gantryDeviceConfig.setLockUserName("james");
        gantryDeviceConfig.setMachineId(11);
        gantryDeviceConfig.setOperateUserErp("james8");
        gantryDeviceConfig.setOperateUserName("james");
        gantryDeviceConfig.setUpdateUserErp("james5");
        gantryDeviceConfig.setUpdateUserName("james");
        gantryDeviceConfig.setSendCode("33334-4444-123431243243");
        gantryDeviceConfig.setStartTime(new Date());
        Assert.assertEquals(new Integer(1),gantryDeviceConfigDao.add(GantryDeviceConfigDao.namespace,gantryDeviceConfig));
    }

    @Test
    public void testFindMaxStartTimeGantryDeviceConfigByMachineId() {
        Assert.assertNotNull(gantryDeviceConfigDao.findMaxStartTimeGantryDeviceConfigByMachineId(11));
    }


    @Test
    public void testFindAllGantryDeviceCurrentConfig() {
        Assert.assertEquals(2,gantryDeviceConfigDao.findAllGantryDeviceCurrentConfig(697).size());
    }


    @Test
    public void testCheckSendCode() {
        Assert.assertNotNull(gantryDeviceConfigDao.checkSendCode("33334-4444-123431243243"));
    }


    @Test
    public void testUpdateLockStatus() {
        GantryDeviceConfig gantryDeviceConfig = new GantryDeviceConfig();
        gantryDeviceConfig.setId(77L);
        gantryDeviceConfig.setLockUserName("zhangsan");
        gantryDeviceConfig.setLockUserErp("bjxings");
        gantryDeviceConfig.setLockStatus(1);
        Assert.assertEquals(1,gantryDeviceConfigDao.updateLockStatus(gantryDeviceConfig));
    }

    @Test
    public void testUpdateBusinessType(){
        GantryDeviceConfig gantryDeviceConfig = new GantryDeviceConfig();
        gantryDeviceConfig.setId(78L);
        gantryDeviceConfig.setBusinessType(5);
        Assert.assertEquals(1,gantryDeviceConfigDao.updateBusinessType(gantryDeviceConfig));
    }

    @Test
    public void testUpdateGantryDeviceById() {
        GantryDevice gantryDevice = new GantryDevice();
        gantryDevice.setMachineId(635L);
        gantryDevice.setSerialNumber("23423432432");
        gantryDevice.setSupplier("xinshijing");
        Assert.assertEquals(1,gantryDeviceDao.updateGantryById(gantryDevice));
    }


    @Test
    public void testDelGantryDevice() {
        Map<String,Object> param = new HashMap<String, Object>();
        param.put("machineId",new Integer(635));
        Assert.assertEquals(1,gantryDeviceDao.delGantry(param));
    }
}
