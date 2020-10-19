package com.jd.bluedragon.distribution.consumer.thirdboxweight;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.consumer.schedule.BdWaybillScheduleMqListener;
import com.jd.bluedragon.distribution.loadAndUnload.UnloadCar;
import com.jd.bluedragon.distribution.loadAndUnload.UnloadCarDistribution;
import com.jd.bluedragon.distribution.schedule.service.DmsScheduleInfoService;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeEntity;
import com.jd.bluedragon.distribution.weightVolume.handler.WeightVolumeHandlerStrategy;
import com.jd.etms.vos.dto.CommonDto;
import com.jd.etms.vos.dto.SealCarDto;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.report.weightVolumeFlow.WeightVolumeFlowJSFService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * @author ：xumigen
 * @date ：Created in 2020/10/17 10:40
 */
@RunWith(MockitoJUnitRunner.class)
public class ThirdBoxWeightConsumerTest {

    @InjectMocks
    private ThirdBoxWeightConsumer thirdBoxWeightConsumer;

    @Mock
    private WeightVolumeFlowJSFService weightVolumeFlowJSFService;

    @Mock
    private WeightVolumeHandlerStrategy weightVolumeHandlerStrategy;

    @Mock
    private BaseMajorManager baseMajorManager;



    @Before
    public void before(){
        BaseStaffSiteOrgDto staffSiteDTO = new BaseStaffSiteOrgDto();
        staffSiteDTO.setSiteCode(1111);


        when(weightVolumeFlowJSFService.checkIfExistFlow("BC1001201016190031431001")).thenReturn(Boolean.TRUE);
        when(weightVolumeFlowJSFService.checkIfExistFlow("ZY0000008481513")).thenReturn(Boolean.FALSE);
        when(baseMajorManager.getBaseStaffByStaffId(anyInt())).thenReturn(staffSiteDTO);
        when(baseMajorManager.getBaseSiteByDmsCode(anyString())).thenReturn(staffSiteDTO);
        when(weightVolumeHandlerStrategy.doHandler(ArgumentMatchers.<WeightVolumeEntity>any())).thenReturn(ArgumentMatchers.<InvokeResult<Boolean>>any());
    }

    @Test
    public void testconsumer()throws Exception{
        String json = "{\n" +
                "\"tenantCode\" : \"ECONOMIC_NET\",\n" +
                "\"startSiteCode\" : \"JJW004108\",\n" +
                "\"endSiteCode\" : \"577F003\",\n" +
                "\"operatorId\" : \"JJW00410812\",\n" +
                "\"operatorName\" : \"章红霞\",\n" +
                "\"operatorUnitName\" : \"温州瑞安飞云公司\",\n" +
                "\"operatorTime\" : 1602901764000,\n" +
                "\"boxCode\" : \"BC1001201016190031431001\",\n" +
                "\"waybillCode\" : \"ZY0000008481513\",\n" +
                "\"packageCode\" : \"ZY0000008481513-1-1-\",\n" +
                "\"operationType\" : 1\n" +
                "}";
        Message message = new Message();
        message.setBody(json.getBytes());
        thirdBoxWeightConsumer.consume(message);
    }

}
