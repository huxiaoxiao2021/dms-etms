package com.jd.bluedragon.distribution.rest.inspection;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import com.jd.bluedragon.distribution.api.request.InspectionECRequest;
import com.jd.bluedragon.distribution.api.request.InspectionRequest;
import com.jd.bluedragon.distribution.api.response.HandoverResponse;
import com.jd.bluedragon.distribution.api.response.InspectionECResponse;
import com.jd.bluedragon.distribution.api.response.PackageResponse;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/distribution-core-context.xml" })
public class InspectionResourceTestCase {
    
    String urlRoot = "http://localhost:8080/services/";
    
    // String urlRoot = "http://10.10.236.45:24050/services/";
    // String urlRoot = "http://192.168.200.202:8080/services/";
    // @Test
    public void testInspectionThridParty() {
        
        /*
         * String json = "" + "{" + "\"shieldsBarcode\":\"dd\"," +
         * "\"boxCode:\"B909-30-38246717\"," +
         * "\"packageBarcode\":\"15868035-1-1\"," + "\"businessType\":\"11\"," +
         * "\"exceptionType\":\"111\"," + "\"userName\":\"zhangsan\"," +
         * "\"userCode\":\"验货人code\"," + "\"siteCode\":\"操作单位Code\"," +
         * "\"receiveUnitCode\":\"接收单位Code\"" + "}";
         */
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
    
    // @Test
    public void testInspectionExceptionQuery() {
        InspectionECRequest inspectionECRequest = new InspectionECRequest();
        inspectionECRequest.setPartnerIdOrCode("010F001");
        
        // inspectionECRequest.setInspectionECType(2);
        // http://10.10.236.45:24050/inspection/exceptionQuery/{"siteCode":"12","inspectionECType":null}
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
        inspectionECRequest.setOperationType(5);
        List<InspectionRequest> requestLists = new ArrayList<InspectionRequest>();
        InspectionRequest packageRequest1 = new InspectionRequest();
        packageRequest1.setPackageBarcode("179619456N1S2H11");
        packageRequest1.setWaybillCode("179619456");
        packageRequest1.setReceiveSiteCode(10);
        packageRequest1.setBoxCode("BC010F001000003900004015");
        packageRequest1.setSiteCode(1006);
        packageRequest1.setUserCode(6781);
        packageRequest1.setUserName("BJCHYAN");
        requestLists.add(packageRequest1);
        
        /*
         * InspectionRequest packageRequest2 = new InspectionRequest();
         * packageRequest2.setPackageBarcode("970000000001N2S3H7");
         * packageRequest2.setWaybillCode("970000000001");
         * packageRequest2.setReceiveSiteCode(99);
         * packageRequest2.setBoxCode("B027Z001027F00100001001");
         * packageRequest2.setSiteCode(100); requestLists.add(packageRequest2);
         */
        
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
    
    // @Test
    public void getWaybill() {
        RestTemplate template = new RestTemplate();
        String url = this.urlRoot + "inspection/getWaybillPackage/117961926/511/0";
        try {
            String re = template.getForObject(url, String.class, String.class);
            System.out.println("re=" + re);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void testInspectionExceptionByBoxOrThird() {
        
        RestTemplate template = new RestTemplate();
        String url = this.urlRoot + "inspection/exceptionQueryExpand";
        
        long st = System.currentTimeMillis();
        InspectionECRequest inspectionECRequest = new InspectionECRequest();
        inspectionECRequest.setBoxCode("BC027F004027E00500003082");
        inspectionECRequest.setPartnerIdOrCode("452");
        // inspectionECRequest.setInspectionECType(2);
        try {
            InspectionECResponse response = template.postForObject(url, inspectionECRequest,
                    InspectionECResponse.class);
            System.out.println(System.currentTimeMillis() - st);
            List<PackageResponse> list = response.getLists();
            
            for (PackageResponse responseBean : list) {
                System.out.println(StringUtils.isBlank(responseBean.getPackageBarcode()) ? ""
                        : responseBean.getPackageBarcode() + "----");
            }
            System.out.println("re=" + response.getLists().size());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    @Test
    public void testMydd() {
        RestTemplate template = new RestTemplate();
        String url = this.urlRoot
                + "inspection?type=20&createSiteCode=511&startTime=2012-3-28 15:49:38&endTime=2012-3-30 19:49:38";
        HandoverResponse handoverResponse = template.getForObject(url, HandoverResponse.class);
        handoverResponse.getCode();
        
    }
    
}
