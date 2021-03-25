package com.jd.bluedragon.distribution.consumer.sealVehicle;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.jd.bluedragon.distribution.newseal.service.TmsVehicleRouteService;
import com.jd.jmq.common.message.Message;

/**
 * @author ：wuyoude
 * @date ：Created in 2021/02/25 10:40
 */
@RunWith(MockitoJUnitRunner.class)
public class TmsVehicleRouteStatusConsumerTest {

    @InjectMocks
    private TmsVehicleRouteStatusConsumer tmsVehicleRouteStatusConsumer;

    @Mock
    private TmsVehicleRouteService tmsVehicleRouteService;
    
    private String vehicleJobCode  = "vehicleJobCode001";

    @Before
    public void before(){

    }

    @Test
    public void testconsumer()throws Exception{
        String json = "{\n" +
                "\"vehicleJobCode\" : \""+vehicleJobCode+"\",\n" +
                "\"vehicleRouteCode\" : \"vehicleRouteCode\",\n" +
                "\"operateTime\" : 1602901764000,\n" +
                "\"status\" : 200\n" +
                "}";
        Message message = new Message();
        message.setBody(json.getBytes());
        tmsVehicleRouteStatusConsumer.consume(message);
    }

}
