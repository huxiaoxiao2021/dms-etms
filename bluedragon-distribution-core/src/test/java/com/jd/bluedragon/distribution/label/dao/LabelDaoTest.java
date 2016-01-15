package com.jd.bluedragon.distribution.label.dao;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.label.domain.Label;

public class LabelDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private LabelDao labelDao;
	
	
	@Test
    public void testAdd() {
        Label parameter = new Label();
        parameter.setType("Jax");
        parameter.setCreateSiteCode(199);
        parameter.setCreateSiteName("Jax");
        parameter.setReceiveSiteCode(697);
        parameter.setReceiveSiteName("James");
        parameter.setCreateUser("Jax");
        parameter.setCreateUserCode(19);
        parameter.setOperator_name("Jax");
        parameter.setCreateTime(new Date());
        parameter.setPrintNum(317);
        labelDao.add(parameter);
    }
}
