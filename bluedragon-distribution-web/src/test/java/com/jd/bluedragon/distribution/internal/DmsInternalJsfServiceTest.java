package com.jd.bluedragon.distribution.internal;

import com.jd.bluedragon.distribution.api.request.BoxRequest;
import com.jd.bluedragon.distribution.api.request.TaskRequest;
import com.jd.bluedragon.distribution.api.response.*;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.internal.service.DmsInternalService;
import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author dudong
 * @date 2016/1/6
 */
public class DmsInternalJsfServiceTest {
    private final static Logger LOGGER = LoggerFactory
            .getLogger(DmsInternalJsfServiceTest.class);

    private DmsInternalService dmsInternalService;

    @Before
    public void getDmsExternalService() {
        ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(
                "/distribution-web-jsf-client-test.xml");
        dmsInternalService = (DmsInternalService) appContext
                .getBean("jsfDmsInternalService");
    }

    @Test
    public void testGetSiteByCode() {
        BaseResponse response = dmsInternalService.getSiteByCode("1");
        Assert.assertNull(response.getSiteCode());
        response = dmsInternalService.getSiteByCode("910");
        Assert.assertNotNull(response.getSiteCode());
        System.out.println(JsonHelper.toJson(response));
        response = dmsInternalService.getSiteByCode(null);
        Assert.assertNotNull(response);
        Assert.assertNull(response.getSiteCode());
    }


    @Test
    public void testGetBox() {
        BoxResponse response = dmsInternalService.getBox("BC000F909010F00100001001");
        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getBoxCode());

