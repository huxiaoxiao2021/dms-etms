package com.jd.bluedragon.distribution.popAbnormal.dao;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.popAbnormal.domain.PopAbnormal;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class PopAbnormalDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private PopAbnormalDao popAbnormalDao;
	
	
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
        popAbnormalDao.add(parameter);
    }
	
	@Test
    public void testFindTotalCount() {
        Map parameter = new HashMap();
         parameter.put("createSiteCode", 729);
        popAbnormalDao.findTotalCount(parameter);
    }
	
	@Test
    public void testCheckByMap() {
        Map parameter = new HashMap();
        parameter.put("waybillCode",String.valueOf(new Random().nextInt()));
        parameter.put("orderCode", "Joe");
        popAbnormalDao.checkByMap(parameter);
    }
	
	@Test
    public void testUpdateById() {
        PopAbnormal parameter = new PopAbnormal();
        parameter.setConfirmNum(171);
        parameter.setConfirmTime(new Date());
        parameter.setId((long)104);
        popAbnormalDao.updateById(parameter);
    }
	
	@Test
    public void testFindList() {
        Map parameter = new HashMap();
        popAbnormalDao.findList(parameter);
    }
}
