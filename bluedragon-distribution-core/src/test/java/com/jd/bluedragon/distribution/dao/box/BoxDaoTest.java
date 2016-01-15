package com.jd.bluedragon.distribution.dao.box;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.box.dao.BoxDao;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;

public class BoxDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private BoxDao boxDao;
	
	@Test
	public void testAdd(){
		Box box = new Box();
		box.setCode("code1");
		box.setCreateSiteCode(1);
		box.setCreateSiteName("测试站点");
		box.setType("1");
		box.setReceiveSiteCode(2);
		box.setReceiveSiteName("接收站点");
		long s = System.currentTimeMillis();
		boxDao.add(BoxDao.namespace, box);
		long e = System.currentTimeMillis();
		System.out.println(e-s);
		
		s = System.currentTimeMillis();
		boxDao.add(BoxDao.namespace, box);
		e = System.currentTimeMillis();
		System.out.println(e-s);
	}

}
