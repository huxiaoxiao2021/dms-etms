package com.jd.bluedragon.distribution.sendprint.service.impl;

import com.jd.bluedragon.distribution.sendprint.domain.PrintQueryCriteria;
import com.jd.bluedragon.distribution.sendprint.domain.SummaryPrintResultResponse;
import com.jd.bluedragon.distribution.sendprint.service.SendPrintService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.util.Objects;

import static org.junit.Assert.*;

/**
 * 类的描述
 *
 * @author hujiping
 * @date 2021/5/25 12:15 下午
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-core-context-test.xml")
public class SendPrintServiceImplTest {

    private final Logger logger = LoggerFactory.getLogger(SendPrintServiceImplTest.class);

    @Autowired
    private SendPrintService sendPrintService;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void batchSummaryPrintQuery() {
        try {
            PrintQueryCriteria criteria = new PrintQueryCriteria();
            criteria.setSiteCode(910);
            criteria.setReceiveSiteCode(39);
            criteria.setSendCode("910-39-20210402104256933");
            SummaryPrintResultResponse response = sendPrintService.batchSummaryPrintQuery(criteria);
            Assert.isTrue(Objects.equals(response.getCode(), 200));
        }catch (Exception e){
            logger.error("服务异常!", e);
            Assert.isTrue(false);
        }
    }
}