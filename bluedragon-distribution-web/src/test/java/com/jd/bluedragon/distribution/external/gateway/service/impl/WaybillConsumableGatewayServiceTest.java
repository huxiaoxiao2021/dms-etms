package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.consumable.request.WaybillConsumablePackConfirmReq;
import com.jd.bluedragon.common.dto.consumable.request.WaybillConsumablePdaDto;
import com.jd.bluedragon.common.dto.consumable.response.WaybillConsumablePackConfirmRes;
import com.jd.bluedragon.external.gateway.service.WaybillConsumableGatewayService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author zhengchengfa
 * @Description //TODO
 * @date 20210621
 **/

@ContextConfiguration(locations = {"classpath:distribution-web-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class WaybillConsumableGatewayServiceTest {


    @Autowired
    private WaybillConsumableGatewayService waybillConsumableGatewayService;

    @Test
    public void testCanModifyConsumableNum() {
        for (int i = 0; i<10; i++) {
            try{
                WaybillConsumablePackConfirmReq param = new WaybillConsumablePackConfirmReq();
                param.setBusinessCode("JDVA00000015616");

                JdCResponse<Boolean> res = waybillConsumableGatewayService.canModifyConsumableNum(param);
                System.out.println(res.getData());
            }catch (Exception e) {
                System.out.println("--error-----------------");
            }
        }
    }

    @Test
    public void testGetWaybillConsumableInfo() {
        for (int i = 0; i<10; i++) {
            try{
                WaybillConsumablePackConfirmReq param = new WaybillConsumablePackConfirmReq();
                param.setBusinessCode("JDVA00001301985");
                User user = new User();
                user.setUserErp("dbsxw");
                user.setUserCode(123);
                param.setUser(user);

                JdCResponse<List<WaybillConsumablePackConfirmRes>> res = waybillConsumableGatewayService.getWaybillConsumableInfo(param);
                System.out.println("--succ---------------");
            }catch (Exception e) {
                System.out.println("--error-----------------");
            }
        }
    }

    @Test
    public void testDoWaybillConsumablePackConfirm() {
        for (int i = 0; i<10; i++) {
            try{
                WaybillConsumablePackConfirmReq param = new WaybillConsumablePackConfirmReq();
                param.setBusinessCode("JDVA00001301985");
                User user = new User();
                user.setUserErp("dbsxw");
                user.setUserName("dbsxw");
                user.setUserCode(123);
                param.setUser(user);

                List<WaybillConsumablePdaDto> ll = new ArrayList<>();

                WaybillConsumablePdaDto d1 = new WaybillConsumablePdaDto();
                d1.setConsumableCode("HC001");
                d1.setConfirmQuantity(5.0);
                WaybillConsumablePdaDto d2 = new WaybillConsumablePdaDto();
                d2.setConsumableCode("HC009");
                d2.setConfirmQuantity(8.0);
                ll.add(d1); ll.add(d2);

                param.setWaybillConsumableDtoList(ll);

                JdCResponse<Boolean> res = waybillConsumableGatewayService.doWaybillConsumablePackConfirm(param);
                System.out.println("--succ---------------");
            }catch (Exception e) {
                System.out.println("--error-----------------");
            }
        }
    }


}
