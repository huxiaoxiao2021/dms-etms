package com.jd.bluedragon.distribution.sorting.dao;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import java.util.HashMap;

public class SortingDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private SortingDao sortingDao;
	
	
	@Test
    public void testFindOrder() {
        Sorting parameter = new Sorting();
        parameter.setBoxCode("Mary");
        parameter.setCreateSiteCode(620);
        parameter.setReceiveSiteCode(748);
        parameter.setCreateTime(new Date());
        sortingDao.findOrder(parameter);
    }
	
	@Test
    public void testFindPackCount() {
        HashMap parameter = new HashMap();
        // parameter.put("createSiteCode", new Object());
        // parameter.put("boxCode", new Object());
        sortingDao.findPackCount(parameter);
    }
	
	@Test
    public void testFindOrderDetail() {
        Sorting parameter = new Sorting();
        parameter.setBoxCode("Stone");
        parameter.setCreateSiteCode(533);
        parameter.setReceiveSiteCode(540);
        parameter.setCreateTime(new Date());
        parameter.setUpdateTime(new Date());
        sortingDao.findOrderDetail(parameter);
    }
	
	@Test
    public void testCanCancelFuzzy() {
        Sorting parameter = new Sorting();
        parameter.setUpdateUserCode(329);
        parameter.setUpdateUser("Jax");
        parameter.setCreateSiteCode(892);
        parameter.setType(226);
        parameter.setPackageCode("Stone");
        parameter.setWaybillCode("Stone");
        parameter.setBoxCode("Mary");
        parameter.setReceiveSiteCode(970);
        sortingDao.canCancelFuzzy(parameter);
    }
	
	@Test
    public void testFindBoxDescSite() {
        HashMap parameter = new HashMap();
        // parameter.put("boxCode", new Object());
        // parameter.put("createSiteCode", new Object());
        sortingDao.findBoxDescSite(parameter);
    }
	
	@Test
    public void testUpdate() {
        Sorting parameter = new Sorting();
        parameter.setBsendCode("Jone");
        parameter.setBoxCode("Jone");
        parameter.setPackageCode("Jim");
        parameter.setWaybillCode("Mary");
        parameter.setPickupCode("Stone");
        parameter.setType(743);
        parameter.setCreateSiteCode(87);
        parameter.setReceiveSiteCode(750);
        parameter.setOperateTime(new Date());
        parameter.setUpdateUserCode(345);
        parameter.setUpdateUser("Mary");
        parameter.setSpareReason("Jone");
        parameter.setIsCancel(484);
        parameter.setIsLoss(229);
        parameter.setFeatureType(966);
        parameter.setBoxCode("Stone");
        parameter.setType(246);
        parameter.setPackageCode("Joe");
        parameter.setCreateSiteCode(786);
        parameter.setReceiveSiteCode(911);
        sortingDao.update(parameter);
    }
	
	@Test
    public void testFindSortingPackages() {
        Sorting parameter = new Sorting();
        parameter.setType(468);
        parameter.setBoxCodes("Jim");
        parameter.setCreateSiteCode(401);
        parameter.setPackageCode("Jone");
        parameter.setWaybillCode("Mary");
        sortingDao.findSortingPackages(parameter);
    }
	
	@Test
    public void testFindByBoxCode() {
        Sorting parameter = new Sorting();
        parameter.setBoxCode("Jax");
        parameter.setCreateSiteCode(177);
        parameter.setReceiveSiteCode(497);
        parameter.setType(59);
        sortingDao.findByBoxCode(parameter);
    }
	
	@Test
    public void testFindByBsendCode() {
        Sorting parameter = new Sorting();
        parameter.setBsendCode("Jax");
        parameter.setCreateSiteCode(382);
        sortingDao.findByBsendCode(parameter);
    }
	
	@Test
    public void testCanCancel() {
        Sorting parameter = new Sorting();
        parameter.setUpdateUserCode(511);
        parameter.setUpdateUser("Jim");
        parameter.setCreateSiteCode(21);
        parameter.setType(874);
        parameter.setPackageCode("Jone");
        parameter.setWaybillCode("Jone");
        parameter.setBoxCode("Joe");
        parameter.setReceiveSiteCode(821);
        sortingDao.canCancel(parameter);
    }
	
	@Test
    public void testExistSortingByPackageCode() {
        Sorting parameter = new Sorting();
        parameter.setPackageCode("Jone");
        parameter.setCreateSiteCode(799);
        sortingDao.existSortingByPackageCode(parameter);
    }
	
	@Test
    public void testFindBoxPackList() {
        Sorting parameter = new Sorting();
        parameter.setCreateSiteCode(278);
        parameter.setType(517);
        parameter.setWaybillCode("Mary");
        parameter.setPackageCode("Stone");
        sortingDao.findBoxPackList(parameter);
    }
	
	@Test
    public void testQueryByCode() {
        Sorting parameter = new Sorting();
        parameter.setCreateSiteCode(355);
        parameter.setType(860);
        parameter.setWaybillCode("James");
        parameter.setPackageCode("Stone");
        sortingDao.queryByCode(parameter);
    }
	
	@Test
    public void testAdd() {
        Sorting parameter = new Sorting();
        parameter.setBsendCode("Mary");
        parameter.setBoxCode("Stone");
        parameter.setPackageCode("Stone");
        parameter.setWaybillCode("Jax");
        parameter.setPickupCode("Joe");
        parameter.setType(767);
        parameter.setCreateSiteCode(131);
        parameter.setReceiveSiteCode(177);
        parameter.setCreateUserCode(37);
        parameter.setCreateUser("Joe");
        parameter.setCreateUserCode(420);
        parameter.setCreateUser("Mary");
        parameter.setOperateTime(new Date());
        parameter.setIsCancel(200);
        parameter.setSpareReason("Jone");
        parameter.setIsLoss(764);
        parameter.setFeatureType(27);
        sortingDao.add(parameter);
    }
}
