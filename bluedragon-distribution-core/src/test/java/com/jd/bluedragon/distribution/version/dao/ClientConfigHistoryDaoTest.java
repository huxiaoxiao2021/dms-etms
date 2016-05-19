package com.jd.bluedragon.distribution.version.dao;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.version.domain.ClientConfigHistory;

import java.util.List;

public class ClientConfigHistoryDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private ClientConfigHistoryDao clientConfigHistoryDao;
	
	
	@Test
    public void testGetAll() {
        clientConfigHistoryDao.getAll();
    }
	
	@Test
    public void testGetByProgramType() {
        ClientConfigHistory parameter = new ClientConfigHistory();
        parameter.setSiteCode("3011");
        parameter.setProgramType(244);
        parameter.setVersionCode("Joe");
        clientConfigHistoryDao.add(parameter);
        List<ClientConfigHistory> list = clientConfigHistoryDao.getByProgramType(244);
        Assert.assertNotNull("按类型获取版本配置历史", list);
        Assert.assertTrue("按类型获取版本配置历史列表", list != null && list.size() > 0);
    }
	
	@Test
    public void testGetBySiteCode() {
        ClientConfigHistory parameter = new ClientConfigHistory();
        parameter.setSiteCode("3011");
        parameter.setProgramType(244);
        parameter.setVersionCode("Joe");
        clientConfigHistoryDao.add(parameter);
        List<ClientConfigHistory> list= clientConfigHistoryDao.getBySiteCode(parameter.getSiteCode());
        Assert.assertTrue("根据站点获取配置历史",list!=null&&list.size()>0);
    }
	
	@Test
    public void testAdd() {
        ClientConfigHistory parameter = new ClientConfigHistory();
        parameter.setSiteCode("Jone");
        parameter.setProgramType(30);
        parameter.setVersionCode("Joe");
        boolean b=clientConfigHistoryDao.add(parameter);
        Assert.assertTrue("配置版本历史",b);
    }
}
