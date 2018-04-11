package com.jd.bluedragon.distribution.external;

import com.jd.bluedragon.distribution.api.response.BoxResponse;
import com.jd.bluedragon.distribution.api.response.WaybillInfoResponse;
import com.jd.bluedragon.distribution.external.service.DmsExternalReadService;
import com.jd.bluedragon.distribution.saf.WaybillSafResponse;
import com.jd.bluedragon.distribution.sorting.domain.OrderDetailEntityResponse;
import com.jd.bluedragon.distribution.sorting.domain.OrdersDetailEntity;
import com.jd.bluedragon.distribution.wss.dto.*;
import com.jd.bluedragon.utils.DateHelper;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.Assert;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DmsExternalReadServiceTest {

    private final static Logger LOGGER = LoggerFactory
            .getLogger(DmsExternalReadServiceTest.class);

    private DmsExternalReadService dmsExternalService;

    @Before
    public void getDmsExternalService() {
        ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(
                "/distribution-web-jsf-client-test.xml");
        dmsExternalService = (DmsExternalReadService) appContext
                .getBean("jsfDmsExternalReadService");
    }

    @Test
    public void testGetBoxSummary() {
//        List<BoxSummaryDto> boxSummaryDtos = dmsExternalService.getBoxSummary("910-21-20150330161521241", 1, 0);
//        LOGGER.error("根据批次号获取箱号数量：{}", boxSummaryDtos.size());
//        for (BoxSummaryDto boxSummaryDto : boxSummaryDtos) {
//            LOGGER.error("根据批次号获取箱号信息：{}", boxSummaryDto.getBoxCode() + "," + boxSummaryDto.getPackagebarNum()
//                    + "," + boxSummaryDto.getWaybillNum());
//        }

        List<BoxSummaryDto> boxSummaryDtos1 = dmsExternalService.getBoxSummary("BC010F001010F61100001001", 2, 20400);
        LOGGER.error("根据箱号获取箱号数量：{}", boxSummaryDtos1.size());
        for (BoxSummaryDto bsd : boxSummaryDtos1) {
            LOGGER.error("根据箱号获取箱号信息：{}", bsd.getBoxCode() + "," + bsd.getPackagebarNum()
                    + "," + bsd.getWaybillNum() + "," + bsd.getSendCode() + "," + bsd.getSendUser());
        }
    }

    @Test
    public void testGetPackageSummary() {
        List<PackageSummaryDto> packSummarys = dmsExternalService.getPackageSummary("910-21-20150330161521241", 1, 0);
        LOGGER.error("根据批次号获取包裹数量：{}", packSummarys.size());
        for (PackageSummaryDto psd : packSummarys) {
            LOGGER.error("根据批次号获取包裹信息：{}", psd.toString());
        }

        List<PackageSummaryDto> packageSummaryDtos = dmsExternalService.getPackageSummary("TC010F511010F644000000251", 2, 909);
        LOGGER.error("根据箱号获取包裹数量：{}", packageSummaryDtos.size());
        for (PackageSummaryDto psdd : packageSummaryDtos) {
            LOGGER.error("根据批次号获取包裹信息：{}", psdd.toString());
        }

    }
    @Test
    public void testGetBoxInfoByCode(){
        BoxResponse response=dmsExternalService.getBoxInfoByCode("TC029F001851F00200006001");
        System.out.println("code is " + response.getCode());
        System.out.println("message is " + response.getMessage());
        System.out.println("box code are " + response.getBoxCode());
        System.out.println("routinfo are " + response.getRouterInfo());
        if(response.getRouterFullId()!=null)
            for(String s:response.getRouterFullId()){
                System.out.println(s);
            }

    }
    @Test
    public void testGetSealsByCode() {
        SealVehicleSummaryDto svsd = dmsExternalService.findSealByCodeSummary("89098789C");
        LOGGER.error("根据封车号获取封车信息：{}", svsd.getCode() + "," + svsd.getCreateUser() + "," + svsd.getVehicleCode()
                + "," + svsd.getDriverCode());
    }

    @Test
    public void testGetDeliveryInfoBySite() {
        Date now = new Date();
        Calendar cal_now = Calendar.getInstance();
        cal_now.setTime(now);
        cal_now.add(Calendar.MONTH, -124);
        LOGGER.info("起始时间：{}，结束时间：{}", DateHelper.formatDateTime(cal_now.getTime()), DateHelper.formatDateTime(now));
        List<WaybillCodeSummatyDto> waybillCodeSummatyDtos = dmsExternalService.findDeliveryPackageBySiteSummary(10, cal_now.getTime(), now);
        for (WaybillCodeSummatyDto dtos : waybillCodeSummatyDtos) {
            LOGGER.error("根据起始时间、收货站点获取发货信息：{}", dtos.getPackagebarNum() + "," + dtos.getWaybillCode());
        }
    }


    @Test
    public void testGetDeliveryPackageByCodeSummary() {
        List<WaybillCodeSummatyDto> waybillCodeSummatyDtos = dmsExternalService.findDeliveryPackageByCodeSummary(10, "170008607");
        for (WaybillCodeSummatyDto dtos : waybillCodeSummatyDtos) {
            LOGGER.error("根据运单、收货站点获取发货信息：{}", dtos.getPackagebarNum() + "," + dtos.getWaybillCode());
        }
    }


    @Test
    public void testGetWaybillsByDeparture() {
        List<DepartureWaybillDto> departureWaybillDtos = dmsExternalService.getWaybillsByDeparture("64", 1);
        for (DepartureWaybillDto dwd : departureWaybillDtos) {
            LOGGER.error("根据封车号获取发车信息：{}", dwd.getWaybillCode());
        }

        List<DepartureWaybillDto> departureWaybillDtos1 = dmsExternalService.getWaybillsByDeparture("1234567890", 2);
        for (DepartureWaybillDto dwd : departureWaybillDtos1) {
            LOGGER.error("根据三方运单号获取发车信息：{}", dwd.getWaybillCode());
        }

    }


    @Test
    public void testGetWaybillInfo() {
        WaybillInfoResponse waybillInfoResponse = dmsExternalService.getWaybillInfo("170001005");
        LOGGER.error("根据运单号获取发货信息：{}", waybillInfoResponse.getCode() + "," + waybillInfoResponse.getJsonData() + "," + waybillInfoResponse.getMessage());
    }

    @Test
    public void testGetOrderDetails() {
        Date endTime = new Date();
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(endTime);
        startCal.add(Calendar.MONTH, -24);
        Date startTime = startCal.getTime();

        OrderDetailEntityResponse orderDetailEntityResponses = dmsExternalService.getOrdersDetails(
                "BC010F002010Y10000033009"
                , DateHelper.formatDateTime(startTime)
                , DateHelper.formatDateTime(endTime)
                , "910"
                , "39");
        LOGGER.error("根据箱号、创建时间范围、分拣中心和收货站点获取分拣数据：{}", orderDetailEntityResponses.getCode() + ","
                + orderDetailEntityResponses.getMessage());
        for (OrdersDetailEntity ode : orderDetailEntityResponses.getData())
            LOGGER.error("根据箱号、创建时间范围、分拣中心和收货站点获取分拣数据：{}", ode.getBoxCode() + "," + ode.getUserName() + "," + ode.getPackNo() + "," + ode.getWaybill());
    }

    @Test
    public void testIsCancel() {
        WaybillSafResponse waybillJsfResponse = dmsExternalService.isCancel("42595675943");
        LOGGER.error("判断运单是否取消、锁定、删除等：{}", waybillJsfResponse.getWaybillCode() + "," + waybillJsfResponse.getCode() + "," + waybillJsfResponse.getMessage());
    }

//    @Test
//    public void testGetOrderDetailsByBoxcode(){
//        WaybillSafResponse<List<WaybillResponse>> waybillSafResponse = dmsExternalService.getOrdersDetailsByBoxcode("BC010F002010Y10000033009");
//        LOGGER.error("根据箱号获取分拣数据:{}", waybillSafResponse.getCode() + "," + waybillSafResponse.getMessage());
//        for(WaybillResponse wr : waybillSafResponse.getData()){
//          LOGGER.error("根据箱号获取分拣数据:{}", wr.getBoxCode() + "," + wr.getPackageCode());
//        }
//    }
//
//    @Test
//    public void testGetOrderDetailsBySendCode(){
//        WaybillSafResponse<List<WaybillResponse>> waybillSafResponse = dmsExternalService.getPackageCodesBySendCode("910-21-20150330161521241");
//        LOGGER.error("根据批次号获取包裹数据:{}", waybillSafResponse.getCode() + "," + waybillSafResponse.getMessage());
//        for(WaybillResponse wr : waybillSafResponse.getData()){
//            LOGGER.error("根据批次号获取包裹数据:{}", wr.getBoxCode() + "," + wr.getPackageCode());
//        }
//    }

    @Test
    public void testfindWaybillByBoxCode() {

        ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(
                "/distribution-web-jsf-client-test.xml");

        DmsExternalReadService service = (DmsExternalReadService) appContext
                .getBean("dmsExternalReadService");
        LOGGER.info("得到调用端代理：{}", service);

        List<String> result = null;
        try {
            result = service
                    .findWaybillByBoxCode("BC010F0010000039000040091");
            for (String waybillCode : result) {
                LOGGER.info(waybillCode);
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        Assert.notEmpty(result);
    }

}
