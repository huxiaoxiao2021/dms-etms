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

	@Test
	public void testfindSortingPackages(){
        Sorting sorting = new Sorting();
        sorting.setId(0L);
        sorting.setBoxCode("");
        sorting.setBoxCodes("");
        sorting.setWaybillCode("");
        sorting.setPackageCode("");
        sorting.setPickupCode("");
        sorting.setCreateSiteCode(0);
        sorting.setCreateSiteName("");
        sorting.setReceiveSiteCode(0);
        sorting.setReceiveSiteName("");
        sorting.setCreateUserCode(0);
        sorting.setCreateUser("");
        sorting.setCreateTime(new Date());
        sorting.setOperateTime(new Date());
        sorting.setUpdateUserCode(0);
        sorting.setUpdateUser("");
        sorting.setUpdateTime(new Date());
        sorting.setYn(0);
        sorting.setStatus(0);
        sorting.setType(0);
        sorting.setIsCancel(0);
        sorting.setExcuteCount(0);
        sorting.setExcuteTime(new Date());
        sorting.setSpareReason("");
        sorting.setIsLoss(0);
        sorting.setFeatureType(0);
        sorting.setWhReverse(0);
//        sorting.setBoxCodeList(Lists.newArrayList());
        sorting.setBsendCode("");

        sortingService.findSortingPackages(sorting);
    }
}
