package com.jd.bluedragon.distribution.dao.version;

import java.util.Date;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import com.jd.bluedragon.distribution.version.dao.ClientConfigDao;
import com.jd.bluedragon.distribution.version.domain.ClientConfig;


public class ClientConfigDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private ClientConfigDao clientConfigDao;
	
	@Test
	public void testAdd(){
		ClientConfig clientConfig = new ClientConfig();
		clientConfig.setCreateTime(new Date());
		clientConfig.setProgramType(1);
		clientConfig.setSiteCode("abcd");
		clientConfig.setUpdateTime(new Date());
		clientConfig.setVersionCode("1.0");
		clientConfig.setYn(1);
		clientConfigDao.add(clientConfig);
	}

}
