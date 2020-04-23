package com.jd.bluedragon.distribution.test.departure;

import com.jd.bluedragon.distribution.api.response.SendBoxResponse;
import com.jd.bluedragon.distribution.departure.domain.Departure;
import com.jd.bluedragon.distribution.departure.domain.SendBox;
import com.jd.bluedragon.distribution.departure.domain.SendMeasure;
import com.jd.bluedragon.distribution.departure.service.DepartureService;
import com.jd.etms.waybill.api.WaybillPackageApi;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/distribution-core-context.xml" })
public class DepartureTestCase {
    
    @Autowired
    private DepartureService departureService;
    
    @Autowired
	WaybillPackageApi waybillPackageApi;
    
    public void testQuerySendMeasure() {
        String sendCode = "112";
        SendMeasure sendMeasure = this.departureService.getSendMeasure(1000, sendCode);
        System.out.println("testQuerySendMeasure result: ");
        if (sendMeasure != null) {
            System.out.println(sendMeasure.toString());
        } else {
            System.out.println("can't find result");
        }
    }
    
    public void testCarBoxCodeMatch() {
        String carCode = "carCode55";
        String boxCode = "box0001";
        boolean flag = this.departureService.checkCarBoxCodeMatch(carCode, boxCode);
        System.out.println("testCarBoxCodeMatch result: " + flag);
    }
    
    public void testGetQuerySendBoxInfo() {
        String boxCode = "444444444444445";
        List<SendBox> sendBoxes = this.departureService.getSendBoxInfo(boxCode, null);
        System.out.println("getQuerySendBoxInfo result: ");
        if (sendBoxes != null) {
            for (SendBox sendBox : sendBoxes) {
                System.out.println(sendBox.toString());
            }
        } else {
            System.out.println("can't find result");
        }
    }
    
    public void testCreateDeparture() {
        
        List<String> list = new ArrayList<String>();
        list.add("111");
        list.add("112");
        Departure departure = new Departure();
        departure.setCarCode("carCode111");
        departure.setShieldsCarCode("封签号11");
        departure.setType(Departure.DEPARTRUE_TYPE_ZHIXIAN);
        departure.setVolume(1.22);
        departure.setWeight(1.23);
        
        boolean result = false;
        try {
            // result = departureService.createDeparture(departure);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("createDeparture result: " + result);
    }
    
    public static void main(String args[]) {
        RestTemplate template = new RestTemplate();
        
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            String url = "http://localhost:8080/dms/services/departure/sendbox/22";
            
            SendBoxResponse response = template.getForObject(url, SendBoxResponse.class,
                    "11001100527248769");
            
            System.out.println("id is " + response);
        }
        long end = System.currentTimeMillis();
        System.out.println((end - startTime) / 1000);
    }
    
    @Test
    public void testWaybill() {
        List<String> requests = new ArrayList<String>();
        BaseEntity<List<DeliveryPackageD>> waybillWSRs;
        List<DeliveryPackageD> datas = null;
        requests.add("11324139-1-1");
        try {
            waybillWSRs = this.waybillPackageApi.queryPackageListForParcodes(requests);
            datas = waybillWSRs.getData();
            System.out.println("调用运单queryPackageListForParcodes结束");
        } catch (Exception e) {
            System.out.println("调用运单queryPackageListForParcodes接口时候失败");
        }
        if (datas != null && datas.size() != 0) {
            for (DeliveryPackageD deliveryPackageD : datas) {
                System.out.println("PackageBarcode: " + deliveryPackageD.getPackageBarcode()
                        + ",GoodWeight:" + deliveryPackageD.getGoodWeight());
            }
        }
    }
    
}
