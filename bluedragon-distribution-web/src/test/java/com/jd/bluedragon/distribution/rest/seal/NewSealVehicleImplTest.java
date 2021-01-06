package com.jd.bluedragon.distribution.rest.seal;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.request.NewSealVehicleRequest;
import com.jd.bluedragon.distribution.api.response.NewSealVehicleResponse;
import com.jd.bluedragon.distribution.api.response.RouteTypeResponse;
import com.jd.bluedragon.distribution.seal.service.NewSealVehicleServiceImpl;
import com.jd.tms.basic.dto.TransportResourceDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @Author: liming522
 * @Description:
 * @Date: create in 2020/12/11 18:43
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:distribution-web-context.xml")
public class NewSealVehicleImplTest {

    @Autowired
    private NewSealVehicleServiceImpl newSealVehicleService;


    private NewSealVehicleRequest request;

    @Autowired
    private NewSealVehicleResource  newSealVehicleResource;

    @Before
    public void before(){
        request = new NewSealVehicleRequest();
        request.setStartSiteId("910");
        request.setEndSiteId("39");
        request.setVehicleNumber("551AD35622");
        request.setBatchCode("910-39-20200123110648024");
        request.setStatus(20);
        request.setSealCode("2002258072C");
        request.setTransportCode("R1810119666780");
        request.setPageNums(20);
    }

    @Test
    public void test1(){
        com.jd.tms.basic.dto.CommonDto<TransportResourceDto> commonDto =  newSealVehicleService.getTransportResourceByTransCode("R1810119666780");
        System.out.println(commonDto);
    }

    @Test
    public  void test2(){
        request = new NewSealVehicleRequest();
        request.setStartSiteId("910");
        request.setEndSiteId("39");
        request.setVehicleNumber("551AD35622");
        request.setBatchCode("910-39-20200123110648024");
        request.setStatus(20);
        request.setSealCode("2002258072C");
        request.setTransportCode("R1810119666780");
        request.setPageNums(20);
        request.setSiteCode(910);
        RouteTypeResponse  routeTypeResponse =  newSealVehicleResource.getTransportCode(request);
        System.out.println(routeTypeResponse);
    }

    @Test
    public void test3(){
        String transportCode = "R1810119666780";
        String batchCode = "910-39-20201210110648024";
        Integer sealCarType = Constants.SEAL_TYPE_TRANSPORT;
        NewSealVehicleResponse newSealVehicleResponse =  newSealVehicleResource.newCheckTranCodeAndBatchCode(transportCode,batchCode,sealCarType);
        System.out.println(newSealVehicleResponse);
    }
}
    
