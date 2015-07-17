package com.jd.bluedragon.distribution.test.version;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jd.bluedragon.distribution.version.domain.ClientConfig;
import com.jd.bluedragon.distribution.version.domain.VersionEntity;
import com.jd.bluedragon.distribution.version.service.ClientConfigService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/distribution-core-context.xml" })
public class ClientConfigServiceTest {

	@Autowired
	private ClientConfigService clientConfigService;
	
	 
	@Test
	public void testGetById() {
		ClientConfig clientConfig=clientConfigService.getById(23L);
		System.out.println("getById(23l)");
		System.out.println(clientConfig);
	}

	@Test
	public void testGetBySiteCode() {
		List<ClientConfig> list=clientConfigService.getBySiteCode("BJ_MAJUQIAO");
		assertNotNull(list);
		System.out.println("getBySiteCode('BJ_MAJUQIAO')");
		for (ClientConfig clientConfig : list) {
            System.out.println(clientConfig);
        }
	}

	@Test
	public void testGetByProgramType() {
		List<ClientConfig> list=clientConfigService.getByProgramType(1);
		assertNotNull(list);
		System.out.println("getByProgramType(1)");
		for (ClientConfig clientConfig : list) {
            System.out.println(clientConfig);
        }
	} 
 
	@Test
	public void testAdd() {
		ClientConfig clientConfig=new ClientConfig(); 
		clientConfig.setSiteCode("SH_QINGPU");
		clientConfig.setVersionCode("20120321");
		clientConfig.setProgramType(1);
		boolean b=clientConfigService.add(clientConfig);
		System.out.println("add "+b);
		
	}

	@Test
	public void testUpdate() {
		ClientConfig clientConfig=clientConfigService.getById(25L);
		assertNotNull(clientConfig);
		clientConfig.setVersionCode("20120101");
		boolean b=clientConfigService.update(clientConfig);
		System.out.println("update "+b);
	}

	@Test
	public void testDelete() {
		boolean b=clientConfigService.delete(25l);
		System.out.println("delete "+b);
	}
	
	@Test
	public void testGetVersionEntity() {
		VersionEntity versionEntity=new VersionEntity("BJ_MAJUQIAO", 1);
		VersionEntity ve=clientConfigService.getVersionEntity(versionEntity);
		assertNotNull(ve);
		System.out.println("getVersionEntity");
		System.out.println(ve.getVersionCode()+" "+ve.getDownloadUrl());
	}
	
	@Test
	public void testExists() {
		ClientConfig clientConfig=new ClientConfig();
		clientConfig.setSiteCode("SH_QINGPU");
		clientConfig.setProgramType(0);
		boolean b=clientConfigService.exists(clientConfig);
		System.out.println("exists "+b);
		
		clientConfig=new ClientConfig();
		clientConfig.setSiteCode("BJ_MAJUQIAO");
		clientConfig.setProgramType(1);
		b=clientConfigService.exists(clientConfig);
		System.out.println("exists "+b);
	}
	
	@Test
	public void testGetAll() {
		List<ClientConfig> list=clientConfigService.getAll();
		assertNotNull(list);
		System.out.println("getAll");
		for (ClientConfig clientConfig : list) {
            System.out.println(clientConfig);
        }
	}

	
}
