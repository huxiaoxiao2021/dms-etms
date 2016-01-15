package com.jd.bluedragon.distribution.send.dao;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import com.jd.bluedragon.distribution.send.domain.SendM;

public class SendMDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private SendMDao sendMDao;
	
	
	@Test
    public void testSelectOneBySiteAndSendCode() {
        SendM parameter = new SendM();
        parameter.setSendCode("Jone");
        parameter.setCreateSiteCode(510);
        sendMDao.selectOneBySiteAndSendCode(parameter);
    }
	
	@Test
    public void testUpdateSendM() {
        List parameter = new ArrayList();
        //set property for items.sendCode
        sendMDao.updateSendM(parameter);
    }
	
	@Test
    public void testUpdateBySendCodeSelective() {
        SendM parameter = new SendM();
        parameter.setSendCode("Joe");
        parameter.setBoxCode("Jim");
        parameter.setSendUser("Stone");
        parameter.setSendUserCode(543);
        parameter.setCreateSiteCode(291);
        parameter.setReceiveSiteCode(43);
        parameter.setCarCode("Stone");
        parameter.setSendType(371);
        parameter.setCreateUser("Jim");
        parameter.setCreateUserCode(398);
        parameter.setOperateTime(new Date());
        parameter.setUpdateUserCode(978);
        parameter.setUpdaterUser("Stone");
        parameter.setYn(473);
        parameter.setShieldsCarId((long)1730);
        parameter.setSendCode("Stone");
        parameter.setCreateSiteCode(150);
        sendMDao.updateBySendCodeSelective(parameter);
    }
	
	@Test
    public void testCheckSendByBox() {
        SendM parameter = new SendM();
        parameter.setSendType(868);
        parameter.setBoxCode("Joe");
        parameter.setCreateSiteCode(993);
        parameter.setReceiveSiteCode(47);
        sendMDao.checkSendByBox(parameter);
    }
	
	@Test
    public void testSelectByPrimaryKey() {
        String sendMId = "Jim";
        sendMDao.selectByPrimaryKey(sendMId);
    }
	
	@Test
    public void testSelectBySiteAndSendCodeBYtime() {
        SendM parameter = new SendM();
        parameter.setSendCode("James");
        parameter.setCreateSiteCode(789);
        sendMDao.selectBySiteAndSendCodeBYtime(parameter);
    }
	
	@Test
    public void testUpdatetmstBySendCode() {
        SendM parameter = new SendM();
        parameter.setSendmStatus(92);
        parameter.setSendCode("Stone");
        sendMDao.updatetmstBySendCode(parameter);
    }
	
	@Test
    public void testSelectBoxBySendCode() {
        SendM parameter = new SendM();
        parameter.setSendCode("Jone");
        sendMDao.selectBoxBySendCode(parameter);
    }
	
	@Test
    public void testBatchQueryCancelSendMList() {
        SendM parameter = new SendM();
        parameter.setCreateSiteCode(0);
        parameter.setReceiveSiteCode(437);
        parameter.setBoxCode("Jone");
        sendMDao.batchQueryCancelSendMList(parameter);
    }
	
	@Test
    public void testQuerySendCodesByBoxCodes() {
        SendM parameter = new SendM();
        parameter.setBoxCode("James");
        sendMDao.querySendCodesByBoxCodes(parameter);
    }
	
	@Test
    public void testSelectBySendSiteCode() {
        SendM parameter = new SendM();
        parameter.setCreateSiteCode(175);
        parameter.setReceiveSiteCode(768);
        parameter.setSendCode("Jim");
        parameter.setBoxCode("James");
        parameter.setCreateUserCode(875);
        parameter.setOperateTime(new Date());
        parameter.setUpdateTime(new Date());
        sendMDao.selectBySendSiteCode(parameter);
    }
	
	@Test
    public void testSelectOneBySendCode() {
        SendM parameter = new SendM();
        parameter.setSendCode("Mary");
        sendMDao.selectOneBySendCode(parameter);
    }
	
	@Test
    public void testBatchUpdateForDepartrue() {
        SendM parameter = new SendM();
        // parameter.getPlanStartTime(new Object());
        // parameter.getPlanEndTime(new Object());
        // parameter.getWorksheetId(new Object());
        sendMDao.batchUpdateForDepartrue(parameter);
    }
	
	@Test
    public void testSelectBySendCode() {
        SendM parameter = new SendM();
        parameter.setSendCode("Mary");
        sendMDao.selectBySendCode(parameter);
    }
	
	@Test
    public void testFindCancel() {
        SendM parameter = new SendM();
        parameter.setCreateSiteCode(837);
        parameter.setReceiveSiteCode(332);
        parameter.setBoxCode("Jone");
        sendMDao.findCancel(parameter);
    }
	
	@Test
    public void testInsertSendM() {
        SendM parameter = new SendM();
        parameter.setSendCode("Jax");
        parameter.setSendUser("Joe");
        parameter.setSendUserCode(286);
        parameter.setBoxCode("Joe");
        parameter.setTurnoverBoxCode("Jim");
        parameter.setCreateSiteCode(346);
        parameter.setReceiveSiteCode(485);
        parameter.setCarCode("James");
        parameter.setSendType(339);
        parameter.setCreateUser("Joe");
        parameter.setCreateUserCode(475);
        parameter.setOperateTime(new Date());
        parameter.setUpdateUserCode(134);
        parameter.setUpdaterUser("Stone");
        parameter.setUpdateTime(new Date());
        parameter.setYn(802);
        parameter.setShieldsCarId((long)9587);
        parameter.setTransporttype(591);
        sendMDao.insertSendM(parameter);
    }
	
	@Test
    public void testAddBatch() {
        List parameter = new ArrayList();
        //set property for item.sendCode
        //set property for item.sendUser
        //set property for item.sendUserCode
        //set property for item.boxCode
        //set property for item.turnoverBoxCode
        //set property for item.createSiteCode
        //set property for item.receiveSiteCode
        //set property for item.carCode
        //set property for item.sendType
        //set property for item.createUser
        //set property for item.createUserCode
        //set property for item.operateTime
        //set property for item.updateUserCode
        //set property for item.updaterUser
        //set property for item.updateTime
        //set property for item.yn
        //set property for item.shieldsCarId
        //set property for item.transporttype
        sendMDao.addBatch(parameter);
    }
	
	@Test
    public void testFindsendMToReverse() {
        sendMDao.findsendMToReverse();
    }
	
	@Test
    public void testFindSendMByBoxCode() {
        SendM parameter = new SendM();
        parameter.setCreateSiteCode(756);
        parameter.setReceiveSiteCode(628);
        parameter.setSendType(403);
        parameter.setBoxCode("Mary");
        sendMDao.findSendMByBoxCode(parameter);
    }
	
	@Test
    public void testSelectBySiteAndSendCode() {
        SendM parameter = new SendM();
        parameter.setSendCode("Jone");
        parameter.setCreateSiteCode(330);
        sendMDao.selectBySiteAndSendCode(parameter);
    }
	
	@Test
    public void testQuerySendCodesByDepartue() {
        SendM parameter = new SendM();
        parameter.setShieldsCarId((long)4609);
        sendMDao.querySendCodesByDepartue(parameter);
    }
	
	@Test
    public void testBatchQuerySendMList() {
        SendM parameter = new SendM();
        parameter.setCreateSiteCode(86);
        parameter.setReceiveSiteCode(932);
        parameter.setBoxCode("Jim");
        sendMDao.batchQuerySendMList(parameter);
    }
	
	@Test
    public void testCancelSendM() {
        SendM parameter = new SendM();
        parameter.setUpdateUserCode(131);
        parameter.setUpdaterUser("James");
        parameter.setUpdateTime(new Date());
        parameter.setUpdateTime(new Date());
        parameter.setCreateSiteCode(448);
        parameter.setReceiveSiteCode(787);
        parameter.setBoxCode("Jim");
        sendMDao.cancelSendM(parameter);
    }
}
