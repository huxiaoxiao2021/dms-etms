package com.jd.bluedragon.distribution.base.service.impl;

import com.jd.bluedragon.core.base.BasicSelectWsManager;
import com.jd.tms.basic.dto.TransportResourceDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;

/**
 * @Author: liming522
 * @Description:
 * @Date: create in 2020/11/19 14:40
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/spring/distribution-core-context-test.xml"})
public class SiteServiceImplTest {

    @Autowired
    private BasicSelectWsManager basicSelectWsManager;

    @Test
    public void queryCapacityCodeInfo(){
        TransportResourceDto transportResourceDto = new TransportResourceDto();

        transportResourceDto.setStartOrgCode("6");
        transportResourceDto.setSendCarDay(0);
        transportResourceDto.setSendCarHour(0);
        transportResourceDto.setSendCarMin(0);
        transportResourceDto.setArriveCarDay(0);
        transportResourceDto.setArriveCarHour(0);
        transportResourceDto.setArriveCarMin(0);

       /* transportResourceDto.setEndOrgCode("6");*/
       /* transportResourceDto.setEndNodeId(6);
        transportResourceDto.setStartNodeId(364605);*/
      /*  transportResourceDto.setTransType(1); //线路类型
        transportResourceDto.setCarrierType(0); //运力类型
        transportResourceDto.setTransWay(3);*/
       // transportResourceDto.setTransCode("R2011012697968");//运力编码
        //transportResourceDto.setCarrierCode()

       List<TransportResourceDto> result =  basicSelectWsManager.queryPageTransportResource(transportResourceDto);

        for(TransportResourceDto dto :result){
            Date transDisableTime = dto.getTransDisableTime();
            Date transEnableTime = dto.getTransEnableTime();
        }
    }
}
    
