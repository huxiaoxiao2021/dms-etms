package com.jd.bluedragon.distribution.testCore.base;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.etms.basic.dto.BaseStaffSiteOrgDto;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/distribution-core-context.xml" })
public class BaseServiceTestCase {

	@Autowired
    private BaseService siteWebService;
	
	//@Test
	public void testQueryDmsBaseSite(){
		BaseStaffSiteOrgDto receiveSite = this.siteWebService.queryDmsBaseSiteByCode(String.valueOf(22200));
		System.out.println(receiveSite.getOrgId());
	}
	
	@Test
	public void testgetSiteInfoByBaseStaffId(){
		Map<Integer,String> sites = siteWebService.getSiteInfoByBaseStaffId(8343);
		Set<Integer> keys = sites.keySet();
		Iterator<Integer> it = keys.iterator();
		while(it.hasNext()){
			Integer id = it.next();
			System.out.println("+++"+id +"==="+sites.get(id));
		}
	}
}
