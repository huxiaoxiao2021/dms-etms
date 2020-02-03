package com.jd.bluedragon.distribution.popAbnormal.dao;

import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationH2Test;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import com.jd.bluedragon.distribution.popAbnormal.domain.PopAbnormal;
import com.jd.coo.sa.mybatis.plugins.id.SequenceGenAdaptor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class PopAbnormalDaoH2Test extends AbstractDaoIntegrationH2Test {

    @Autowired
    private PopAbnormalDao popAbnormalDao;

    //id生成器
    @Autowired
    SequenceGenAdaptor sequenceGenAdaptor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Assert.assertNotNull(sequenceGenAdaptor);
    }

    @Test
    public void testContextLoads() {
        Assert.assertNotNull(sequenceGenAdaptor);

    }

    @Test
    public void testNewId() {
        Assert.assertEquals(111L, sequenceGenAdaptor.newId("sdf"));
    }


    @Test
    public void testAdd() {
        PopAbnormal parameter = new PopAbnormal();
        parameter.setSerialNumber("Mary");
        parameter.setAbnormalType(1);
        parameter.setWaybillCode("Jax");
        parameter.setOrderCode("Joe");
        parameter.setPopSupNo("Jim");
        parameter.setPopSupName("Jone");
        parameter.setCurrentNum(678);
        parameter.setActualNum(557);
        parameter.setConfirmNum(673);
        parameter.setOperatorCode(737);
        parameter.setOperatorName("Mary");
        parameter.setCreateSiteCode(729);
        parameter.setCreateSiteName("Joe");
        parameter.setConfirmTime(new Date());
        parameter.setMemo("Stone");
        int add = popAbnormalDao.add(parameter);
        Assert.assertEquals(1, add);
    }

    @Test
    public void testFindTotalCount() {
        Map parameter = new HashMap();
        parameter.put("createSiteCode", 7);
        int totalCount = popAbnormalDao.findTotalCount(parameter);
        Assert.assertEquals(1, totalCount);
    }

    @Test
    public void testCheckByMap() {
        Map parameter = new HashMap();
        parameter.put("waybillCode", "Jax");
        parameter.put("orderCode", "Joe");
        Map parameter1 = parameter;
        PopAbnormal popAbnormal = popAbnormalDao.checkByMap(parameter1);
        Assert.assertEquals(new Long(1217691424705740800L), popAbnormal.getId());

    }

    @Test
    public void testUpdateById() {
        PopAbnormal parameter = new PopAbnormal();
        parameter.setConfirmNum(673);
        parameter.setConfirmTime(new Date());
        parameter.setId(1217691424705740800L);
        int i = popAbnormalDao.updateById(parameter);
        Assert.assertEquals(1, i);
    }

    @Test
    public void testFindList() {
        Map parameter = new HashMap();
        parameter.put("createSiteCode", 7);
        List list = popAbnormalDao.findList(parameter);
        Assert.assertEquals(1, list.size());
        System.out.println(1);
    }
}
