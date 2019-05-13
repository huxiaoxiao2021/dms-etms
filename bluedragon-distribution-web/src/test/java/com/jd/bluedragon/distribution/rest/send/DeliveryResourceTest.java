package com.jd.bluedragon.distribution.rest.send;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by xumigen on 2019/4/12.
 */
@ContextConfiguration( {"classpath:distribution-web-context-test.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class DeliveryResourceTest {

    @Autowired
    private DeliveryResource deliveryResource;

    @Test
    public void testfindWaybillStatus(){
        deliveryResource.findWaybillStatus("111");
        deliveryResource.findWaybillStatus("");
    }

}
