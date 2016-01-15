package com.jd.bluedragon.distribution.receive.dao;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.receive.domain.TurnoverBox;

public class TurnoverBoxDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private TurnoverBoxDao turnoverBoxDao;
	
	
	@Test
    public void testAdd() {
        TurnoverBox parameter = new TurnoverBox();
        parameter.setTurnoverBoxCode("Joe");
        parameter.setCreateUser("Jone");
        parameter.setCreateUserCode(562);
        parameter.setOperateTime(new Date());
        parameter.setCreateSiteCode(14);
        parameter.setReceiveSiteCode(826);
        parameter.setOperateType(762);
        parameter.setReceiveSiteName("Mary");
        parameter.setCreateSiteName("Jone");
        turnoverBoxDao.add(parameter);
    }
	
	@Test
    public void testGetTurnoverBoxList() {
        TurnoverBox parameter = new TurnoverBox();
        parameter.setCreateSiteCode(230);
        parameter.setTurnoverBoxCode("Joe");
        parameter.setOperateType(122);
        parameter.setPrintStartTime("Mary");
        parameter.setPrintEndTime("Jim");
        parameter.setEnd(591);
        parameter.setStart(679);
        turnoverBoxDao.getTurnoverBoxList(parameter);
    }
	
	@Test
    public void testGetCount() {
        TurnoverBox parameter = new TurnoverBox();
        parameter.setCreateSiteCode(451);
        parameter.setTurnoverBoxCode("Joe");
        parameter.setOperateType(360);
        parameter.setPrintStartTime("Mary");
        parameter.setPrintEndTime("Stone");
        turnoverBoxDao.getCount(parameter);
    }
}
