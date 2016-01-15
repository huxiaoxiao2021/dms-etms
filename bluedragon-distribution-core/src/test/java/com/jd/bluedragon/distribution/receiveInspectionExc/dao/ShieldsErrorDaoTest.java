package com.jd.bluedragon.distribution.receiveInspectionExc.dao;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ShieldsErrorDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private ShieldsErrorDao shieldsErrorDao;
	
	
	@Test
    public void testAdd() {
        List parameter = new ArrayList();
        // parameter.getBoxCode(new Object());
        // parameter.getCarCode(new Object());
        // parameter.getShieldsCode(new Object());
        // parameter.getShieldsError(new Object());
        // parameter.getCreateSiteCode(new Object());
        // parameter.getCreateTime(new Object());
        // parameter.getCreateUser(new Object());
        // parameter.getCreateUserCode(new Object());
        // parameter.getYn(new Object());
        // parameter.getBusinessType(new Object());
        // parameter.getUpdateTime(new Object());
        shieldsErrorDao.add(parameter);
    }
}
