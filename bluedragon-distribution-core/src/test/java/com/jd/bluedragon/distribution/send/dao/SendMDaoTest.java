package com.jd.bluedragon.distribution.send.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import com.jd.bluedragon.distribution.send.domain.SendM;

public class SendMDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private SendMDao sendMDao;
	
	
	//@Test
    public void testSelectOneBySiteAndSendCode() {
        sendMDao.selectOneBySiteAndSendCode(510, "Jone");
    }
	
	
	//@Test
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
	
	//@Test
    public void testCheckSendByBox() {
        SendM parameter = new SendM();
        parameter.setSendType(868);
        parameter.setBoxCode("Joe");
        parameter.setCreateSiteCode(993);
        parameter.setReceiveSiteCode(47);
        sendMDao.checkSendByBox(parameter);
    }
	
	
	//@Test
    public void testSelectBySiteAndSendCodeBYtime() {
        SendM parameter = new SendM();
        parameter.setSendCode("James");
        parameter.setCreateSiteCode(789);
        sendMDao.selectBySiteAndSendCodeBYtime(789, "James");
    }
	
	//@Test
    public void testSelectBoxBySendCode() {
        SendM parameter = new SendM();
        parameter.setSendCode("Jone");
        sendMDao.selectBoxBySendCode("Jone");
    }
	
	//@Test
    public void testBatchQueryCancelSendMList() {
        SendM parameter = new SendM();
        parameter.setCreateSiteCode(0);
        parameter.setReceiveSiteCode(437);
        parameter.setBoxCode("Jone");
        sendMDao.batchQueryCancelSendMList(parameter);
    }
	
	
	//@Test
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
	
	//@Test
    public void testSelectOneBySendCode() {
        SendM parameter = new SendM();
        parameter.setSendCode("Mary");
        sendMDao.selectOneBySendCode("Mary");
    }
	
	
	//@Test
    public void testSelectBySendCode() {
        SendM parameter = new SendM();
        parameter.setSendCode("Mary");
        sendMDao.selectBySendCode("Mary");
    }
	
	@Test
    public void testInsertSendM() {
        SendM parameter = new SendM();
        parameter.setSendCode("1111");
        parameter.setSendUser("Joe");
        parameter.setSendUserCode(286);
        parameter.setBoxCode("2222");
        parameter.setTurnoverBoxCode("112323");
        parameter.setCreateSiteCode(346);
        parameter.setReceiveSiteCode(485);
        parameter.setCarCode("James");
        parameter.setSendType(3);
        parameter.setCreateUser("Joe");
        parameter.setCreateUserCode(475);
        parameter.setOperateTime(new Date());
        parameter.setUpdateUserCode(134);
        parameter.setUpdaterUser("Stone");
        parameter.setUpdateTime(new Date());
        parameter.setYn(1);
        parameter.setTransporttype(3);
        sendMDao.insertSendM(parameter);
    }
	
	//@Test
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
	
	//@Test
    public void testFindSendMByBoxCode() {
        SendM parameter = new SendM();
        parameter.setCreateSiteCode(756);
        parameter.setReceiveSiteCode(628);
        parameter.setSendType(403);
        parameter.setBoxCode("Mary");
        sendMDao.findSendMByBoxCode(parameter);
    }
	
	//@Test
    public void testSelectBySiteAndSendCode() {
        SendM parameter = new SendM();
        parameter.setSendCode("Jone");
        parameter.setCreateSiteCode(330);
        sendMDao.selectBySiteAndSendCode(330, "Jone");
    }
	
	///@Test
    public void testQuerySendCodesByDepartue() {
        SendM parameter = new SendM();
        parameter.setShieldsCarId((long)4609);
        sendMDao.querySendCodesByDepartue(4609l);
    }
	
	//@Test
    public void testBatchQuerySendMList() {
        SendM parameter = new SendM();
        parameter.setCreateSiteCode(86);
        parameter.setReceiveSiteCode(932);
        parameter.setBoxCode("Jim");
        sendMDao.batchQuerySendMList(parameter);
    }
	
	//@Test
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
