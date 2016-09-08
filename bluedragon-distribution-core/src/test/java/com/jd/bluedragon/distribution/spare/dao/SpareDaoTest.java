package com.jd.bluedragon.distribution.spare.dao;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.spare.domain.Spare;

import java.util.List;

public class SpareDaoTest extends AbstractDaoIntegrationTest {

    @Autowired
    private SpareDao spareDao;


    @Test
    public void testAdd() {
        Spare parameter = new Spare();
        parameter.setCode("Joe1");
        parameter.setType("Jone");
        parameter.setCreateUserCode(484);
        parameter.setCreateUser("Mary");
        parameter.setCreateUserCode(705);
        parameter.setCreateUser("Stone");
        Integer i = spareDao.add(SpareDao.class.getName(), parameter);
        Assert.assertTrue("SpareDao.add", i > 0);
    }

    @Test
    public void testUpdate() {

        Spare parameter = spareDao.findBySpareCode("Joe1");
        parameter.setCreateUserCode(860);
        parameter.setCreateUser("Mary");
        parameter.setUpdateUserCode(238);
        parameter.setUpdateUser("Jax");
        parameter.setStatus(2);
        parameter.setCode("Joe1");
        parameter.setTimes(1);
        Integer i = spareDao.update(SpareDao.class.getName(), parameter);
        Assert.assertTrue("SpareDao.update", i > 0);
    }

    @Test
    public void testFindSpares() {
        Spare parameter = new Spare();
        parameter.setType("Jone");
        parameter.setStatus(2);
        parameter.setTimes(2);
        parameter.setQuantity(328);
        List<Spare> list = spareDao.findSpares(parameter);

    }

    @Test
    public void testFindBySpareCode() {

        Spare sp = spareDao.findBySpareCode("Joe");

    }
}
