package com.jd.bluedragon.distribution.consumer.sealVehicle;

import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.jd.bluedragon.core.jsf.tms.VosVehicleJobQueryWSManager;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.newseal.service.TmsVehicleRouteService;
import com.jd.etms.vos.dto.SealVehicleJobDto;
import com.jd.etms.vos.dto.SealVehicleRouteDto;
import com.jd.jmq.common.message.Message;

/**
 * @author ：wuyoude
 * @date ：Created in 2021/02/25 10:40
 */
@RunWith(MockitoJUnitRunner.class)
public class TmsVehicleJobCancelConsumerTest {

    @InjectMocks
    private TmsVehicleJobCancelConsumer tmsVehicleJobCancelConsumer;

    @Mock
    private TmsVehicleRouteService tmsVehicleRouteService;
    @Mock
    private VosVehicleJobQueryWSManager vosVehicleJobQueryWSManager;
    
    private String vehicleJobCode  = "vehicleJobCode001";

    @Before
    public void before(){

    }

    @Test
    public void testconsumer()throws Exception{
    	JdResult<SealVehicleJobDto> getSealVehicleJobByVehicleJobCode = new JdResult<SealVehicleJobDto>();
    	getSealVehicleJobByVehicleJobCode.toSuccess();
    	getSealVehicleJobByVehicleJobCode.setData(new SealVehicleJobDto());
    	getSealVehicleJobByVehicleJobCode.getData().setRouteList(new ArrayList<SealVehicleRouteDto>());
    	SealVehicleRouteDto route = new SealVehicleRouteDto();
    	route.setVehicleRouteCode("vehicleRouteCode");
    	getSealVehicleJobByVehicleJobCode.getData().getRouteList().add(route);
        when(vosVehicleJobQueryWSManager.getSealVehicleJobByVehicleJobCode(vehicleJobCode)).thenReturn(getSealVehicleJobByVehicleJobCode);
        
        String json = "{\n" +
                "\"vehicleJobCode\" : \""+vehicleJobCode+"\",\n" +
                "\"operateTime\" : 1602901764000,\n" +
                "\"status\" : 200\n" +
                "}";
        Message message = new Message();
        message.setBody(json.getBytes());
        tmsVehicleJobCancelConsumer.consume(message);
    }

}
