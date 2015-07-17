package com.jd.bluedragon.distribution.testCore.inspection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jd.bluedragon.distribution.receive.dao.CenConfirmDao;
import com.jd.bluedragon.distribution.receive.domain.CenConfirm;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/distribution-core-context.xml" })
public class CenConfirmDaoTest {

	@Autowired
	private CenConfirmDao cenConfirmDao;
	
	@Test
	public void testUpdateYnByPackage(){
		CenConfirm cenConfirm = new CenConfirm.Builder("-1", 1006).receiveSiteCode(10).type(30).build();
		int result =  cenConfirmDao.updateYnByPackage(cenConfirm);
		System.out.println("========"+result);
	}
}
