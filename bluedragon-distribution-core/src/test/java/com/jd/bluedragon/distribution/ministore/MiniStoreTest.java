package com.jd.bluedragon.distribution.ministore;

import com.jd.bluedragon.distribution.ministore.dao.MiniStoreBindRelationDao;
import com.jd.bluedragon.distribution.ministore.domain.MiniStoreBindRelation;
import com.jd.bluedragon.distribution.ministore.dto.DeviceDto;
import com.jd.bluedragon.distribution.ministore.service.MiniStoreService;
import com.jd.bluedragon.distribution.ministore.service.impl.MiniStoreServiceImpl;
import com.jd.bluedragon.distribution.seal.dao.SealBoxDao;
import com.jd.bluedragon.distribution.seal.domain.SealBox;
import com.jd.bluedragon.distribution.sorting.dao.SortingDao;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.utils.BeanUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.validation.constraints.AssertTrue;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-core-context-dev.xml")
public class MiniStoreTest {
    @Autowired
    MiniStoreBindRelationDao miniStoreBindRelationDao;
    @Autowired
    MiniStoreService miniStoreService;
    @Test
    public void validatDeviceBindStatusTest(){
        DeviceDto deviceDto =new DeviceDto();
        deviceDto.setStoreCode("s2");
        deviceDto.setBoxCode("b2");
        deviceDto.setIceBoardCode1("i5");
        deviceDto.setIceBoardCode2("i3");
        Boolean f = miniStoreService.validatDeviceBindStatus(deviceDto);
        Assert.assertTrue(true==f);
    }


    @Test
    public void validatInsert(){
        DeviceDto deviceDto =new DeviceDto();
        deviceDto.setStoreCode("s2");
        deviceDto.setBoxCode("b2");
        deviceDto.setIceBoardCode1("i5");
        deviceDto.setIceBoardCode2("i3");
        deviceDto.setCreateUser("weixiaofeng12");
        deviceDto.setCreateUserCode(1L);
        Boolean f = miniStoreService.bindMiniStoreDevice(deviceDto);
        Assert.assertTrue(true==f);
    }

    @Test
    public void BeanCopyTest(){
        Long start =System.currentTimeMillis();
        for (int i=0;i<100000;i++){
            DeviceDto deviceDto =new DeviceDto();
            deviceDto.setStoreCode("s2");
            deviceDto.setBoxCode("b2");
            deviceDto.setIceBoardCode1("i5");
            deviceDto.setIceBoardCode2("i3");
            deviceDto.setCreateUser("weixiaofeng12");
            deviceDto.setCreateUserCode(1L);
            MiniStoreBindRelation miniStoreBindRelation =BeanUtils.copy(deviceDto, MiniStoreBindRelation.class);
        }
        System.out.println("耗时："+(System.currentTimeMillis()-start)+"ms");
        //耗时：17564ms
        //耗时：18860ms
        //耗时：19910ms
        //vs
        //耗时：3422ms 耗时：4360ms 耗时：3316ms 差一个数量级
    }

    @Test
    public void unBoxTest(){
        DeviceDto deviceDto =new DeviceDto();
        deviceDto.setMiniStoreBindRelationId(2L);
        deviceDto.setBoxCode("JDVF00001693352-7-15-");
        Boolean r =miniStoreService.updateProcessStatusAndInvaliSortRealtion(deviceDto);
        System.out.println("解封箱结果："+r);
    }
}
