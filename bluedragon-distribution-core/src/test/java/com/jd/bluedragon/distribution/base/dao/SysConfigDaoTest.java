package com.jd.bluedragon.distribution.base.dao;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.base.domain.SysConfig;

public class SysConfigDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private SysConfigDao sysConfigDao;
	
	
	@Test
    public void testDel() {
        Long config_id = (long)1876;
        sysConfigDao.del(config_id);
    }
	
	@Test
    public void testGetSwitchList() {
        sysConfigDao.getSwitchList();
    }
	
	@Test
    public void testQueryByKey() {
        SysConfig parameter = new SysConfig();
        parameter.setConfigName("Stone");
        // parameter.getStartIndex(new Object());
        // parameter.getPageSize(new Object());
        sysConfigDao.queryByKey(parameter);
    }
	
	@Test
    public void testGetListByConName() {
        String conName = "Jone";
        sysConfigDao.getListByConName(conName);
    }
	
	@Test
    public void testGetList() {
        SysConfig parameter = new SysConfig();
        parameter.setConfigType(3);
        parameter.setConfigName("Jim");
        sysConfigDao.getList(parameter);
    }
	
	@Test
    public void testAdd() {
        SysConfig parameter = new SysConfig();
        parameter.setConfigType(450);
        parameter.setConfigName("Jone");
        parameter.setConfigContent("Joe");
        parameter.setConfigOrder(799);
        parameter.setMemo("Jax");
        sysConfigDao.add(parameter);
    }
	
	@Test
    public void testUpdate() {
        SysConfig parameter = new SysConfig();
        parameter.setConfigType(387);
        parameter.setConfigName("Jim");
        parameter.setConfigContent("Joe");
        parameter.setConfigOrder(883);
        parameter.setMemo("Jone");
        parameter.setConfigId((long)8101);
        parameter.setConfigName("Jim");
        sysConfigDao.update(parameter);
    }
	
	@Test
    public void testTotalSysconfigSizeByParams() {
        String config_name = "Jone";
        sysConfigDao.totalSysconfigSizeByParams(config_name);
    }
	
	@Test
    public void testGet() {
        Long config_id = (long)1074;
        sysConfigDao.get(config_id);
    }
}
