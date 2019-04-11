package com.jd.bluedragon.utils;

import com.jd.bluedragon.distribution.api.request.DeliveryRequest;
import com.jd.bluedragon.distribution.kuaiyun.weight.exception.WeighByWaybillExcpetion;
import com.jd.bluedragon.distribution.kuaiyun.weight.service.impl.WeighByWaybillServiceImpl;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class WaybillUtilTest {
    WaybillUtil waybillUtil = new WaybillUtil();

    @Test
    public void testGetWaybillCodeByPackageBarcode(){
        String packageCode = "12345678901-1-5-";

        System.out.println(waybillUtil.getWaybillCode("BC755F002316F00100597010BC755F002316F00100597010"));
        WeighByWaybillServiceImpl a =new WeighByWaybillServiceImpl();
        try {
            System.out.println(a.convertToWaybillCode("VA66649334524"));
        } catch (WeighByWaybillExcpetion weighByWaybillExcpetion) {
            weighByWaybillExcpetion.printStackTrace();
        }
        System.out.println(WaybillUtil.getCrossCodeOnPackageCode("85358175547N1S1H2"));
        List<DeliveryRequest> request = new ArrayList<>();
        DeliveryRequest item = new DeliveryRequest();
        item.setBoxCode("85358175547N1S1H2");
        request.add(item);
        System.out.println(JsonUtil.getInstance().list2Json(request));


    }
}
