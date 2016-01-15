package com.jd.bluedragon.distribution.sorting.dao;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import com.jd.bluedragon.distribution.sorting.domain.SortingException;

public class SortingExceptionDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private SortingExceptionDao sortingExceptionDao;
	
	
	@Test
    public void testGetListByBatchCodeAndSiteCode() {
        Map parameter = new HashMap();
        // parameter.put("batchCode", new Object());
        // parameter.put("siteCode", new Object());
        sortingExceptionDao.getListByBatchCodeAndSiteCode(parameter);
    }
	
	@Test
    public void testAdd() {
        SortingException parameter = new SortingException();
        parameter.setCreateSiteCode(208);
        parameter.setReceiveSiteCode(767);
        parameter.setBusinessType(730);
        parameter.setBoxCode("Jax");
        parameter.setPackageCode("Stone");
        parameter.setExceptionCode(203);
        parameter.setExceptionMessage("Jone");
        parameter.setCreateUserCode(603);
        parameter.setCreateUserName("Joe");
        parameter.setUpdateUserCode(372);
        parameter.setUpdateUserName("Jax");
        parameter.setCreateTime(new Date());
        parameter.setUpdateTime(new Date());
        parameter.setYn(768);
        sortingExceptionDao.add(parameter);
    }
}
