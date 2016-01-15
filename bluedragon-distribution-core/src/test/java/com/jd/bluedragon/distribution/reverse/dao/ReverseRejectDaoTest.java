package com.jd.bluedragon.distribution.reverse.dao;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.reverse.domain.ReverseReject;

public class ReverseRejectDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private ReverseRejectDao reverseRejectDao;
	
	
	@Test
    public void testAdd() {
        ReverseReject parameter = new ReverseReject();
        parameter.setCreateSiteCode(644);
        parameter.setCreateSiteName("Mary");
        parameter.setOrgId(753);
        parameter.setCky2(628);
        parameter.setStoreId(780);
        parameter.setBusinessType(888);
        parameter.setOrderId("James");
        parameter.setPickwareCode("Joe");
        parameter.setPackageCode("Jax");
        parameter.setOperatorCode("Joe");
        parameter.setOperator("Joe");
        parameter.setOperateTime(new Date());
        parameter.setInspector("James");
        parameter.setInspectorCode("Mary");
        parameter.setInspectTime(new Date());
        reverseRejectDao.add(parameter);
    }
	
	@Test
    public void testGet() {
        ReverseReject parameter = new ReverseReject();
        parameter.setBusinessType(716);
        parameter.setOrderId("Joe");
        parameter.setPackageCode("Mary");
        reverseRejectDao.get(parameter);
    }
	
	@Test
    public void testUpdate() {
        ReverseReject parameter = new ReverseReject();
        parameter.setCreateSiteCode(561);
        parameter.setCreateSiteName("Jax");
        parameter.setCky2(881);
        parameter.setStoreId(203);
        parameter.setBusinessType(933);
        parameter.setOrderId("Mary");
        parameter.setPickwareCode("Jone");
        parameter.setPackageCode("Jax");
        parameter.setOperatorCode("Mary");
        parameter.setOperator("Jim");
        parameter.setOperateTime(new Date());
        parameter.setInspectorCode("Jone");
        parameter.setInspector("Jax");
        parameter.setInspectTime(new Date());
        parameter.setId((long)5840);
        reverseRejectDao.update(parameter);
    }
}
