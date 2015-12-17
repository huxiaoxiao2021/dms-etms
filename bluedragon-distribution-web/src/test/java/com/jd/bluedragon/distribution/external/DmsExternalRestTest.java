package com.jd.bluedragon.distribution.external;

import com.jd.bluedragon.distribution.wss.dto.*;
import com.jd.bluedragon.utils.DateHelper;
import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.util.GenericType;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author dudong
 * @date 2015/12/7
 */


public class DmsExternalRestTest extends TestCase{
    private static final String DMS_EXTERNAL_URL = "http://dmswebtest.360buy.com/services/external/";
    private static final Log LOGGER = LogFactory.getLog(DmsExternalRestTest.class);
    @Test
    public void testGetBoxSummary() throws Exception{
//        List<BoxSummaryDto> boxSummaryDtos = dmsExternalService.getBoxSummary("910-21-20150330161521241", 1, 0);
//        LOGGER.error("根据批次号获取箱号数量：{}", boxSummaryDtos.size());
//        for (BoxSummaryDto boxSummaryDto : boxSummaryDtos) {
//            LOGGER.error("根据批次号获取箱号信息：{}", boxSummaryDto.getBoxCode() + "," + boxSummaryDto.getPackagebarNum()
//                    + "," + boxSummaryDto.getWaybillNum());
//        }
        String url = DMS_EXTERNAL_URL + "getBoxSummary/51676568811-90-100-/" + "2/" + "39";
        ClientRequest request = new ClientRequest(url);
        request.accept(MediaType.APPLICATION_JSON);
        ClientResponse<List<BoxSummaryDto>> boxSummaryDtos1 = request.get(new GenericType<List<BoxSummaryDto>>(){});
        LOGGER.error("根据箱号获取箱号数量：{}" + boxSummaryDtos1.getEntity().size());
        for (BoxSummaryDto bsd : boxSummaryDtos1.getEntity()) {
            LOGGER.error("根据箱号获取箱号信息：{}" + bsd.getBoxCode() + "," + bsd.getPackagebarNum()
                    + "," + bsd.getWaybillNum() + "," + bsd.getSendCode() + "," + bsd.getSendUser());
        }
    }

    @Test
    public void testGetPackageSummary() throws Exception {
        String url = DMS_EXTERNAL_URL + "getPackageSummary/51676568811-90-100-/" + "2/" + "39";
        ClientRequest request = new ClientRequest(url);
        request.accept(MediaType.APPLICATION_JSON);
        ClientResponse<List<PackageSummaryDto>> packSummarys = request.get(new GenericType<List<PackageSummaryDto>>() {
        });
        LOGGER.error("根据批次号获取包裹数量：{}" + packSummarys.getEntity().size());
        for (PackageSummaryDto psd : packSummarys.getEntity()) {
            LOGGER.error("根据批次号获取包裹信息：{}" + psd.getWaybillCode() + "," + psd.getSendCode());
        }
        url = DMS_EXTERNAL_URL + "getPackageSummary/TC010F511010F644000000251/" + "2/" + "909";
        ClientResponse<List<PackageSummaryDto>> packageSummaryDtos = request.get(new GenericType<List<PackageSummaryDto>>() {
        });
        LOGGER.error("根据箱号获取包裹数量：{}" + packageSummaryDtos.getEntity().size());
        for (PackageSummaryDto psdd : packageSummaryDtos.getEntity()) {
            LOGGER.error("根据批次号获取包裹信息：{}" + psdd.getWaybillCode() + "," + psdd.getSendCode());
        }

    }

    @Test
    public void testGetSealsByCode() throws Exception{
        String url = DMS_EXTERNAL_URL + "findSealByCodeSummary/90358362C";
        ClientRequest request = new ClientRequest(url);
        request.accept(MediaType.APPLICATION_JSON);
        ClientResponse<SealVehicleSummaryDto> packSummarys = request.get(new GenericType<SealVehicleSummaryDto>(){});
        SealVehicleSummaryDto svsd = packSummarys.getEntity();
        LOGGER.error("根据封车号获取封车信息：{}" + svsd.getCode() + "," + svsd.getCreateUser() + "," + svsd.getVehicleCode()
                + "," + svsd.getDriverCode());
    }

    @Test
    public void testGetDeliveryInfoBySite() throws Exception{
        Date now = new Date();
        Calendar cal_now = Calendar.getInstance();
        cal_now.setTime(now);
        cal_now.add(Calendar.MONTH, -1024);
        LOGGER.info("起始时间：{}，结束时间：{}" + DateHelper.formatDateTime(cal_now.getTime())  + DateHelper.formatDateTime(now));
        String url = DMS_EXTERNAL_URL + "findDeliveryPackageBySiteSummary/39/" + DateHelper.formatDateTime(cal_now.getTime()) + "/" + DateHelper.formatDateTime(now);
        ClientRequest request = new ClientRequest(url);
        request.accept(MediaType.APPLICATION_JSON);
        ClientResponse<List<WaybillCodeSummatyDto>> waybillCodeSummatyDtos = request.get(new GenericType<List<WaybillCodeSummatyDto>>(){});
        for (WaybillCodeSummatyDto dtos : waybillCodeSummatyDtos.getEntity()) {
            LOGGER.error("根据起始时间、收货站点获取发货信息：{}" + dtos.getPackagebarNum() + "," + dtos.getWaybillCode());
        }
    }


    @Test
    public void testGetDeliveryPackageByCodeSummary() throws Exception{
        String url = DMS_EXTERNAL_URL + "findDeliveryPackageByCodeSummary/39/52641427582";
        ClientRequest request = new ClientRequest(url);
        request.accept(MediaType.APPLICATION_JSON);
        ClientResponse<List<WaybillCodeSummatyDto>> waybillCodeSummatyDtos = request.get(new GenericType<List<WaybillCodeSummatyDto>>(){});
        for (WaybillCodeSummatyDto dtos : waybillCodeSummatyDtos.getEntity()) {
            LOGGER.error("根据运单、收货站点获取发货信息：{}" + dtos.getPackagebarNum() + "," + dtos.getWaybillCode());
        }
    }


    @Test
    public void testGetWaybillsByDeparture() throws Exception{
        String url = DMS_EXTERNAL_URL + "getWaybillsByDeparture/62/1";
        ClientRequest request = new ClientRequest(url);
        request.accept(MediaType.APPLICATION_JSON);
        ClientResponse<List<DepartureWaybillDto>> departureWaybillDtos = request.get(new GenericType<List<DepartureWaybillDto>>(){});
        for (DepartureWaybillDto dwd : departureWaybillDtos.getEntity()) {
            LOGGER.error("根据封车号获取发车信息：{}" + dwd.getWaybillCode());
        }
        url = DMS_EXTERNAL_URL + "getWaybillsByDeparture/1234567890/2";
        ClientResponse<List<DepartureWaybillDto>> departureWaybillDtos1 = request.get(new GenericType<List<DepartureWaybillDto>>(){});
        for (DepartureWaybillDto dwd : departureWaybillDtos1.getEntity()) {
            LOGGER.error("根据三方运单号获取发车信息：{}" + dwd.getWaybillCode());
        }

    }



}
