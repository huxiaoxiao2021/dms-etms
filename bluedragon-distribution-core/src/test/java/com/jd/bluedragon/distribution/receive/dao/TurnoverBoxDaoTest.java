package com.jd.bluedragon.distribution.receive.dao;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.receive.domain.TurnoverBox;

import java.util.Date;

public class TurnoverBoxDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private TurnoverBoxDao turnoverBoxDao;
	
	
	@Test
    public void testAdd() {
        TurnoverBox parameter = new TurnoverBox();
        parameter.setTurnoverBoxCode("James");
        parameter.setCreateUser("James");
        parameter.setCreateUserCode(910);
        parameter.setOperateTime(new Date());
        parameter.setCreateSiteCode(910);
        parameter.setReceiveSiteCode(910);
        parameter.setOperateType(10);
        parameter.setReceiveSiteName("James");
        parameter.setCreateSiteName("James");
        turnoverBoxDao.add(TurnoverBoxDao.class.getName(),parameter);
    }
	
	@Test
    public void testGetTurnoverBoxList() {
        TurnoverBox parameter = new TurnoverBox();
        parameter.setCreateSiteCode(910);
        parameter.setTurnoverBoxCode("James");
        parameter.setOperateType(10);
        parameter.setPrintStartTime("James");
        parameter.setPrintEndTime("James");
        parameter.setEnd(910);
        parameter.setStart(910);
        turnoverBoxDao.getTurnoverBoxList(parameter);
    }
	
	@Test
    public void testGetCount() {
        TurnoverBox parameter = new TurnoverBox();
        parameter.setCreateSiteCode(910);
        parameter.setTurnoverBoxCode("James");
        parameter.setOperateType(10);
        parameter.setPrintStartTime("James");
        parameter.setPrintEndTime("James");
        Assert.assertTrue(turnoverBoxDao.getCount(parameter)>0);
    }
}