        response = dmsInternalService.getBox("BC010F005201100011040");
        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getBoxCode());

        response = dmsInternalService.getBox("1343225646545");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.getCode().intValue() == 20101);
        Assert.assertNull(response.getBoxCode());
    }

    @Test
    public void testGetDatadict() {
        DatadictResponse response = dmsInternalService.getDatadict(70550709, 2, 70550709);
        Assert.assertNotNull(response);
        Assert.assertEquals(response.getDatadicts().size(), 0);
        response = dmsInternalService.getDatadict(0, 1, 18);
        System.out.println(JsonHelper.toJson(response));
        Assert.assertNotNull(response);
        Assert.assertTrue(response.getCode().intValue() == 200);
        Assert.assertTrue(response.getDatadicts().size() != 0);
    }

    @Test
    public void testAddTask() {
        TaskRequest tTaskRequest = new TaskRequest();
        tTaskRequest.setKeyword1("910");
        tTaskRequest.setType(1130);
        tTaskRequest.setSiteCode(910);
        tTaskRequest.setKeyword2("HA01234567899");
        tTaskRequest.setBody("[ {\n" +
                "  \"boxCode\" : \"\",\n" +
                "  \"packageBarOrWaybillCode\" : \"HA01234567899\",\n" +
                "  \"exceptionType\" : \"\",\n" +
                "  \"operateType\" : 0,\n" +
                "  \"receiveSiteCode\" : 0,\n" +
                "  \"id\" : 0,\n" +
                "  \"businessType\" : 50,\n" +
                "  \"userCode\" : 91734,\n" +
                "  \"userName\" : \"刘统彤\",\n" +
                "  \"siteCode\" : 910,\n" +
                "  \"siteName\" : \"北京马驹桥分拣中心\",\n" +
                "  \"operateTime\" : \"2015-07-14 00:14:01\"\n" +
                "} ]");
        TaskResponse response = dmsInternalService.addTask(tTaskRequest);
        System.out.println(JsonHelper.toJson(response));
        Assert.assertNotNull(response);
        Assert.assertTrue(response.getCode().intValue() == 200);
    }


    @Test
    public void testGetBelongSiteCode() {
        BaseResponse response = dmsInternalService.getBelongSiteCode(123363);
        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getSiteCode());
        Assert.assertTrue(response.getCode().intValue() == 200);

        response = dmsInternalService.getBelongSiteCode(123307);
        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getSiteCode());
        Assert.assertTrue(response.getCode().intValue() == 200);

        response = dmsInternalService.getBelongSiteCode(711);
        Assert.assertNotNull(response);
        Assert.assertTrue(response.getSiteCode().intValue() == -1);
        Assert.assertTrue(response.getCode().intValue() == 200);
    }


    @Test
    public void testGetTargetDmsCenter() {
        BaseResponse response = dmsInternalService.getTargetDmsCenter(909, 21);
        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getSiteCode());
        Assert.assertTrue(response.getCode().intValue() == 200);

        response = dmsInternalService.getTargetDmsCenter(511, 24);
        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getSiteCode());
        Assert.assertTrue(response.getCode().intValue() == 200);

        response = dmsInternalService.getTargetDmsCenter(111, 12);
        Assert.assertNotNull(response);
        Assert.assertNull(response.getSiteCode());
        Assert.assertTrue(response.getCode().intValue() == 200);
    }


    @Test
    public void testGetLastScheduleSite() {
        BaseResponse response = dmsInternalService.getLastScheduleSite("13432123432");
        Assert.assertNotNull(response);
        Assert.assertNull(response.getSiteCode());
        Assert.assertTrue(response.getCode().intValue() == 20001);

        response = dmsInternalService.getLastScheduleSite("170326031-1-2-");
        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getSiteCode());
        Assert.assertTrue(response.getCode().intValue() == 200);

        response = dmsInternalService.getLastScheduleSite("170326046-1-1-");
        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getSiteCode());
        Assert.assertTrue(response.getCode().intValue() == 200);
    }

    @Test
    public void testGetReverseReceive() {
        ReverseReceiveResponse response = dmsInternalService.getReverseReceive("170319119");
        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getCanReceive());

        response = dmsInternalService.getReverseReceive("170317592");
        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getCanReceive());

        response = dmsInternalService.getReverseReceive("1234567878");
        Assert.assertNull(response);
    }


    @Test
    public void testGetLossOrderProducts() {
        LossProductResponse response = dmsInternalService.getLossOrderProducts(123432556l);
        Assert.assertNull(response);

        response = dmsInternalService.getLossOrderProducts(145645478l);
        Assert.assertNull(response);

    }


    @Test
    public void testGetSwitchStatus() {
        SysConfigResponse response = dmsInternalService.getSwitchStatus("redis.switch");
        Assert.assertNotNull(response.getConfigContent());
        Assert.assertTrue("1".equals(response.getConfigContent()));
        Assert.assertTrue(response.getCode().intValue() == 200);

        response = dmsInternalService.getSwitchStatus("hh.switch");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.getCode().intValue() == 2200);

    }


    @Test
    public void testCreateAutoSortingBox(){
        BoxRequest request = new BoxRequest();
        request.setCreateSiteCode(910);
        request.setReceiveSiteCode(511);
        request.setQuantity(10);
        InvokeResult<AutoSortingBoxResult> response = dmsInternalService.createAutoSortingBox(request);
        Assert.assertNull(response);
        request.setType("10");
        response = dmsInternalService.createAutoSortingBox(request);
        Assert.assertNotNull(response);
        Assert.assertTrue(response.getCode() == 200);
        System.out.println(JsonHelper.toJson(response));
    }


    @Test
    public void testGetPreseparateSiteId(){
        InvokeResult<Integer> result = dmsInternalService.getPreseparateSiteId("VA00000000884");
        Assert.assertTrue(result.getCode() == 200);
        Assert.assertTrue(result.getData().intValue() == -2);
        System.out.println(JsonHelper.toJson(result));

        result = dmsInternalService.getPreseparateSiteId("VA00000027123");
        System.out.println(JsonHelper.toJson(result));
        Assert.assertTrue(result.getCode() == 200);
        Assert.assertTrue(result.getData().intValue() == 39);


        result = dmsInternalService.getPreseparateSiteId("454564454");
        System.out.println(JsonHelper.toJson(result));
        Assert.assertTrue(result.getCode() == 200);
        Assert.assertTrue(result.getData().intValue() == 0);
    }


    @Test
    public void testGetPopPrintByWaybillCode(){
        PopPrintResponse result = dmsInternalService.getPopPrintByWaybillCode("VA00000000884");
        System.out.println(JsonHelper.toJson(result));
        Assert.assertNotNull(result);
        Assert.assertTrue(result.getCode().intValue() == 2200);

        result = dmsInternalService.getPopPrintByWaybillCode("VA00000027123");
        System.out.println(JsonHelper.toJson(result));
        Assert.assertNotNull(result);
        Assert.assertTrue(result.getCode().intValue() == 200);
        Assert.assertTrue(result.getPopPrintId().intValue() == 3148);

        result = dmsInternalService.getPopPrintByWaybillCode("454564454");
        System.out.println(JsonHelper.toJson(result));
        Assert.assertNotNull(result);
        Assert.assertTrue(result.getCode().intValue() == 2200);
    }
}
