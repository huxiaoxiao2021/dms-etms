package com.jd.bluedragon.distribution.sorting.dao;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import com.jd.bluedragon.distribution.sorting.domain.SortingReturn;

public class SortingReturnDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private SortingReturnDao sortingReturnDao;
	
	
	@Test
    public void testUpdate() {
        SortingReturn parameter = new SortingReturn();
        parameter.setWaybillCode("James");
        parameter.setPackageCode("Jim");
        parameter.setUserCode(103);
        parameter.setUserName("Stone");
        parameter.setSiteCode(441);
        parameter.setSiteName("Stone");
        parameter.setCreateTime(new Date());
        parameter.setUpdateTime(new Date());
        parameter.setBusinessType(735);
        parameter.setShieldsError("Mary");
        parameter.setShieldsType(734);
        parameter.setSiteCode(851);
        parameter.setBusinessType(626);
        parameter.setPackageCode("Jax");
        parameter.setWaybillCode("Jone");
        sortingReturnDao.update(parameter);
    }
	
	@Test
    public void testUpdateStatus() {
        SortingReturn parameter = new SortingReturn();
        parameter.setStatus(607);
        parameter.setYn(315);
        parameter.setId((long)3757);
        sortingReturnDao.updateStatus(parameter);
    }
	
	@Test
    public void testUpdateListStatusSuccess() {
        List parameter = new ArrayList();
        //set property for item.id
        sortingReturnDao.updateListStatusSuccess(parameter);
    }
	
	@Test
    public void testUpdateListStatusFail() {
        List parameter = new ArrayList();
        //set property for item.id
        sortingReturnDao.updateListStatusFail(parameter);
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
        sortingReturnDao.add(parameter);
    }
	
	@Test
    public void testExists() {
        SortingReturn parameter = new SortingReturn();
        parameter.setSiteCode(700);
        parameter.setWaybillCode("Jone");
        parameter.setPackageCode("Jim");
        sortingReturnDao.exists(parameter);
    }
	
	@Test
    public void testFindByStatus() {
        Integer fetchNum = 581;
        sortingReturnDao.findByStatus(fetchNum);
    }
	
	@Test
    public void testGet() {
        String id = "Joe";
        sortingReturnDao.get(id);
    }
}
