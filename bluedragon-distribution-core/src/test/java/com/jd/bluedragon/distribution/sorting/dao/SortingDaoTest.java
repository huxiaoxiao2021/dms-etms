package com.jd.bluedragon.distribution.sorting.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.jd.bluedragon.distribution.middleend.DynamicSortingQueryDao;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;

public class SortingDaoTest extends AbstractDaoIntegrationTest {

    @Autowired
    private DynamicSortingQueryDao dynamicSortingQueryDao;

    @Autowired
    private SortingDao sortingDao;


    @Test
    public void testFindOrder() {
        Sorting parameter = new Sorting();
        parameter.setBoxCode("Mary");
        parameter.setCreateSiteCode(620);
        parameter.setReceiveSiteCode(748);
        parameter.setCreateTime(new Date());
        dynamicSortingQueryDao.findOrder(parameter);
    }

    @Test
    public void testFindPackCount() {
        HashMap parameter = new HashMap();
        // parameter.put("createSiteCode", new Object());
        // parameter.put("boxCode", new Object());
        dynamicSortingQueryDao.findPackCount(11, "boxCode");
    }

    @Test
    public void testFindOrderDetail() {
        Sorting parameter = new Sorting();
        parameter.setBoxCode("Stone");
        parameter.setCreateSiteCode(533);
        parameter.setReceiveSiteCode(540);
        parameter.setCreateTime(new Date());
        parameter.setUpdateTime(new Date());
        dynamicSortingQueryDao.findOrderDetail(parameter);
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
        dynamicSortingQueryDao.canCancelFuzzy(parameter);
    }

    @Test
    public void testFindBoxDescSite() {
        HashMap parameter = new HashMap();
        // parameter.put("boxCode", new Object());
        // parameter.put("createSiteCode", new Object());
        Sorting sorting = dynamicSortingQueryDao.findBoxDescSite(910, "James");
//        Assert.assertTrue(sorting != null);
    }

    @Test
    public void testUpdate() {
        Sorting parameter = new Sorting();
        parameter.setBsendCode("James");
        parameter.setBoxCode("James");
        parameter.setPackageCode("James");
        parameter.setWaybillCode("James");
        parameter.setPickupCode("James");
        parameter.setReceiveSiteCode(910);
        parameter.setOperateTime(new Date());
        parameter.setUpdateUserCode(910);
        parameter.setUpdateUser("James");
        parameter.setSpareReason("James");
        parameter.setCreateSiteCode(910);
        parameter.setIsCancel(0);
        parameter.setIsLoss(0);
        parameter.setFeatureType(0);
        parameter.setType(1120);
        sortingDao.update(SortingDao.namespace, parameter);
    }


    @Test
    public void testFindByBoxCode() {
        Sorting parameter = new Sorting();
        parameter.setBoxCode("James");
        parameter.setCreateSiteCode(910);
        parameter.setReceiveSiteCode(910);
        parameter.setType(1120);
        dynamicSortingQueryDao.findByBoxCode(parameter);
    }

    @Test
    public void testFindByBsendCode() {
        Sorting parameter = new Sorting();
        parameter.setBsendCode("James");
        parameter.setCreateSiteCode(910);
        dynamicSortingQueryDao.findByBsendCode(parameter);
    }

    @Test
    public void testCanCancel() {
        Sorting parameter = new Sorting();
        parameter.setUpdateUserCode(511);
        parameter.setUpdateUser("James");
        parameter.setCreateSiteCode(910);
        parameter.setType(1120);
        parameter.setPackageCode("James");
        parameter.setWaybillCode("James");
        parameter.setBoxCode("James");
        parameter.setReceiveSiteCode(910);
        dynamicSortingQueryDao.canCancel(parameter);
    }

    @Test
    public void testExistSortingByPackageCode() {
        Sorting parameter = new Sorting();
        parameter.setPackageCode("James");
        parameter.setCreateSiteCode(910);
        dynamicSortingQueryDao.existSortingByPackageCode(parameter);
    }

    @Test
    public void testFindBoxPackList() {
        Sorting parameter = new Sorting();
        parameter.setCreateSiteCode(910);
        parameter.setType(1120);
        parameter.setWaybillCode("James");
        parameter.setPackageCode("James");
        dynamicSortingQueryDao.findBoxPackList(parameter);
    }

    @Test
    public void testQueryByCode() {
        Sorting parameter = new Sorting();
        parameter.setCreateSiteCode(910);
        parameter.setType(1120);
        parameter.setWaybillCode("James");
        parameter.setPackageCode("James");
        dynamicSortingQueryDao.queryByCode(parameter);
    }

    @Test
    public void testAdd() {
        Sorting parameter = new Sorting();
        parameter.setBsendCode("James");
        parameter.setBoxCode("James");
        parameter.setPackageCode("James");
        parameter.setWaybillCode("James");
        parameter.setPickupCode("James");
        parameter.setType(1120);
        parameter.setCreateSiteCode(910);//按createsitecode分库
        parameter.setReceiveSiteCode(910);//Oracle造mysql方案，
        parameter.setCreateUserCode(910);
        parameter.setCreateUser("James");
        parameter.setCreateUserCode(420);
        parameter.setCreateUser("James");
        parameter.setOperateTime(new Date());
        parameter.setIsCancel(0);
        parameter.setSpareReason("James");
        parameter.setIsLoss(0);
        parameter.setFeatureType(0);
        Assert.assertTrue(sortingDao.add(SortingDao.namespace, parameter) > 0);
    }
}
