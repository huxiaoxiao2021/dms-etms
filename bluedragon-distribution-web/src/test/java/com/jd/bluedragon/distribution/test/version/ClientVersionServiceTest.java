package com.jd.bluedragon.distribution.test.version;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.jd.bluedragon.distribution.version.domain.ClientVersion;
import com.jd.bluedragon.distribution.version.service.ClientVersionService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/distribution-core-context.xml" })
public class ClientVersionServiceTest {

	@Autowired
	private ClientVersionService clientVersionService;
		
	@Test
	public void testGetById() {
		ClientVersion clientVersion=clientVersionService.getById(1l);
		System.out.println("getById(1l)");
		System.out.println(clientVersion);
	}

	@Test
	public void testGetByVersionCode() {
		List<ClientVersion> list=clientVersionService.getByVersionCode("20120321");
		assertNotNull(list);
		System.out.println("getByVersionCode('20120321')");
		for (ClientVersion clientVersion : list) {
            System.out.println(clientVersion);
        }
	}

	@Test
	public void testGetByVersionType() {
		List<ClientVersion> list=clientVersionService.getByVersionType(1);
		assertNotNull(list);
		System.out.println("getByVersionType(1)");
		for (ClientVersion clientVersion : list) {
            System.out.println(clientVersion);
        }
	} 

	@Test
	public void testAdd() {
		ClientVersion clientVersion=new ClientVersion(); 
		clientVersion.setDownloadUrl("http://10.10.236.45:1999/dms-web/");
		clientVersion.setMemo("PDA 测试版");
		clientVersion.setVersionCode("20120331");
		clientVersion.setVersionType(0);
		boolean b=clientVersionService.add(clientVersion);
		System.out.println("add "+b);
		
	}
 
	@Test
	public void testUpdate() {
		ClientVersion clientVersion=clientVersionService.getById(5L);
		assertNotNull(clientVersion);
		clientVersion.setMemo(clientVersion.getMemo()+" updated");
		clientVersion.setYn(1);
		boolean b=clientVersionService.update(clientVersion);
		System.out.println("update "+b);
	}

	@Test
	public void testDelete() {
		boolean b=clientVersionService.delete(7l);
		System.out.println("delete "+b);
	}
	@Test
	public void testGetAll() {
		List<ClientVersion> list=clientVersionService.getAll();
		assertNotNull(list);
		System.out.println("getAll");
		for (ClientVersion clientVersion : list) {
            System.out.println(clientVersion);
        }
	}

	@Test
	public void testExists() {
		ClientVersion clientVersion=new ClientVersion(); 
		clientVersion.setVersionCode("20120321");
		clientVersion.setVersionType(0);
		boolean b=clientVersionService.exists(clientVersion);
		System.out.println("exists "+b);
		
		clientVersion=new ClientVersion(); 
		clientVersion.setVersionCode("20120321");
		clientVersion.setVersionType(0);
		b= clientVersionService.exists(clientVersion);
		System.out.println("exists "+b);
	}
	
}
