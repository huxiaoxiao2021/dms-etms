package com.jd.bluedragon.distribution.popAbnormal.dao;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.popAbnormal.domain.PopReceiveAbnormal;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PopReceiveAbnormalDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private PopReceiveAbnormalDao popReceiveAbnormalDao;
	
	
	@Test
    public void testFindByMap() {
        Map parameter = new HashMap();
        popReceiveAbnormalDao.findByMap(parameter);
    }
	
	@Test
    public void testFindList() {
        Map parameter = new HashMap();
        parameter.put("abnormalId", 40);
        popReceiveAbnormalDao.findList(parameter);
    }
	
	@Test
    public void testDelete() {
        Long abnormalId = (long)40;
        popReceiveAbnormalDao.delete(abnormalId);
    }
	
	@Test
    public void testUpdateById() {
        PopReceiveAbnormal parameter = new PopReceiveAbnormal();
        parameter.setAbnormalStatus(62);
        parameter.setAttr1("Stone");
        parameter.setAttr2("Mary");
        parameter.setAttr3("James");
        parameter.setAttr4("Joe");
        parameter.setUpdateTime(new Date());
        parameter.setAbnormalId((long)9176);
        popReceiveAbnormalDao.updateById(parameter);
    }
	
	@Test
    public void testFindByObj() {
        PopReceiveAbnormal parameter = new PopReceiveAbnormal();
        parameter.setAbnormalId((long)40);
        popReceiveAbnormalDao.findByObj(parameter);
    }
	
	@Test
    public void testFindTotalCount() {
        Map parameter = new HashMap();
        parameter.put("abnormalId", 40);
        popReceiveAbnormalDao.findTotalCount(parameter);
    }
	
	@Test
    public void testAdd() {
        PopReceiveAbnormal parameter = new PopReceiveAbnormal();
        parameter.setOrgCode(818);
        parameter.setOrgName("Jone");
        parameter.setCreateSiteCode(526);
        parameter.setCreateSiteName("Stone");
        parameter.setMainType(17);
        parameter.setMainTypeName("Stone");
        parameter.setSubType(1);
        parameter.setSubTypeName("Mary");
        parameter.setWaybillCode("James");
        parameter.setPopSupNo("Stone");
        parameter.setPopSupName("Mary");
        parameter.setAbnormalStatus(1);
        parameter.setAttr1("Jax");
        parameter.setAttr2("Stone");
        parameter.setAttr3("Stone");
        parameter.setAttr4("Jax");
        parameter.setOperatorName("Stone");
        parameter.setUpdateTime(new Date());
        parameter.setWaybillType(1);
        parameter.setOrderCode("James");
        popReceiveAbnormalDao.add(PopReceiveAbnormalDao.NAME_SPACE,parameter);
    }
}
