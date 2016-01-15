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

public class ClientConfigDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private ClientConfigDao clientConfigDao;
	
	
	@Test
    public void testGetBySiteCode() {
        String siteCode = "Stone";
        clientConfigDao.getBySiteCode(siteCode);
    }
	
	@Test
    public void testExists() {
        ClientConfig parameter = new ClientConfig();
        parameter.setSiteCode("Stone");
        parameter.setProgramType(423);
        clientConfigDao.exists(parameter);
    }
	
	@Test
    public void testDelete() {
        Long configId = (long)6587;
        clientConfigDao.delete(configId);
    }
	
	@Test
    public void testGetVersionEntity() {
        VersionEntity parameter = new VersionEntity();
        parameter.setProgramType(555);
        parameter.setSiteCode("Mary");
        clientConfigDao.getVersionEntity(parameter);
    }
	
	@Test
    public void testGetByProgramType() {
        Integer programType = 355;
        clientConfigDao.getByProgramType(programType);
    }
	
	@Test
    public void testUpdate() {
        ClientConfig parameter = new ClientConfig();
        parameter.setSiteCode("Mary");
        parameter.setVersionCode("Jim");
        parameter.setProgramType(696);
        parameter.setYn(648);
        parameter.setConfigId((long)8782);
        clientConfigDao.update(parameter);
    }
	
	@Test
    public void testGet() {
        Long configId = (long)917;
        clientConfigDao.get(configId);
    }
	
	@Test
    public void testGetAll() {
        clientConfigDao.getAll();
    }
	
	@Test
    public void testAdd() {
        ClientConfig parameter = new ClientConfig();
        parameter.setSiteCode("James");
        parameter.setProgramType(889);
        parameter.setVersionCode("Mary");
        clientConfigDao.add(parameter);
    }
}
