package com.jd.bluedragon.distribution.testCore.sorting;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.sorting.service.SortingService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/distribution-core-context.xml" })
@TransactionConfiguration(defaultRollback = true)		//事物测试
public class SortingServiceTest {

	@Autowired SortingService sortingService;

	@Before
	public void beforeInit(){
		//task something before...
	}
	
	@After
	public void afterInit(){
		//task something after...
	}
	
	
	@Test(expected=Exception.class)
	public void testAdd(){
		Sorting s = new Sorting();
		s.setReceiveSiteCode(43);
		s.setCreateTime(new java.util.Date());
		s.setUpdateTime(new java.util.Date());
		int result = sortingService.add(s);
		Assert.assertEquals(1,result);
	}
}
