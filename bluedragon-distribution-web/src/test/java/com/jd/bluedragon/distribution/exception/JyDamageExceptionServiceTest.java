package com.jd.bluedragon.distribution.exception;

import com.jd.bluedragon.common.dto.jyexpection.request.ExpDamageDetailReq;
import com.jd.bluedragon.distribution.jy.service.exception.JyDamageExceptionService;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:distribution-web-context.xml"})
@Slf4j
public class JyDamageExceptionServiceTest {

    @Autowired
    private JyDamageExceptionService jyDamageExceptionService;
    private ExpDamageDetailReq req;
    @Autowired
    private SendDatailDao sendDatailDao;
    @Before
    public void init() {
        req = new ExpDamageDetailReq();
        req.setBizId("TestFrank001");
        req.setUserErp("bjxings");
        req.setSiteId(910);
    }

    @Test
    public void testfind(){
        SendDetail parameter = new SendDetail();
        parameter.setOperateTime(DateHelper.parse("2024-09-07 17:09:09", DateHelper.DATE_FORMAT_YYYYMMDDHHmmss2));
        parameter.setPackageBarcode("JDVA00233457082-1-1-");
        parameter.setCreateSiteCode(910);
        SendDetail sendDetail = sendDatailDao.findOneByParams(parameter);
        System.out.println(JsonHelper.toJson(sendDetail));
    }
    @Test
    public void processTaskOfDamage() {
        jyDamageExceptionService.processTaskOfDamage(req);
    }
}
