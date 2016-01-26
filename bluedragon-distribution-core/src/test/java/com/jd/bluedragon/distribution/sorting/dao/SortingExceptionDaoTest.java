package com.jd.bluedragon.distribution.sorting.dao;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.jd.bluedragon.distribution.sorting.domain.SortingException;

public class SortingExceptionDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private SortingExceptionDao sortingExceptionDao;
	
	
	@Test
    public void testGetListByBatchCodeAndSiteCode() {
        List<SortingException> list= sortingExceptionDao.search("Jax", 910);
        Assert.assertTrue(list==null||list.size()==0);
    }
	
	@Test
    public void testAdd() {
        SortingException parameter = new SortingException();
        parameter.setCreateSiteCode(910);
        parameter.setReceiveSiteCode(767);
        parameter.setBusinessType(730);
        parameter.setBoxCode("Jax");
        parameter.setPackageCode("Stone");
        parameter.setExceptionCode(2);
        parameter.setExceptionMessage("Jone");
        parameter.setCreateUserCode(603);
        parameter.setCreateUserName("Joe");
        parameter.setUpdateUserCode(372);
        parameter.setUpdateUserName("Jax");
        parameter.setCreateTime(new Date());
        parameter.setUpdateTime(new Date());
        parameter.setYn(1);
        Integer i= sortingExceptionDao.add(parameter);
        Assert.assertTrue("SortingExceptionDao.Add",i>0);
    }
}
