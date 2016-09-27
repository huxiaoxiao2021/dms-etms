package com.jd.bluedragon.distribution.send.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jd.bluedragon.distribution.api.response.SendBoxDetailResponse;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import com.jd.bluedragon.distribution.send.domain.SendDetail;

public class SendDatailDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private SendDatailDao sendDatailDao;

    @Autowired
    private SendDatailReadDao sendDatailReadDao;

    @Test
    public void testAdd() {
        SendDetail parameter = new SendDetail();
        parameter.setSendCode("1111");
        parameter.setBoxCode("2222");
        parameter.setPackageBarcode("3333-1-1-");
        parameter.setPackageNum(27);
        parameter.setWaybillCode("3333");
        parameter.setPickupCode("Jone");
        parameter.setSendType(30);
        parameter.setCreateSiteCode(346);
        parameter.setReceiveSiteCode(171);
        parameter.setCreateUser("James");
        parameter.setCreateUserCode(521);
        parameter.setOperateTime(new Date());
        parameter.setIsCancel(0);
        parameter.setSpareReason("Stone");
        parameter.setIsLoss(1);
        parameter.setFeatureType(3);
        sendDatailDao.add(SendDatailDao.namespace, parameter);
    }


	@Test
    public void testGetSendSiteID() {
        SendDetail parameter = new SendDetail();
        parameter.setCreateSiteCode(420);
        parameter.setReceiveSiteCode(266);
        parameter.setPackageBarcode("James");
        sendDatailDao.getSendSiteID(parameter);
    }
	
	@Test
    public void testGetSendDatailsWithoutMeasures() {
        Map parameter = new HashMap();
        //sendDatailDao.getSendDatailsWithoutMeasures(100);
    }
	
	@Test
    public void testUpdatewaybillCodeStatus() {
        SendDetail parameter = new SendDetail();
        parameter.setSendCode("Stone");
        parameter.setSendType(255);
        parameter.setCreateUser("Stone");
        parameter.setCreateUserCode(67);
        parameter.setOperateTime(new Date());
        parameter.setStatus(347);
        parameter.setPackageBarcode("Jone");
        parameter.setCreateSiteCode(205);
        parameter.setReceiveSiteCode(227);
        parameter.setBoxCode("Stone");
        sendDatailDao.updatewaybillCodeStatus(parameter);
    }
	
	@Test
    public void testQueryWaybillsByBoxCode() {
        String boxCode = "Joe";
        sendDatailDao.queryWaybillsByBoxCode(boxCode);
    }
	
	@Test
    public void testCancelDelivery() {
        SendDetail parameter = new SendDetail();
        parameter.setSendCode("Jim");
        parameter.setWaybillCode("Jax");
        sendDatailDao.cancelDelivery(parameter);
    }
	
	@Test
    public void testUpdateSendDatail() {
        SendDetail parameter = new SendDetail();
        parameter.setStatus(300);
        parameter.setWaybillCode("Jax");
        parameter.setCreateSiteCode(923);
        parameter.setReceiveSiteCode(774);
        sendDatailDao.updateSendDatail(parameter);
    }
	
	@Test
    public void testBatchQuerySendDList() {
        SendDetail parameter = new SendDetail();
        parameter.setCreateSiteCode(128);
        parameter.setReceiveSiteCode(435);
        parameter.setBoxCode("'Joe'");
        sendDatailDao.batchQuerySendDList(parameter);
    }
	
	@Test
    public void testFindsendDeliveryMessageTotms() {
//        sendDatailDao.findsendDeliveryMessageTotms();
    }
	
	@Test
    public void testUpdateCancelBatch() {
        SendDetail parameter = new SendDetail();
        parameter.setBoxCode("'Jax'");
        parameter.setCreateSiteCode(133);
        parameter.setReceiveSiteCode(419);
        sendDatailDao.updateCancelBatch(parameter);
    }
	
	@Test
    public void testQueryWithoutPackageNum() {
        SendDetail parameter = new SendDetail();
        parameter.setCreateSiteCode(237);
        parameter.setReceiveSiteCode(229);
        parameter.setBoxCode("Stone");
        sendDatailDao.queryWithoutPackageNum(parameter);
    }
	
	@Test
    public void testQueryWaybillsBySendCode() {
        String sendCode = "Jax";
        sendDatailDao.queryWaybillsBySendCode(sendCode);
    }
	
	@Test
    public void testQuerySendDatailsByBoxCode() {
        SendDetail parameter = new SendDetail();
        parameter.setBoxCode("James");
        sendDatailDao.querySendDatailsByBoxCode(parameter);
    }
	
	@Test
    public void testFindUpdatewaybillCodeMessage() {
        sendDatailDao.findUpdatewaybillCodeMessage();
    }
	
	@Test
    public void testFindSendDetails() {
        SendDetail parameter = new SendDetail();
        parameter.setSendCode("Jax");
        sendDatailDao.findSendDetails(parameter);
    }
	
	@Test
    public void testQuerySortingDiff() {
        SendDetail parameter = new SendDetail();
        parameter.setCreateSiteCode(113);
        parameter.setReceiveSiteCode(128);
        parameter.setBoxCode("Mary");
        sendDatailDao.querySortingDiff(parameter);
    }
	
	@Test
    public void testUpdatePackageNum() {
        SendDetail parameter = new SendDetail();
        parameter.setPackageNum(259);
        parameter.setSendDId((long)1907);
        sendDatailDao.updatePackageNum(parameter);
    }
	
	@Test
    public void testQuerySendDatailsBySelective() {
        SendDetail parameter = new SendDetail();
        parameter.setCreateSiteCode(152);
        parameter.setReceiveSiteCode(52);
        parameter.setSendCode("Mary");
        parameter.setBoxCode("Stone");
        parameter.setPackageBarcode("Joe");
        parameter.setWaybillCode("Jax");
        parameter.setSendType(313);
        sendDatailDao.querySendDatailsBySelective(parameter);
    }
	
	@Test
    public void testQueryOneSendDatailBySendM() {
        SendDetail parameter = new SendDetail();
        parameter.setCreateSiteCode(725);
        parameter.setReceiveSiteCode(781);
        parameter.setBoxCode("Joe");
        //sendDatailDao.queryOneSendDatailBySendM(parameter);
    }
	
	@Test
    public void testQueryWaybillsByDepartID() {
        Long departureID = (long)3567;
        //sendDatailDao.queryWaybillsByDepartID(departureID);
    }
	
	@Test
    public void testCheckSendSendCode() {
        SendDetail parameter = new SendDetail();
        parameter.setSendType(756);
        parameter.setReceiveSiteCode(775);
        parameter.setCreateSiteCode(120);
        parameter.setPackageBarcode("Jone");
        parameter.setWaybillCode("Jone");
        parameter.setBoxCode("Jone");
        sendDatailDao.checkSendSendCode(parameter);
    }
	
	@Test
    public void testUpdateMessageTotmsStatus() {
        SendDetail parameter = new SendDetail();
        parameter.setStatus(956);
        parameter.setWaybillCode("Jax");
        parameter.setCreateSiteCode(233);
        parameter.setReceiveSiteCode(377);
        //sendDatailDao.updateMessageTotmsStatus(parameter);
    }
	
	@Test
    public void testUpdateSendStatusByPackage() {
        SendDetail parameter = new SendDetail();
        parameter.setSendCode("Joe");
        parameter.setSendType(718);
        parameter.setCreateUser("Jone");
        parameter.setCreateUserCode(82);
        parameter.setOperateTime(new Date());
        parameter.setStatus(38);
        parameter.setPackageBarcode("Jax");
        parameter.setCreateSiteCode(694);
        parameter.setReceiveSiteCode(413);
        parameter.setBoxCode("Jax");
        sendDatailDao.updateSendStatusByPackage(parameter);
    }
	
	@Test
    public void testQueryOneSendDatailByBoxCode() {
        String boxCode = "Mary";
        sendDatailDao.queryOneSendDatailByBoxCode(boxCode);
    }
	
	@Test
    public void testQueryWaybillsByPackCode() {
        String packCode = "James";
        sendDatailDao.queryWaybillsByPackCode(packCode);
    }
	
	@Test
    public void testCanCancel() {
        SendDetail parameter = new SendDetail();
        parameter.setCreateSiteCode(627);
        parameter.setSendType(858);
        parameter.setPackageBarcode("Jim");
        parameter.setWaybillCode("Jone");
        parameter.setBoxCode("Mary");
        parameter.setReceiveSiteCode(407);
        sendDatailDao.canCancel(parameter);
    }
	
	@Test
    public void testUpdateCancelStasus() {
        SendDetail parameter = new SendDetail();
        parameter.setBoxCode("Mary");
        parameter.setCreateSiteCode(987);
        parameter.setReceiveSiteCode(922);
        sendDatailDao.updateCancel(parameter);
    }
	
	@Test
    public void testQueryBySiteCodeAndSendCode() {
        SendDetail parameter = new SendDetail();
        parameter.setSendCode("Mary");
        parameter.setCreateSiteCode(620);
        sendDatailDao.queryBySiteCodeAndSendCode(parameter);
    }
	
	@Test
    public void testFindOrder() {
        SendDetail parameter = new SendDetail();
        parameter.setOperateTime(new Date());
        parameter.setCreateSiteCode(747);
        parameter.setReceiveSiteCode(769);
        sendDatailDao.findOrder(parameter);
    }
	
	@Test
    public void testQueryBySendCodeAndSendType() {
        SendDetail parameter = new SendDetail();
        parameter.setSendCode("Jim");
        parameter.setSendType(704);
        sendDatailDao.queryBySendCodeAndSendType(parameter);
    }
	
	@Test
    public void testCheckSendByPackage() {
        SendDetail parameter = new SendDetail();
        parameter.setSendType(502);
        parameter.setReceiveSiteCode(894);
        parameter.setCreateSiteCode(820);
        parameter.setPackageBarcode("Jim");
        parameter.setBoxCode("Mary");
        sendDatailDao.checkSendByPackage(parameter);
    }
	
	@Test
    public void testQuerySendDatailsByPackageCode() {
        SendDetail parameter = new SendDetail();
        parameter.setReceiveSiteCode(332);
        parameter.setBoxCode("Jone");
        sendDatailDao.querySendDatailsByPackageCode(parameter);
    }
	
	@Test
    public void testUpdateWeight() {
        SendDetail parameter = new SendDetail();
        parameter.setWeight(0.7887576473532901);
        parameter.setSendDId((long)6766);
        parameter.setCreateSiteCode(433);
        parameter.setPackageBarcode("Jone");
        sendDatailDao.updateWeight(parameter);
    }
	
	@Test
    public void testQuerySendDatailBySendStatus() {
        SendDetail parameter = new SendDetail();
        parameter.setCreateSiteCode(423);
        parameter.setReceiveSiteCode(614);
        parameter.setBoxCode("Jax");
        sendDatailDao.querySendDatailBySendStatus(parameter);
    }
	
	@Test
    public void testUpdate() {
        SendDetail parameter = new SendDetail();
        parameter.setSendCode("Stone");
        parameter.setBoxCode("James");
        parameter.setPackageBarcode("Jim");
        parameter.setWaybillCode("Jone");
        parameter.setPickupCode("Jax");
        parameter.setSendType(720);
        parameter.setCreateSiteCode(252);
        parameter.setReceiveSiteCode(283);
        parameter.setIsCancel(677);
        parameter.setStatus(422);
        parameter.setSpareReason("Stone");
        parameter.setOperateTime(new Date());
        parameter.setIsLoss(432);
        parameter.setFeatureType(15);
        parameter.setBoxCode("Mary");
        parameter.setCreateSiteCode(437);
        parameter.setReceiveSiteCode(203);
        parameter.setPackageBarcode("Jax");
        sendDatailDao.update(SendDatailDao.namespace, parameter);
    }
	
	@Test
    public void testCanCancelFuzzy() {
        SendDetail parameter = new SendDetail();
        parameter.setCreateSiteCode(508);
        parameter.setSendType(585);
        parameter.setPackageBarcode("Jone");
        parameter.setWaybillCode("James");
        parameter.setBoxCode("Jax");
        parameter.setReceiveSiteCode(251);
        sendDatailDao.canCancelFuzzy(parameter);
    }
	
	@Test
    public void testFindDeliveryPackageByCode() {
        SendDetail parameter = new SendDetail();
        parameter.setWaybillCode("Jax");
        parameter.setReceiveSiteCode(32);
        sendDatailDao.findDeliveryPackageByCode(parameter);
    }
	
	@Test
    public void testQuerySendCodesByWaybills() {
        SendDetail parameter = new SendDetail();
        parameter.setWaybillCode("Jax");
        sendDatailDao.querySendCodesByWaybills("'Jax'");
    }
	
	@Test
    public void testFindSendwaybillMessage() {
        //sendDatailDao.findSendwaybillMessage();
    }
	
	@Test
    public void testCancelSendDatail() {
        SendDetail parameter = new SendDetail();
        parameter.setCreateSiteCode(618);
        parameter.setReceiveSiteCode(487);
        parameter.setBoxCode("Stone");
        parameter.setPackageBarcode("'Stone'");
        parameter.setSendCode("('Jax')");
        sendDatailDao.cancelSendDatail(parameter);
    }
	

	
	@Test
    public void testFindDeliveryPackageBySite() {
        SendDetail parameter = new SendDetail();
        parameter.setOperateTime(new Date());
        parameter.setUpdateTime(new Date());
        parameter.setReceiveSiteCode(560);
        sendDatailDao.findDeliveryPackageBySite(parameter);
    }




    /*********************SendDetailReadIndexDao*********************/

    @Test
    public void testFindSendBoxByWaybillCode () {
        List<SendBoxDetailResponse> sendBoxDetailResponses = sendDatailReadDao.findSendBoxByWaybillCode("16635238583");
        Assert.assertNotNull(sendBoxDetailResponses);
    }
}
