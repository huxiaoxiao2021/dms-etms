package com.jd.bluedragon.distribution.saf;

import com.jd.bluedragon.distribution.sorting.domain.OrderDetailEntityResponse;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

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

}