package com.jd.bluedragon.distribution.exception;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.jyexpection.request.ExpDamageDetailReq;
import com.jd.bluedragon.common.dto.jyexpection.response.Consumable;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyExceptionDamageEnum;
import com.jd.bluedragon.distribution.jy.service.exception.JyDamageExceptionService;
import com.jd.bluedragon.utils.JsonHelper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:distribution-web-context.xml"})
@Slf4j
public class JyDamageExceptionServiceTest {

    @Autowired
    private JyDamageExceptionService jyDamageExceptionService;
    private ExpDamageDetailReq req;

    @Before
    public void init() {
        req = new ExpDamageDetailReq();
        req.setBizId("testclf001");
        req.setUserErp("bjxings");
        req.setSiteId(40240);

        Consumable consumable1 = new Consumable();
        consumable1.setBarcode("06012001");
        consumable1.setCode(1);
        consumable1.setName("纸箱");
        Consumable consumable2 = new Consumable();
        consumable2.setBarcode("09042012");
        consumable2.setCode(6);
        consumable2.setName("温控包装");
        List<Consumable> consumables = new ArrayList<>();
        consumables.add(consumable1);
        consumables.add(consumable2);
        req.setConsumables(consumables);

        req.setDamageType(1);
        req.setRepairType(2);
    }

    @Test
    public void processTaskOfDamage() {

        jyDamageExceptionService.processTaskOfDamage(req);
    }

    @Test
    public void getConsumables() {

        JdCResponse<List<JyExceptionDamageEnum.ConsumableEnum>> consumables = jyDamageExceptionService.getConsumables();

        System.out.println(JsonHelper.toJson(consumables));
    }
}
