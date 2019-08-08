package com.jd.bluedragon.distribution.testCore.sorting;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration( {"classpath:distribution-web-context-test.xml"})
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
