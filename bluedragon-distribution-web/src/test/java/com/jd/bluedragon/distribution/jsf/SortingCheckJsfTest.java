package com.jd.bluedragon.distribution.jsf;

import com.jd.bluedragon.distribution.api.domain.LoginUser;
import com.jd.bluedragon.distribution.schedule.service.DmsScheduleInfoService;
import com.jd.bluedragon.distribution.waybill.service.WaybillCancelService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author dudong
 * @version 1.0
 * @date 2016/6/8
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/distribution-web-context.xml" })
public class SortingCheckJsfTest {
    @Autowired
    private WaybillCancelService waybillCancelService;
    @Autowired
    private DmsScheduleInfoService dmsScheduleInfoService;

    @Test
    public void testWaybillCancel() {
        Assert.assertTrue(waybillCancelService.isRefundWaybill("42595669421"));
        Assert.assertTrue(waybillCancelService.isRefundWaybill("42595675097"));
        Assert.assertTrue(waybillCancelService.isRefundWaybill("42595672817"));

        Assert.assertFalse(waybillCancelService.isRefundWaybill("42747081125"));
        Assert.assertFalse(waybillCancelService.isRefundWaybill("42747081239"));
        Assert.assertFalse(waybillCancelService.isRefundWaybill("42746445977"));

    }

    @Test
    public void testPrintEdnPickingList(){
        LoginUser user=new LoginUser();
        user.setUserErp("cl");
        dmsScheduleInfoService.printEdnPickingList("ZD20200507",user);
    }
}
