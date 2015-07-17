package com.jd.bluedragon.distribution.external;

import com.jd.bluedragon.distribution.external.service.DmsExternalService;
import com.jd.bluedragon.distribution.wss.dto.BaseEntity;
import com.jd.bluedragon.distribution.wss.dto.SealBoxDto;
import com.jd.bluedragon.distribution.wss.dto.SealVehicleDto;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by dudong on 2015/4/7.
 */
public class DmsExternalServiceTest {
    private final static Logger LOGGER = LoggerFactory
            .getLogger(DmsExternalReadServiceTest.class);

    private DmsExternalService dmsExternalService;

    @Before
    public void getDmsExternalService() {
        ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(
                "/distribution-web-jsf-client-test.xml");
        dmsExternalService = (DmsExternalService) appContext
                .getBean("jsfDmsExternalService");
    }


    @Test
    public void testBatchAddSealVehicle(){
        List<SealVehicleDto> sealVehicleDtos = new ArrayList<SealVehicleDto>();
        for(int i = 0; i < 10; i++){
            SealVehicleDto sealVehicleDto = new SealVehicleDto();
            sealVehicleDto.setCode("1234011545" + i);
            sealVehicleDto.setCreateSiteCode(910);
            sealVehicleDto.setCreateTime(new Date());
            sealVehicleDto.setCreateUser("zhangsan");
            sealVehicleDto.setCreateUserCode(7712);
            sealVehicleDto.setDriver("7K143");
            sealVehicleDto.setDriverCode("7K143");
            sealVehicleDto.setReceiveSiteCode(412);
            sealVehicleDto.setUpdateTime(new Date());
            sealVehicleDto.setVehicleCode("VH1234011545" + i);
            sealVehicleDtos.add(sealVehicleDto);
        }
        BaseEntity<Map<String,Integer>> baseEntity = dmsExternalService.batchAddSealVehicle(sealVehicleDtos);
        if(200 != baseEntity.getCode()){
            LOGGER.error("批量增加封车信息失败，编码{},原因描述{}",baseEntity.getCode(),baseEntity.getMessage());
            return;
        }

        Map<String, Integer> result = baseEntity.getData();
        LOGGER.error("批量增加封车信息成功，返回信息如下：\n");
        for(String key : result.keySet()){
            LOGGER.error("返回VH Code={}, Message={}", key, result.get(key));
        }
    }


    @Test
    public void testBatchAddSealBox(){
        List<SealBoxDto> sealBoxDtos = new ArrayList<SealBoxDto>();
        for(int i = 0; i < 10; i++){
            SealBoxDto sealBoxDto = new SealBoxDto();
            sealBoxDto.setBoxCode("BF000001234324324" + i);
            sealBoxDto.setCode("412341111" + i);
            sealBoxDto.setCreateSiteCode(910);
            sealBoxDto.setCreateTime(new Date());
            sealBoxDto.setCreateUser("zhangsan");
            sealBoxDto.setCreateUserCode(44121);
            sealBoxDto.setReceiveSiteCode(1);
            sealBoxDto.setUpdateTime(new Date());
            sealBoxDto.setUpdateUser("zhangsan");
            sealBoxDto.setUpdateUserCode(11234);
            sealBoxDtos.add(sealBoxDto);
        }

        BaseEntity<Map<String,Integer>> baseEntity = dmsExternalService.batchAddSealBox(sealBoxDtos);

        if(200 != baseEntity.getCode()){
            LOGGER.error("批量增加封箱信息失败，code = {}， message = {}", baseEntity.getCode(), baseEntity.getMessage());
            return;
        }

        Map<String, Integer> result = baseEntity.getData();
        LOGGER.error("批量增加封箱信息成功，返回信息如下：");
        for(String key : result.keySet()){
            LOGGER.error("返回VB Code={}, Message={}", key, result.get(key));
        }
    }

    @Test
    public void testBatchUpdateSealVehicle(){
        List<SealVehicleDto> sealVehicleDtos = new ArrayList<SealVehicleDto>();
        for(int i = 0; i < 10; i++){
            SealVehicleDto sealVehicleDto = new SealVehicleDto();
            sealVehicleDto.setCode("1234011515" + i);
            sealVehicleDto.setCreateSiteCode(910);
            sealVehicleDto.setCreateTime(new Date());
            sealVehicleDto.setCreateUser("lisi");
            sealVehicleDto.setCreateUserCode(7712);
            sealVehicleDto.setDriver("7K123");
            sealVehicleDto.setDriverCode("7K123");
            sealVehicleDto.setReceiveSiteCode(412);
            sealVehicleDto.setUpdateTime(new Date());
            sealVehicleDto.setVehicleCode("VH12340341545" + i);
            sealVehicleDtos.add(sealVehicleDto);
        }
        BaseEntity<Map<String,Integer>> baseEntity = dmsExternalService.batchUpdateSealVehicle(sealVehicleDtos);
        if(200 != baseEntity.getCode()){
            LOGGER.error("批量更新封车信息失败，编码{},原因描述{}",baseEntity.getCode(),baseEntity.getMessage());
            return;
        }

        Map<String, Integer> result = baseEntity.getData();
        LOGGER.error("批量更新封车信息成功，返回信息如下：\n");
        for(String key : result.keySet()){
            LOGGER.error("返回VH Code={}, Message={}", key, result.get(key));
        }
    }
}
