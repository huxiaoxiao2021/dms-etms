package com.jd.bluedragon.distribution.version.dao;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.version.domain.ClientConfigHistory;

public class ClientConfigHistoryDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private ClientConfigHistoryDao clientConfigHistoryDao;
	
	
	@Test
    public void testGetAll() {
        clientConfigHistoryDao.getAll();
    }
	
	@Test
    public void testGetByProgramType() {
        Integer programType = 869;
        clientConfigHistoryDao.getByProgramType(programType);
    }
	
	@Test
    public void testGetBySiteCode() {
        String siteCode = "James";
        clientConfigHistoryDao.getBySiteCode(siteCode);
    }
	
	@Test
    public void testAdd() {
        ClientConfigHistory parameter = new ClientConfigHistory();
        parameter.setSiteCode("Jone");
        parameter.setProgramType(412);
        parameter.setVersionCode("Joe");
        clientConfigHistoryDao.add(parameter);
    }
}
