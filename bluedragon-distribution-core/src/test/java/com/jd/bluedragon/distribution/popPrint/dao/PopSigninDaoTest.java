package com.jd.bluedragon.distribution.popPrint.dao;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.popPrint.domain.PopSignin;
import com.jd.bluedragon.distribution.popPrint.dto.PopSigninDto;

public class PopSigninDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private PopSigninDao popSigninDao;
	
	
	@Test
    public void testGetCount() {
        PopSigninDto parameter = new PopSigninDto();
        parameter.setQueueNo("Jim");
        parameter.setCreateSiteCode(140);
        parameter.setExpressCode("Jone");
        parameter.setThirdWaybillCode("Jone");
        parameter.setCreateUserCode(142);
        parameter.setCreateUser("Jax");
        parameter.setSignStartTime("Jim");
        parameter.setSignEndTime("Jax");
        popSigninDao.getCount(parameter);
    }
	
	@Test
    public void testInsert() {
        PopSignin parameter = new PopSignin();
        parameter.setQueueNo("Jim");
        parameter.setThirdWaybillCode("Mary");
        parameter.setCreateUserCode(505);
        parameter.setCreateUser("James");
        parameter.setOperateTime(new Date());
        parameter.setExpressCode("Joe");
        parameter.setExpressName("Mary");
        parameter.setCreateSiteCode(188);
        parameter.setCreateSiteName("Joe");
        popSigninDao.insert(parameter);
    }
	
	@Test
    public void testGetPopSigninList() {
        PopSigninDto parameter = new PopSigninDto();
        parameter.setQueueNo("Jone");
        parameter.setCreateSiteCode(749);
        parameter.setExpressCode("Joe");
        parameter.setThirdWaybillCode("James");
        parameter.setCreateUserCode(618);
        parameter.setCreateUser("Joe");
        parameter.setSignStartTime("Jone");
        parameter.setSignEndTime("Stone");
        parameter.setEnd(701);
        parameter.setStart(475);
        popSigninDao.getPopSigninList(parameter);
    }
	
	@Test
    public void testUpdate() {
        PopSignin parameter = new PopSignin();
        parameter.setQueueNo("Joe");
        parameter.setCreateUserCode(302);
        parameter.setCreateUser("Stone");
        parameter.setOperateTime(new Date());
        parameter.setExpressCode("Stone");
        parameter.setExpressName("Jax");
        parameter.setCreateSiteCode(637);
        parameter.setThirdWaybillCode("Jax");
        popSigninDao.update(parameter);
    }
}
