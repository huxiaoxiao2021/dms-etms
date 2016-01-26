package com.jd.bluedragon.distribution.version.dao;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.version.domain.ClientConfig;
import com.jd.bluedragon.distribution.version.domain.VersionEntity;

import java.util.List;
import java.util.Random;

public class ClientConfigDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private ClientConfigDao clientConfigDao;
	
	
	@Test
    public void testGetBySiteCode() {
        ClientConfig parameter = new ClientConfig();
        parameter.setSiteCode("910");
        parameter.setProgramType(88);
        parameter.setVersionCode("Mary");
        boolean b=clientConfigDao.add(parameter);
        List<ClientConfig> clientCofnigs= clientConfigDao.getBySiteCode("910");
        Assert.assertTrue("获取当前站点版本配置信息",clientCofnigs!=null&&clientCofnigs.size()>0);

    }
	
	@Test
    public void testExists() {
        ClientConfig parameter = new ClientConfig();
        parameter.setSiteCode("910");
        parameter.setProgramType(88);
        parameter.setVersionCode("Mary");
        clientConfigDao.add(parameter);
        boolean b=clientConfigDao.exists(parameter);
        Assert.assertTrue("检查是否存在分拣版本配置",b);
    }
	
	@Test
    public void testDelete() {
        testAdd();
        List<ClientConfig> list=clientConfigDao.getAll();
        boolean b=clientConfigDao.delete(list.get(0).getConfigId());
        Assert.assertTrue("删除配置信息",b);
        ClientConfig clientConfig= clientConfigDao.getById(list.get(0).getConfigId());
        Assert.assertTrue("删除配置信息",clientConfig==null);
    }
	
	@Test
    public void testGetVersionEntity() {
        testAdd();
        ClientConfig parameter = new ClientConfig();
        parameter.setSiteCode("910");
        parameter.setProgramType(88);
        parameter.setVersionCode("Mary");
        clientConfigDao.add(parameter);

        VersionEntity parameter1 = new VersionEntity();
        parameter1.setProgramType(88);
        parameter1.setSiteCode("910");
        VersionEntity ve= clientConfigDao.getVersionEntity(parameter1);
        Assert.assertNotNull("",ve);
    }
	
	@Test
    public void testGetByProgramType() {
        Integer programType = 88;
        clientConfigDao.getByProgramType(programType);
    }
	
	@Test
    public void testUpdate() {
        ClientConfig parameter = new ClientConfig();
        parameter.setSiteCode("Mary");
        parameter.setVersionCode("Jim");
        parameter.setProgramType(88);
        parameter.setYn(648);
        parameter.setConfigId((long)8782);
        clientConfigDao.update(parameter);
    }
	
	@Test
    public void testGet() {
        List<ClientConfig> list= clientConfigDao.getAll();
        int i=0;
        Long configId=new Long(0);
        while (i < list.size()) {
            if (configId < list.get(i).getConfigId())
                configId = list.get(i).getConfigId();
            i++;
        }
        ClientConfig parameter = new ClientConfig();
        parameter.setSiteCode("James");
        parameter.setProgramType(88);
        parameter.setVersionCode("Mary");
        boolean b=clientConfigDao.add(parameter);
        ClientConfig clientConfig= clientConfigDao.get(ClientConfigDao.class.getName(), new Long(configId.longValue() + 1));
        Assert.assertNotNull("根据版本配置ID获取版本配置信息",clientConfig);
    }
	
	@Test
    public void testGetAll() {
        clientConfigDao.getAll();
    }
	
	@Test
    public void testAdd() {
        ClientConfig parameter = new ClientConfig();
        parameter.setSiteCode("James");
        parameter.setProgramType(88);
        parameter.setVersionCode("Mary");
        boolean b=clientConfigDao.add(parameter);
        Assert.assertTrue("分拣中心PDA打印系统配置",b);
    }
}
