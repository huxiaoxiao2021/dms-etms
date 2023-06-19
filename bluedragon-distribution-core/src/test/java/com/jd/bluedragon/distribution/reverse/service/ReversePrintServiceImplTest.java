package com.jd.bluedragon.distribution.reverse.service;

import com.jd.bluedragon.distribution.reverse.domain.ExchangeWaybillDto;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 类描述信息
 *
 * @author: hujiping
 * @date: 2020/11/16 14:22
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/distribution-core-context-test.xml"})
public class ReversePrintServiceImplTest {

    @Autowired
    private ReversePrintService reversePrintService;

    @Test
    public void saveReturnAddressInfo(){
        ExchangeWaybillDto request = new ExchangeWaybillDto();
        request.setWaybillCode("JDV000488496740");
        request.setCreateSiteCode(910);
        try {
            reversePrintService.saveReturnAddressInfo(request);
            Assert.assertTrue(true);
        }catch (Exception e){
            Assert.assertTrue(false);
        }
    }

}