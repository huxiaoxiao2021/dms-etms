package com.jd.bluedragon.distribution.version.dao;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.version.domain.ClientVersion;

public class ClientVersionDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private ClientVersionDao clientVersionDao;
	
	
	@Test
    public void testExists() {
        ClientVersion parameter = new ClientVersion();
        parameter.setVersionCode("Stone");
        parameter.setVersionType(362);
        clientVersionDao.exists(parameter);
    }
	
	@Test
    public void testGetByVersionType() {
        Integer versionType = 741;
        clientVersionDao.getByVersionType(versionType);
    }
	
	@Test
    public void testUpdate() {
        ClientVersion parameter = new ClientVersion();
        parameter.setVersionCode("Joe");
        parameter.setVersionType(546);
        parameter.setDownloadUrl("James");
        parameter.setMemo("Joe");
        parameter.setYn(625);
        parameter.setVersionId((long)8780);
        clientVersionDao.update(parameter);
    }
	
	@Test
    public void testAdd() {
        ClientVersion parameter = new ClientVersion();
        parameter.setVersionCode("Mary");
        parameter.setVersionType(563);
        parameter.setDownloadUrl("Stone");
        parameter.setMemo("Jim");
        clientVersionDao.add(parameter);
    }
	
	@Test
    public void testGet() {
        Long versionId = (long)8423;
        clientVersionDao.get(versionId);
    }
	
	@Test
    public void testGetAllAvailable() {
        clientVersionDao.getAllAvailable();
    }
	
	@Test
    public void testGetByVersionCode() {
        String versionCode = "Joe";
        clientVersionDao.getByVersionCode(versionCode);
    }
	
	@Test
    public void testDelete() {
        Long versionId = (long)5434;
        clientVersionDao.delete(versionId);
    }
	
	@Test
    public void testGetAll() {
        clientVersionDao.getAll();
    }
}
