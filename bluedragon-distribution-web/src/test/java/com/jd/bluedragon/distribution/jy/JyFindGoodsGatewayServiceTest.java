package com.jd.bluedragon.distribution.jy;

import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.inventory.*;
import com.jd.bluedragon.common.dto.inventory.enums.InventoryDetailTypeEnum;
import com.jd.bluedragon.common.dto.inventory.enums.InventoryListTypeEnum;
import com.jd.bluedragon.external.gateway.service.JyFindGoodsGatewayService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 解封车单测
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2023-03-07 10:19:35 周二
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:distribution-web-context.xml")
public class JyFindGoodsGatewayServiceTest {


    @Autowired
    private JyFindGoodsGatewayService jyFindGoodsGatewayService;


    private static final CurrentOperate CURRENT_OPERATE = new CurrentOperate(910,"马驹桥分拣中心",new Date());
    public static final CurrentOperate SITE_40240 = new CurrentOperate(40240, "北京通州分拣中心", new Date());

    public static final String POST = "GW00150001";
    public static final String GROUP_CODE = "G00000059567";

    public static final User USER_wuyoude = new User(17331,"吴有德");
    static {
        USER_wuyoude.setUserErp("wuyoude");
    }

    @Test
    public void getMixScanTaskDefaultNameTest() {
        InventoryTaskQueryReq req = new InventoryTaskQueryReq();
        req.setCurrentOperate(CURRENT_OPERATE);
        req.setUser(USER_wuyoude);
        req.setGroupCode(GROUP_CODE);
        req.setPositionCode(POST);
        while (true) {
            try {
                JdCResponse<InventoryTaskDto> obj1 = jyFindGoodsGatewayService.findCurrentInventoryTask(req);
                System.out.println("end");
                req.setBizId(obj1.getData().getBizId());
//                JdCResponse<InventoryTaskDto> obj2 = jyFindGoodsGatewayService.findInventoryTaskByBizId(req);
                System.out.println("end");
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    @Test
    public void findInventoryTaskListPageTest() {
        InventoryTaskListQueryReq req = new InventoryTaskListQueryReq();
        req.setCurrentOperate(CURRENT_OPERATE);
        req.setUser(USER_wuyoude);
        req.setGroupCode(GROUP_CODE);
        req.setPositionCode(POST);

        req.setPageNo(1);
        req.setPageSize(20);

        for(int i = 0; i<10; i++) {
            if(i % 2 ==0) {
                req.setOnlyHistoryComplete(true);
                req.setQueryDays(15);
            }else {
                req.setQueryDays(0);
                req.setOnlyHistoryComplete(false);
            }
            Object obj = jyFindGoodsGatewayService.findInventoryTaskListPage(req);
            System.out.println("end");
        }
    }



    @Test
    public void inventoryTaskStatisticsTest() {
        InventoryTaskStatisticsReq req = new InventoryTaskStatisticsReq();
        req.setCurrentOperate(CURRENT_OPERATE);
        req.setUser(USER_wuyoude);
        req.setGroupCode(GROUP_CODE);
        req.setPositionCode(POST);
        while (true) {
            try {
                JdCResponse<InventoryTaskStatisticsRes> obj1 = jyFindGoodsGatewayService.inventoryTaskStatistics(req);
                System.out.println("end");
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Test
    public void inventoryTaskPhotographTest() {
        String str=
                "    {\n" +
                "        \"bizId\": \"test009\",\n" +
                "        \"currentOperate\": {\n" +
                "            \"dmsCode\": \"010F002\",\n" +
                "            \"operateTime\": 1690168647531,\n" +
                "            \"operatorId\": \"54091\",\n" +
                "            \"operatorTypeCode\": 1,\n" +
                "            \"orgId\": 6,\n" +
                "            \"orgName\": \"华北\",\n" +
                "            \"siteCode\": 40240,\n" +
                "            \"siteName\": \"北京通州分拣中心\"\n" +
                "        },\n" +
                "        \"groupCode\": \"G00000047003\",\n" +
                "        \"photoPosition\": 1,\n" +
                "        \"photoUrls\": [\n" +
                "            \"http://s3.cn-north-1.jdcloud-oss.com/dmsweb/dms-feedback/3597077a-f2e4-473f-9739-7efd406f11fa.jpg?AWSAccessKeyId=JDC_D7F39DBD95C716540108BD9333F4&Expires=1721704647&Signature=5sW%2BQ5Tibj%2BQtaDDhFiPb1gMnv8%3D\"\n" +
                "        ],\n" +
                "        \"positionCode\": \"GW00019001\",\n" +
                "        \"requestId\": \"02bfa0d925974744a920ce74ed029dc8\",\n" +
                "        \"user\": {\n" +
                "            \"userCode\": 17331,\n" +
                "            \"userErp\": \"wuyoude\",\n" +
                "            \"userName\": \"吴有德\"\n" +
                "        }\n" +
                "    }" ;


        InventoryTaskPhotographReq param = JSONObject.parseObject(str, InventoryTaskPhotographReq.class);
        JdCResponse obj0 = jyFindGoodsGatewayService.inventoryTaskPhotograph(param);
        System.out.println(obj0.getCode());

        InventoryTaskPhotographReq req = new InventoryTaskPhotographReq();
        req.setCurrentOperate(CURRENT_OPERATE);
        req.setUser(USER_wuyoude);
        req.setGroupCode(GROUP_CODE);
        req.setPositionCode(POST);

        req.setBizId("test009");
        req.setPhotoPosition(0);
        List<String> url = Arrays.asList("url1","url2","url3","url4","url5");
        req.setPhotoUrls(url);
        JdCResponse obj1 = jyFindGoodsGatewayService.inventoryTaskPhotograph(req);
        for(int i = 0; i<10000; i++) {
            try {

                req.setPhotoPosition(i % 4 + 1);
                List<String> urls = Arrays.asList("url1","url2","url3","url4","url5");
                req.setPhotoUrls(urls);
                JdCResponse obj2 = jyFindGoodsGatewayService.inventoryTaskPhotograph(req);
                if(!obj2.isSucceed()) {
                    System.out.println("fail");
                }
                System.out.println("end");
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Test
    public void findInventoryDetailPageTest() {
        InventoryDetailQueryReq req = new InventoryDetailQueryReq();
        req.setCurrentOperate(CURRENT_OPERATE);
        req.setUser(USER_wuyoude);
        req.setGroupCode(GROUP_CODE);
        req.setPositionCode(POST);
        req.setBizId("test009");
        req.setPageNo(1);
        req.setPageSize(20);
        for(int i = 0; i<10000; i++) {
            try {
                req.setInventoryListType(InventoryListTypeEnum.NEED_FIND.getCode());
                req.setInventoryDetailType(InventoryDetailTypeEnum.WAIT_SEND.getCode());
                JdCResponse obj1 = jyFindGoodsGatewayService.findInventoryDetailPage(req);
                System.out.println("end");
                req.setInventoryDetailType(InventoryDetailTypeEnum.NULL_NEXT.getCode());
                JdCResponse obj2 = jyFindGoodsGatewayService.findInventoryDetailPage(req);
                System.out.println("end");

                req.setInventoryListType(InventoryListTypeEnum.FOUND.getCode());
                req.setInventoryDetailType(InventoryDetailTypeEnum.WAIT_SEND.getCode());
                JdCResponse obj3 = jyFindGoodsGatewayService.findInventoryDetailPage(req);
                System.out.println("end");
                req.setInventoryDetailType(InventoryDetailTypeEnum.NULL_NEXT.getCode());
                JdCResponse obj4 = jyFindGoodsGatewayService.findInventoryDetailPage(req);
                System.out.println("end");
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}

