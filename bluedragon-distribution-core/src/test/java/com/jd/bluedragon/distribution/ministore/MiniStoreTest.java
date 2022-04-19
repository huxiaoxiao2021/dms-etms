package com.jd.bluedragon.distribution.ministore;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jd.bluedragon.distribution.ministore.dao.MiniStoreBindRelationDao;
import com.jd.bluedragon.distribution.ministore.domain.MiniStoreBindRelation;
import com.jd.bluedragon.distribution.ministore.dto.DeviceDto;
import com.jd.bluedragon.distribution.ministore.dto.QueryTaskDto;
import com.jd.bluedragon.distribution.ministore.enums.BizDirectionEnum;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
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
    public void BizDirectionEnumTest() {
       Integer a =10;
       if (BizDirectionEnum.FROWARD.getCode().equals(a)){
           System.out.println("正向流程");
       }
    }

    @Test
    public void ThreadPoolTest() {
        Runnable runnable =new Runnable() {
            @Override
            public void run() {
                System.out.println(1);
            }
        };
        //taskExecutor.execute(runnable);
    }

    @Test
    public void validatDeviceBindStatusTest() {
        DeviceDto deviceDto = new DeviceDto();
        deviceDto.setStoreCode("s2");
        deviceDto.setBoxCode("b2");
        deviceDto.setIceBoardCode1("i5");
        deviceDto.setIceBoardCode2("i3");
        Boolean f = miniStoreService.validatDeviceBindStatus(deviceDto);
        Assert.assertTrue(true == f);
    }


    @Test
    public void validatInsert() {
        DeviceDto deviceDto = new DeviceDto();
        deviceDto.setStoreCode("s2");
        deviceDto.setBoxCode("b2");
        deviceDto.setIceBoardCode1("i5");
        deviceDto.setIceBoardCode2("i3");
        deviceDto.setCreateUser("weixiaofeng12");
        deviceDto.setCreateUserCode(1L);
        Boolean f = miniStoreService.bindMiniStoreDevice(deviceDto);
        Assert.assertTrue(true == f);
    }

    @Test
    public void BeanCopyXingNengTest() {
        Long start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            DeviceDto deviceDto = new DeviceDto();
            deviceDto.setStoreCode("s2");
            deviceDto.setBoxCode("b2");
            deviceDto.setIceBoardCode1("i5");
            deviceDto.setIceBoardCode2("i3");
            deviceDto.setCreateUser("weixiaofeng12");
            deviceDto.setCreateUserCode(1L);
            MiniStoreBindRelation miniStoreBindRelation = BeanUtils.copy(deviceDto, MiniStoreBindRelation.class);
        }
        System.out.println("耗时：" + (System.currentTimeMillis() - start) + "ms");
        //耗时：17564ms
        //耗时：18860ms
        //耗时：19910ms
        //vs
        //耗时：3422ms 耗时：4360ms 耗时：3316ms 差一个数量级
    }

    @Test
    public void unBoxTest() {
        DeviceDto deviceDto = new DeviceDto();
        deviceDto.setMiniStoreBindRelationId(2L);
        deviceDto.setBoxCode("JDVF00001693352-7-15-");
        deviceDto.setCreateSiteCode(910L);
        Boolean r = miniStoreService.updateProcessStatusAndInvaliSortRealtion(deviceDto);
        System.out.println("解封箱结果：" + r);
    }

    @Test
    public void listBindData() {
        QueryTaskDto dto = new QueryTaskDto();
        dto.setCreateUserCode(1L);
        PageHelper.startPage(1, 2);
        List<MiniStoreBindRelation> miniStoreBindRelationList =miniStoreService.queryBindAndNoSortTaskList(dto);
        System.out.println("绑定数据：" + miniStoreBindRelationList.get(1));
    }

    @Test
    public void unbind() {
        boolean rs =miniStoreService.unBind(1L,1L,"weixiaofeng12");
        System.out.println("解绑结果：" +rs);
    }

    @Test
    public void validateSortRelationTest() {
        boolean rs =miniStoreService.validateSortRelation("BC1001191120330000012905","JDV000049314581-1-0-",10512);
        System.out.println("集包关系校验：" +rs);
    }

    @Test
    public void incrSortCountTest() {
        int rs =    miniStoreService.incrSortCount(19L, "吴有德", 17331L);
        System.out.println("校验结果：" +rs);
    }

    @Test
    public void updateByIdTest() {
        MiniStoreBindRelation m =new MiniStoreBindRelation();
        m.setId(5L);
        int rs =    miniStoreService.updateById(m);
        System.out.println("校验结果：" +rs);
    }
}
