package com.jd.bluedragon.distribution.seal.service;

import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.inspection.service.impl.InspectionNotifyServiceImpl;
import com.jd.etms.vos.dto.SealCarDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class NewSealVehicleServiceImplTest {

    @InjectMocks
    private NewSealVehicleServiceImpl newSealVehicleService;

    @Test
    public void testremoveBatchCodeRedisCache(){
        List<SealCarDto> paramList = new ArrayList<>();
        paramList.add(new SealCarDto());
        newSealVehicleService.removeBatchCodeRedisCache(paramList);
    }


}