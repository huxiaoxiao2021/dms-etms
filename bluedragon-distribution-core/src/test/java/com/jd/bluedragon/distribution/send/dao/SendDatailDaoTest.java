package com.jd.bluedragon.distribution.send.dao;

import com.jd.bluedragon.distribution.api.response.SendBoxDetailResponse;
import com.jd.bluedragon.distribution.base.dao.KvIndexDao;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.utils.JsonHelper;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SendDatailDaoTest extends AbstractDaoIntegrationTest{


    @Autowired
    private KvIndexDao kvIndexDao;

	@Autowired
	private SendDatailDao sendDatailDao;

    @Autowired
    private SendDatailReadDao sendDatailReadDao;

    @Autowired
    private SendMDao sendMDao;

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
//        sendDatailDao.querySendCodesByWaybills("'Jax'");
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


    ////////////////////////////////////////////////////////
    // SendDetailReadRouterDao TestCase
    ///////////////////////////////////////////////////////
    @Test
    public void testFindSendBoxByWaybillCode () {
        List<SendBoxDetailResponse> sendBoxDetailResponses = sendDatailReadDao.findSendBoxByWaybillCode("16635238583");
        Assert.assertNotNull(sendBoxDetailResponses);
    }

    @Test
    public void testFindSendDetailsBySendMSendCodeAndYn1AndIsCancel0(){
        SendM sendM=new SendM();
        List<String> sendCodes=new ArrayList<String>();
        sendCodes.add("910-39-20160920155200010");
        sendCodes.add("910-1-20160923111244123");
        sendCodes.add("910-25016-20160921110636250");
        sendCodes.add("910-25016-20160921110921111");
        sendCodes.add("910-25016-20160921110923131");
        sendCodes.add("910-25016-20160921151356382");
        sendCodes.add("910-37620-20160930113159606");
        sendCodes.add("910-39-20160921105544016");

        for (String item:sendCodes) {
            sendM.setSendCode(item);
            List<SendDetail> source = this.sendDatailDao.findSendDetails(this.paramSendDetail(sendM));
            List<SendDetail> target = findSendDetailsBySendMSendCodeAndYn1AndIsCancel0(sendM.getSendCode());
            System.out.println(JsonHelper.toJson(source));
            int scount = (null != source) ? source.size() : 0;
            int tcount = (null != target) ? target.size() : 0;
            Assert.assertEquals(scount, tcount);
        }
    }
    private SendDetail paramSendDetail(SendM sendM) {
        SendDetail sendDetail = new SendDetail();
        sendDetail.setSendCode(sendM.getSendCode());
        sendDetail.setCreateSiteCode(sendM.getCreateSiteCode());
        return sendDetail;
    }
    /**
     * 根据SEND_M的send_code查询sendd明细，通过箱号关联
     * @return
     */
    private List<SendDetail> findSendDetailsBySendMSendCodeAndYn1AndIsCancel0(String sendCodeForSendM){
        List<String> boxCodeList=sendMDao.selectBoxCodeBySendCodeAndCreateSiteCode(sendCodeForSendM);
        List<SendDetail> details=new ArrayList<SendDetail>();
        if(null!=boxCodeList&&boxCodeList.size()>0){
            SendDetail detail=new SendDetail();

            for (String item:boxCodeList){
                if(org.apache.commons.lang.StringUtils.isBlank(item)){
                    continue;
                }
                detail.setBoxCode(item.trim());
                List<SendDetail> tempList= sendDatailDao.querySendDatailsByBoxCode(detail);
                if(null!=tempList&&tempList.size()>0){
                    details.addAll(tempList);
                }
            }
        }
        return details;
    }
    /////////////////////////////////////////////////////
    // SendDetailRouterDao TestCase
    ////////////////////////////////////////////////////
    @Test
    public void testQuerySendDatailsBySelectiveRouter() {
        SendDetail sendDetail = new SendDetail();
        sendDetail.setCreateSiteCode(910);
        sendDetail.setWaybillCode("16635238583");
        Assert.assertEquals(1,sendDatailDao.querySendDatailsBySelective(sendDetail).size());

        sendDetail = new SendDetail();
        sendDetail.setWaybillCode("16635238583");
        Assert.assertEquals(2, sendDatailDao.querySendDatailsBySelective(sendDetail).size());

        sendDetail = new SendDetail();
        sendDetail.setWaybillCode("16635238583");
        sendDetail.setBoxCode("BC010F002010Y10000003001");
        Assert.assertEquals(1, sendDatailDao.querySendDatailsBySelective(sendDetail).size());

        sendDetail = new SendDetail();
        sendDetail.setBoxCode("BC010F002010Y10000004101");
        Assert.assertEquals(1, sendDatailDao.querySendDatailsBySelective(sendDetail).size());
    }

    @Test
    public void testQuerySendDatailsBySelectiveRouterSendCode() {
        SendDetail sendDetail = new SendDetail();
        sendDetail.setSendCode("910-39-20160921105544016");
        sendDetail.setWaybillCode("16635238583");
        Assert.assertEquals(1,sendDatailDao.querySendDatailsBySelective(sendDetail).size());

        sendDetail.setSendCode("9103920160921105544016");
        sendDetail.setWaybillCode("16635238583");
        Assert.assertEquals(0,sendDatailDao.querySendDatailsBySelective(sendDetail).size());
    }
    
    @Test
    public void testQuerySendDatailsByPackCodeRouter() {
        SendDetail sendDetail = new SendDetail();
        sendDetail.setBoxCode("BC010F002010Y10000003001");
        sendDetail.setReceiveSiteCode(39);
        Assert.assertEquals(1, sendDatailDao.querySendDatailsByPackageCode(sendDetail).size());

        sendDetail = new SendDetail();
        sendDetail.setBoxCode("BC01324214324324324");
        sendDetail.setReceiveSiteCode(29);
        Assert.assertEquals(4, sendDatailDao.querySendDatailsByPackageCode(sendDetail).size());

        sendDetail = new SendDetail();
        sendDetail.setBoxCode("BC010F005027F04422341413");
        sendDetail.setReceiveSiteCode(25016);
        Assert.assertEquals(1, sendDatailDao.querySendDatailsByPackageCode(sendDetail).size());

    }


    @Test
    public void testQuerySendDatailsByBoxCodeRouter() {
        SendDetail sendDetail = new SendDetail();
        sendDetail.setBoxCode("BC010F002010Y10000003001");
        Assert.assertEquals(1, sendDatailDao.querySendDatailsByBoxCode(sendDetail).size());

        sendDetail = new SendDetail();
        sendDetail.setBoxCode("BC01324214324324324");
        Assert.assertEquals(4, sendDatailDao.querySendDatailsByBoxCode(sendDetail).size());

        sendDetail = new SendDetail();
        sendDetail.setBoxCode("BC010F005027F04422341413");
        Assert.assertEquals(1, sendDatailDao.querySendDatailsByBoxCode(sendDetail).size());

    }


    @Test
    public void testfindDeliveryPackageByCode() {
        SendDetail sendDetail = new SendDetail();
        sendDetail.setWaybillCode("36245754583");
        sendDetail.setReceiveSiteCode(910);
        Assert.assertEquals(0, sendDatailDao.findDeliveryPackageByCode(sendDetail).size());

        sendDetail = new SendDetail();
        sendDetail.setWaybillCode("16635238583");
        sendDetail.setReceiveSiteCode(29);
        Assert.assertEquals(1, sendDatailDao.findDeliveryPackageByCode(sendDetail).size());

    }

    /////////////////////////////////////////////////////
    // KVIndexDao TestCase
    /////////////////////////////////////////////////////

    @Test
    public void testGetIndexByKeywordSet() {
        List<String> param = new ArrayList<String>();
        param.add("BC010F005027F04444341413 ");
        param.add("16635238583");
        param.add(" 16635238583");
        param.add("BC010F005027F00200123412");
        param.add("BC010F005027F00200123412");
        Assert.assertEquals(6, kvIndexDao.queryByKeywordSet(param).size());

        param = new ArrayList<String>();
        Assert.assertEquals(0, kvIndexDao.queryByKeywordSet(param).size());

        param.add(null);
        Assert.assertEquals(0, kvIndexDao.queryByKeywordSet(param).size());
    }

}
