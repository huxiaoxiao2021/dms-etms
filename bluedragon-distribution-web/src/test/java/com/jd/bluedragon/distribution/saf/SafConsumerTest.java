package com.jd.bluedragon.distribution.saf;

import com.jd.bluedragon.distribution.sorting.domain.OrderDetailEntityResponse;
import com.jd.etms.basic.domain.PsStoreInfo;
import com.jd.etms.basic.wss.BasicMajorWS;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.jd.etms.basic.domain.BaseResult;
import com.jd.etms.basic.saf.BasicSafInterface;

public class SafConsumerTest {

    @Test
    public void testWaybillSafService_isCancel() {
        ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(
                "/safConsumerTest.xml");
        WaybillSafService helloBean = (WaybillSafService) appContext
                .getBean("waybillSafRefer");
        WaybillSafResponse res = helloBean.isCancel("42595675943");

        Logger.getRootLogger().info(res.getCode());
    }

    @Test
    public void testOrdersResourceSafService_getOrdersDetails() {
        ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(
                "/safConsumerTest.xml");

        OrdersResourceSafService helloBean = (OrdersResourceSafService) appContext
                .getBean("ordersResourceSafRefer");
        OrderDetailEntityResponse res = helloBean.getOrdersDetails("TC010F001027W00100002019", "2012-12-11",
                "2012-12-11", "479", "5000");

        Logger.getRootLogger().info(res.getCode());
    }

    /**
     * 测试调用基础资料接口,获得站点路由信息
     */
    @Test
    public void testfaBasicSafInterce_getCrossDmsBox() {
        ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(
                "/safConsumerTest.xml");

        BasicSafInterface helloBean = (BasicSafInterface) appContext
                .getBean("basicSafInterfaceRefer");
        //南京-重庆
        BaseResult<String> routInfoRes = helloBean.getCrossDmsBox(2015, 509);

        Logger.getRootLogger().info("BasicSaf getCrossDmsBox Routerinfo:" + routInfoRes.getData() + " ResultCode:" + routInfoRes.getResultCode() + " Message:" + routInfoRes.getMessage());

        //南京-重庆
        routInfoRes = helloBean.getCrossDmsBox(480, 2005);

        Logger.getRootLogger().info("BasicSaf getCrossDmsBox Routerinfo:" + routInfoRes.getData() + " ResultCode:" + routInfoRes.getResultCode() + " Message:" + routInfoRes.getMessage());
    }

    /**
     * 测试调用基础资料接口,获得仓库信息
     */
    @Test
    public void testBasicGetWarehouseByCky2() {
        ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(
                "/safConsumerTest.xml");
        BasicMajorWS basicMajorWSSaf = (BasicMajorWS) appContext.getBean("basicMajorWSSaf");
        BaseResult<PsStoreInfo> psinfo = basicMajorWSSaf.getStoreByCky2Id("wms", 6, 1, "dms");
        System.out.println(psinfo.getResultCode() + psinfo.getMessage() + psinfo.getData().getDmsStoreName());
    }


}