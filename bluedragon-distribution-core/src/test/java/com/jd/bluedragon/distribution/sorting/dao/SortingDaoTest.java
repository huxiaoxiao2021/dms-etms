package com.jd.bluedragon.distribution.sorting.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;

public class SortingDaoTest extends AbstractDaoIntegrationTest {

    @Autowired
    private SortingDao sortingDao;
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
