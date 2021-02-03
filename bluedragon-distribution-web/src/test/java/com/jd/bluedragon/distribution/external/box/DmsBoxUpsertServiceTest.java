package com.jd.bluedragon.distribution.external.box;

import com.jd.bluedragon.distribution.box.domain.BoxGenReq;
import com.jd.bluedragon.distribution.box.service.DmsBoxUpsertService;
import com.jd.bluedragon.utils.JsonHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @ClassName DmsBoxUpsertServiceTest
 * @Description
 * @Author wyh
 * @Date 2021/2/2 23:14
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:distribution-web-context.xml"})
public class DmsBoxUpsertServiceTest {

    @Autowired
    private DmsBoxUpsertService dmsBoxUpsertService;

    @Test
    public void generateBoxTest() {
        BoxGenReq req = new BoxGenReq();
        req.setTenantCode("03");
        req.setStartSiteCode(364605);
        req.setDestSiteCode(910);
        req.setOperatorId(10000);
        req.setOperatorName("xingsong");
        req.setBoxType("JX");
        req.setNum(1);
        System.out.println(JsonHelper.toJson(dmsBoxUpsertService.generateBox(req)));

    }
}
