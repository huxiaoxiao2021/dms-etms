package com.jd.bluedragon.distribution.test.inspection.rest;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import com.jd.bluedragon.distribution.api.request.InspectionECRequest;
import com.jd.bluedragon.distribution.api.request.InspectionRequest;
import com.jd.bluedragon.distribution.api.response.InspectionECResponse;
import com.jd.bluedragon.distribution.api.response.PackageResponse;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/distribution-core-context.xml" })
public class InspectionResourceTestCase {

    String urlRoot = "http://localhost:8989/services/";

    //String urlRoot = "http://10.10.236.45:24050/services/";
    //String urlRoot = "http://192.168.200.202:8080/services/";
    //@Test
    public void testInspectionThridParty() {

        /*String json = "" + "{" 
        		+ "\"shieldsBarcode\":\"dd\","
        		+ "\"boxCode:\"B909-30-38246717\","
        		+ "\"packageBarcode\":\"15868035-1-1\","
        		+ "\"businessType\":\"11\"," 
        		+ "\"exceptionType\":\"111\","
        		+ "\"userName\":\"zhangsan\"," 
        		+ "\"userCode\":\"验货人code\","
        		+ "\"siteCode\":\"操作单位Code\","
        		+ "\"receiveUnitCode\":\"接收单位Code\"" + "}";*/
        InspectionRequest requestBean = new InspectionRequest();
        requestBean.setBoxCode("154604803-1-2-1");
        requestBean.setBusinessType(10);
        requestBean.setExceptionType("ee");
        requestBean.setPackageBarcode("4324N232S2");
        requestBean.setReceiveSiteCode(111);
        requestBean.setSealBoxCode("55");
        requestBean.setSiteCode(111);
        requestBean.setSiteName("dd");
        requestBean.setUserCode(333);
        requestBean.setUserName("3");
        String url = this.urlRoot + "inspection/thirdParty/";
        RestTemplate template = new RestTemplate();

        try {
            String re = template.postForObject(url, requestBean, String.class);
            System.out.println("re=" + re);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void testInspectionExceptionQuery() {
        InspectionECRequest inspectionECRequest = new InspectionECRequest();
        inspectionECRequest.setSiteCode(11002);

        //inspectionECRequest.setInspectionECType(2);
        //http://10.10.236.45:24050/inspection/exceptionQuery/{"siteCode":"12","inspectionECType":null}
        String url = this.urlRoot + "inspection/exceptionQuery";
        RestTemplate template = new RestTemplate();

        try {
            InspectionECResponse response = template.postForObject(url, inspectionECRequest,
                    InspectionECResponse.class);
            List<PackageResponse> list = response.getLists();

            for (PackageResponse responseBean : list) {
                System.out.println(responseBean.getPackageBarcode() + "----");
            }
            System.out.println("re=" + response.getLists().size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void exceptionDispose() {
        InspectionECRequest inspectionECRequest = new InspectionECRequest();
        inspectionECRequest.setOperationType(6);
        List<InspectionRequest> requestLists = new ArrayList<InspectionRequest>();
        InspectionRequest packageRequest1 = new InspectionRequest();
        packageRequest1.setPackageBarcode("620000000001N2S3H3");
        packageRequest1.setWaybillCode("620000000001");
        packageRequest1.setReceiveSiteCode(10);
        packageRequest1.setBoxCode("B010F001010F00200000002");
        packageRequest1.setSiteCode(111);
        requestLists.add(packageRequest1);
       
        /*InspectionRequest packageRequest2 = new InspectionRequest();
        packageRequest2.setPackageBarcode("970000000001N2S3H7");
        packageRequest2.setWaybillCode("970000000001");
        packageRequest2.setReceiveSiteCode(99);
        packageRequest2.setBoxCode("B027Z001027F00100001001");
        packageRequest2.setSiteCode(100);
        requestLists.add(packageRequest2);*/

        inspectionECRequest.setPackages(requestLists);

        RestTemplate template = new RestTemplate();

        String url = this.urlRoot + "inspection/exception/dispose";
        try {
            String re = template.postForObject(url, inspectionECRequest, String.class);
            System.out.println("re=" + re);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //@Test
    public void getWaybill() {
        RestTemplate template = new RestTemplate();
        String url = this.urlRoot + "inspection/getWaybillPackage/1234567890/1212";
        try {
            String re = template.getForObject(url, String.class, String.class);
            System.out.println("re=" + re);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
