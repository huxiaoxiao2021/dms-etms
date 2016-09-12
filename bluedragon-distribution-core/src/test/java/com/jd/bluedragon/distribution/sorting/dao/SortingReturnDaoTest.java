package com.jd.bluedragon.distribution.sorting.dao;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import com.jd.bluedragon.distribution.sorting.domain.SortingReturn;

public class SortingReturnDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private SortingReturnDao sortingReturnDao;
	
	
	@Test
    public void testUpdate() {

        SortingReturn parameter = new SortingReturn();
        parameter.setWaybillCode("Mary");
        parameter.setPackageCode("Mary");
        parameter.setUserCode(103);
        parameter.setUserName("Stone");
        parameter.setSiteCode(441);
        parameter.setSiteName("Stone");
        parameter.setCreateTime(new Date());
        parameter.setUpdateTime(new Date());
        parameter.setBusinessType(391);
        parameter.setShieldsError("Mary");
        parameter.setShieldsType(734);
        parameter.setSiteCode(924);
        parameter.setBusinessType(391);
        Integer i=sortingReturnDao.update(SortingReturnDao.class.getName(), parameter);
    }
	
	@Test
    public void testUpdateStatus() {
        SortingReturn parameter = new SortingReturn();
        parameter.setStatus(1);
        parameter.setYn(1);
        parameter.setId(6111l);
        Integer i=sortingReturnDao.updateStatus(parameter);

    }

	@Test
    public void testUpdateListStatusFail() {
        List parameter = new ArrayList();
        SortingReturn p1 = new SortingReturn();
        p1.setStatus(607);
        p1.setYn(1);
        p1.setId(6111l);
        parameter.add(p1);
        SortingReturn p2 = new SortingReturn();
        p2.setStatus(607);
        p2.setYn(1);
        p2.setId(6112l);
        parameter.add(p1);
        //set property for item.id
        Integer i= sortingReturnDao.updateListStatusFail(parameter);
    }
	
	@Test
    public void testAdd() {
        SortingReturn parameter = new SortingReturn();
        parameter.setWaybillCode("Mary");
        parameter.setPackageCode("Mary");
        parameter.setUserCode(829);
        parameter.setUserName("Joe");
        parameter.setSiteCode(924);
        parameter.setSiteName("Jim");
        parameter.setCreateTime(new Date());
        parameter.setUpdateTime(new Date());
        parameter.setBusinessType(391);
        parameter.setShieldsError("James");
        parameter.setShieldsType(970);
        parameter.setStatus(0);
        Integer i=sortingReturnDao.add(SortingReturnDao.class.getName(), parameter);
        Assert.assertTrue("SortingReturnDao.testAdd",i>0);
    }
	
	@Test
    public void testExists() {
        SortingReturn parameter = new SortingReturn();
        parameter.setSiteCode(924);
        parameter.setWaybillCode("Mary");
        parameter.setPackageCode("Mary");
        parameter.setBusinessType(391);
        boolean b=sortingReturnDao.exists(parameter);
        Assert.assertTrue("SortingReturnDao.testExists",b);
    }
	
	@Test
    public void testFindByStatus() {
        List<SortingReturn> ll=sortingReturnDao.findByStatus(1);
    }
	
	@Test
    public void testGet() {
        List<SortingReturn> ll=sortingReturnDao.findByStatus(1);
        SortingReturn sr= sortingReturnDao.get(SortingReturnDao.class.getName(), 1L);
    }
}
