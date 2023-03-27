package com.jd.bluedragon.distribution.dock.service;

import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import com.jd.bluedragon.distribution.dock.dao.DockBaseInfoDao;
import com.jd.bluedragon.distribution.dock.entity.AllowedVehicleEntity;
import com.jd.bluedragon.distribution.dock.entity.DockInfoEntity;
import com.jd.bluedragon.distribution.dock.entity.DockPageQueryCondition;
import com.jd.bluedragon.distribution.dock.enums.DockAttributeEnums;
import com.jd.bluedragon.distribution.dock.enums.DockTypeEnums;
import com.jd.bluedragon.utils.JsonHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.dock.service
 * @ClassName: DockInfoJsfServiceImplTest
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2021/12/2 15:18
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-core-context-test.xml")
public class DockInfoJsfServiceImplTest {

    @Autowired
    @Qualifier("dockInfoJsfService")
    private DockInfoJsfServiceImpl dockInfoJsfService;

    @Autowired
    private DockBaseInfoDao dockBaseInfoDao;

    @Test
    public void getAllowedVehicleTypeEnums() {

        System.out.println(dockInfoJsfService.getAllowedVehicleTypeEnums());
    }


    @Test
    public void saveDockInfo() {
        /*
         *  System.out.println(dockInfoJsfService.getAllowedVehicleTypeEnums());
         *  {"allowedVehicleTypes":["code1"],"createTime":null,"createUserName":"","dockAttribute":"1","dockCode":"001","dockType":"1","hasDockLeveller":false,"hasScales":false,"height":1.0,"id":0,"immediately":false,"isDelete":0,"orgId":3,"orgName":"","siteCode":480,"siteName":"","ts":null,"updateTime":null,"updateUserName":""}
         *
         */
        DockInfoEntity entity = new DockInfoEntity();
        entity.setDockCode("001");
        entity.setSiteCode(480);
        entity.setSiteName("南京配送中心B");
        entity.setOrgId(3);
        entity.setOrgName("上海分公司");
        entity.setDockType(DockTypeEnums.artery);
        entity.setDockAttribute(DockAttributeEnums.load);
        entity.setIsHasDockLeveller(1);
        entity.setIsHasScales(1);
        entity.setIsImmediately(1);
        entity.setHeight(1.09D);
        AllowedVehicleEntity entity1 = new AllowedVehicleEntity();
        entity1.setCode("CODE-1");
        entity1.setName("NAME-1");
        entity.setAllowedVehicleTypes(Collections.singletonList(entity1));
        entity.setCreateUserName("wzx");
        entity.setIsDelete((byte) 1);
        System.out.println(JsonHelper.toJson(dockInfoJsfService.saveDockInfo(entity)));

    }

    @Test
    public void queryDockInfoByPage() {
        DockPageQueryCondition condition = new DockPageQueryCondition();
        condition.setSiteCode(480);
        condition.setOrgId(3);
        System.out.println(JsonHelper.toJson(dockInfoJsfService.queryDockInfoByPage(condition)));

        DockPageQueryCondition condition1 = new DockPageQueryCondition();
        condition1.setSiteCodeList(Collections.singletonList(480));
        condition1.setOrgId(3);
        System.out.println(JsonHelper.toJson(dockInfoJsfService.queryDockInfoByPage(condition1)));
    }

    @Test
    public void deleteDockInfoById() {
        DockInfoEntity entity = new DockInfoEntity();
        entity.setId(1L);
        entity.setUpdateUserName("WZX");
        System.out.println(JsonHelper.toJson(dockInfoJsfService.deleteDockInfoById(entity)));
    }

    @Test
    public void updateDockInfoById() {
        DockInfoEntity entity = new DockInfoEntity();
        entity.setId(1L);
        entity.setDockCode("001");
        entity.setSiteCode(480);
        entity.setSiteName("南京配送中心B");
        entity.setOrgId(3);
        entity.setOrgName("上海分公司");
        entity.setDockType(DockTypeEnums.artery);
        entity.setDockAttribute(DockAttributeEnums.load);
        entity.setIsHasDockLeveller(1);
        entity.setIsHasScales(1);
        entity.setIsImmediately(1);
        entity.setHeight(2.1D);
        AllowedVehicleEntity entity1 = new AllowedVehicleEntity();
        entity1.setCode("CODE-1");
        entity1.setName("NAME-1");
        entity.setAllowedVehicleTypes(Collections.singletonList(entity1));
        entity.setCreateUserName("wzx");
        entity.setIsDelete((byte) 0);
        System.out.println(JsonHelper.toJson(dockInfoJsfService.updateDockInfoById(entity)));

    }

    @Test
    public void findById() {
        System.out.println(JsonHelper.toJson(dockBaseInfoDao.findById(1L)));
    }

    @Test
    public void testQueryDockListBySiteId() {
        Integer siteCode = 10186;
        DockInfoEntity entity = new DockInfoEntity();
        entity.setSiteCode(siteCode);
        Response<List<String>> response = dockInfoJsfService.queryDockListBySiteId(entity);
        Assert.assertNotNull(response);
    }
    
}