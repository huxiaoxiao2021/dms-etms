package com.jd.bluedragon.distribution.testCore.sorting;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jd.bluedragon.distribution.sorting.dao.SortingDao;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/distribution-core-context.xml" })
public class SortingDaoTestCase {

	@Autowired
	private SortingDao sortingDao;
	
	@Test
	public void testInsert(){
		Sorting s = new Sorting();
		s.setReceiveSiteCode(1);
		s.setCreateTime(new java.util.Date());
		s.setUpdateTime(new java.util.Date());
		int r= sortingDao.add(SortingDao.namespace, s);
		System.out.println("=="+r);
	}
}
