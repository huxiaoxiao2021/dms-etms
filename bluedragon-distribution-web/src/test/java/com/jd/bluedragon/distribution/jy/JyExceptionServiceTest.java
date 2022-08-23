package com.jd.bluedragon.distribution.jy;

import com.jd.bluedragon.common.dto.jyexpection.request.ExpUploadScanReq;
import com.jd.bluedragon.common.dto.jyexpection.request.StatisticsByGridReq;
import com.jd.bluedragon.distribution.jy.service.exception.JyExceptionService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:bak/distribution-web-context-test.xml"})
public class JyExceptionServiceTest {
    @Autowired
    JyExceptionService jyExceptionService;


    @Test
    public void uploadScanTest() {
        ExpUploadScanReq req = new ExpUploadScanReq();

        req.setUserErp("wuyoude");
        req.setBarCode("sw000001");
        req.setSource(0);
        req.setPositionCode("GW00029026");

        jyExceptionService.uploadScan(req);
    }
}
