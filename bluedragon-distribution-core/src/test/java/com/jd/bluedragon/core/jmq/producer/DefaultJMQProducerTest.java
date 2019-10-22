package com.jd.bluedragon.core.jmq.producer;

import com.jd.bluedragon.core.jmq.domain.RailwaySendRegistCostFxmDto;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.exception.JMQException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author : xumigen
 * @date : 2019/9/23
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/spring/distribution-core-context-test.xml"})
public class DefaultJMQProducerTest {

    @Autowired
    private DefaultJMQProducer railwaySendRegistCostFxmMQ;


    @Test
    public void testrailwaySendRegistCostFxmMQ()throws JMQException {
        RailwaySendRegistCostFxmDto costFxmDto = new RailwaySendRegistCostFxmDto();
        costFxmDto.setSendDate(new Date());
        costFxmDto.setOrderCode("100068917662");
        costFxmDto.setTrainNumber("G303");
        costFxmDto.setStartStationCode("123456");
        costFxmDto.setStartStationCodeName("北京南");
        costFxmDto.setEndStationCode("123456");
        costFxmDto.setEndStationCodeName("贵阳北");
        costFxmDto.setWeight(new BigDecimal("2000"));
        costFxmDto.setSendNum(10000);
        costFxmDto.setCarrierCode("A11111111111111");
        costFxmDto.setCarrierName("京东物流");
        costFxmDto.setGoodsType(1);
        costFxmDto.setGoodsTypeName("普货");


        railwaySendRegistCostFxmMQ.send(costFxmDto.getOrderCode(), JsonHelper.toJson(costFxmDto));
    }

}
