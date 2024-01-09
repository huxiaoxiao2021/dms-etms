package com.jd.bluedragon.distribution.exception;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.jyexpection.request.ExpDamageDetailReq;
import com.jd.bluedragon.common.dto.jyexpection.response.Consumable;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyExceptionDamageEnum;
import com.jd.bluedragon.distribution.jy.dao.exception.JyDamageConsumableDao;
import com.jd.bluedragon.distribution.jy.exception.JyDamageConsumableEntity;
import com.jd.bluedragon.distribution.jy.service.exception.JyDamageExceptionService;
import com.jd.bluedragon.utils.JsonHelper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:distribution-web-context.xml"})
@Slf4j
public class JyDamageExceptionServiceTest {

    @Autowired
    private JyDamageExceptionService jyDamageExceptionService;
    private ExpDamageDetailReq req;

    @Autowired
    private JyDamageConsumableDao jyDamageConsumableDao;

    /*
    [{"actualImageUrlList":["https://s3-internal.cn-north-1.jdcloud-oss.com/dmsweb/dms-feedback/74952b05-ef57-424f-ba1a-db252ab6477c.jpg?AWSAccessKeyId=JDC_D7F39DBD95C716540108BD9333F4&Expires=1732000943&Signature=6YY0aMpV2K%2BzCOsJVX3pXpSt5DI%3D","https://s3-internal.cn-north-1.jdcloud-oss.com/dmsweb/dms-feedback/e17d7474-d7d9-4f20-a039-de3319afaba5.jpg?AWSAccessKeyId=JDC_D7F39DBD95C716540108BD9333F4&Expires=1732000965&Signature=POZOdK57%2BuSbHpRVx%2Fl2hxaDcSk%3D","https://s3-internal.cn-north-1.jdcloud-oss.com/dmsweb/dms-feedback/0adcffcd-8604-4751-a778-879a57d56eb5.jpg?AWSAccessKeyId=JDC_D7F39DBD95C716540108BD9333F4&Expires=1732000970&Signature=5DZBtZMzJpC4hXXFyZjmepge9N0%3D"],
    "bizId":"JD0003422717790_40240","consumables":[{"code":0},{"code":5}],"damageType":1,
    "positionCode":"GW00006005","repairType":2,"saveType":0,"siteId":910,"userErp":"wuyoude","volumeRepairBefore":11.00,"weightRepairBefore":5.00}]
     */
    @Before
    public void init() {
//        req = new ExpDamageDetailReq();
//        req.setBizId("testclf001");
//        req.setUserErp("bjxings");
//        req.setSiteId(40240);
//
//        Consumable consumable1 = new Consumable();
//        consumable1.setBarcode("06012001");
//        consumable1.setCode(1);
//        consumable1.setName("纸箱");
//        Consumable consumable2 = new Consumable();
//        consumable2.setBarcode("09042012");
//        consumable2.setCode(6);
//        consumable2.setName("温控包装");
//        List<Consumable> consumables = new ArrayList<>();
//        consumables.add(consumable1);
//        consumables.add(consumable2);
//        req.setConsumables(consumables);
//
//        req.setDamageType(1);
//        req.setRepairType(2);
//        req.setPositionCode("GW00010001");
    }

    @Test
    public void processTaskOfDamage() {
        ExpDamageDetailReq req = new ExpDamageDetailReq();
        req.setBizId("JD0003422717790_40240");
        req.setDamageType(1);
        req.setPositionCode("GW00006005");
        req.setRepairType(2);
        req.setSaveType(0);
        req.setSiteId(910);
        req.setUserErp("wuyoude");
        req.setVolumeRepairBefore(new BigDecimal(11.00));
        req.setWeightRepairBefore(new BigDecimal(5.00));
        Consumable consumable = new Consumable();
        consumable.setCode(0);
        List<Consumable> consumables = new ArrayList<>();
        consumables.add(consumable);
        req.setConsumables(consumables);
        jyDamageExceptionService.processTaskOfDamage(req);
    }

    @Test
    public void getConsumables() {

        JdCResponse<List<com.jd.bluedragon.distribution.jy.dto.Consumable>> consumables = jyDamageExceptionService.getConsumables();

        System.out.println(JsonHelper.toJson(consumables));
    }

    @Test
    public void damageConsumableDao(){
//        List<JyDamageConsumableEntity> jyDamageConsumableEntities = jyDamageConsumableDao.selectByBizId("testclf001");
//        System.out.println(JsonHelper.toJson(jyDamageConsumableEntities));

    }

    @Test
    public void insertDamageConsumable(){
        List<JyDamageConsumableEntity> jyDamageConsumableEntities = jyDamageConsumableDao.selectByBizId("testclf001");
        System.out.println(JsonHelper.toJson(jyDamageConsumableEntities));

    }
}
