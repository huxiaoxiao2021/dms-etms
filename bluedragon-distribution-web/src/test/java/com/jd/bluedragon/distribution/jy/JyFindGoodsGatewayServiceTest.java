package com.jd.bluedragon.distribution.jy;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.inventory.InventoryTaskQueryReq;
import com.jd.bluedragon.external.gateway.service.JyFindGoodsGatewayService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

/**
 * 解封车单测
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2023-03-07 10:19:35 周二
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:distribution-web-context.xml")
public class JyFindGoodsGatewayServiceTest {


    @Autowired
    private JyFindGoodsGatewayService jyFindGoodsGatewayService;


    private static final CurrentOperate CURRENT_OPERATE = new CurrentOperate(910,"马驹桥分拣中心",new Date());
    public static final CurrentOperate SITE_40240 = new CurrentOperate(40240, "北京通州分拣中心", new Date());

    public static final String POST = "GW00150001";
    public static final String GROUP_CODE = "G00000059567";

    public static final User USER_wuyoude = new User(17331,"吴有德");
    static {
        USER_wuyoude.setUserErp("wuyoude");
    }

    @Test
    public void getMixScanTaskDefaultNameTest() {
        InventoryTaskQueryReq req = new InventoryTaskQueryReq();
        req.setCurrentOperate(CURRENT_OPERATE);
        req.setUser(USER_wuyoude);
        req.setGroupCode(GROUP_CODE);
        req.setPositionCode(POST);
        while (true) {
            try {
                Object obj = jyFindGoodsGatewayService.findCurrentInventoryTask(req);
                System.out.println("end");
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



}

