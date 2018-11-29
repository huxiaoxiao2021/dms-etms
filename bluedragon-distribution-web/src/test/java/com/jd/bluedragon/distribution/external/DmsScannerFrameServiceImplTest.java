package com.jd.bluedragon.distribution.external;

import com.jd.bluedragon.distribution.api.request.GantryDeviceConfigJsfRequest;
import com.jd.bluedragon.distribution.api.request.UploadDataJsfRequest;
import com.jd.bluedragon.distribution.auto.domain.UploadData;
import com.jd.bluedragon.distribution.external.service.DmsScannerFrameService;
import com.jd.bluedragon.distribution.external.service.impl.DmsScannerFrameServiceImpl;
import com.jd.bluedragon.distribution.gantry.domain.GantryDeviceConfig;
import com.jd.bluedragon.utils.BeanHelper;
import com.jd.bluedragon.utils.JsonHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

/** 
* DmsScannerFrameServiceImpl Tester. 
* 
* @author <wuzuxiang> 
* @since <pre>11/14/2018</pre> 
* @version 1.0 
*/ 
public class DmsScannerFrameServiceImplTest {

    private DmsScannerFrameService service;

    private UploadDataJsfRequest dataRequest;

    private GantryDeviceConfigJsfRequest configJsfRequest;

    @Before
    public void before() throws Exception {
        service = new DmsScannerFrameServiceImpl();
        dataRequest = new UploadDataJsfRequest();
        configJsfRequest = new GantryDeviceConfigJsfRequest();

        dataRequest.setBarCode("12345678900-1-1-");
        dataRequest.setSendSiteCode(364605);
        dataRequest.setBoxCode("BC666F555444F232873847589");
        dataRequest.setBoxSiteCode(364605);
        dataRequest.setChuteCode("123");
        dataRequest.setDistributeId(910);
        dataRequest.setHeight(0.1f);
        dataRequest.setLength(0.2f);
        dataRequest.setWidth(0.3f);
        dataRequest.setWeight(0.4f);
        dataRequest.setOperatorId(36566);
        dataRequest.setOperatorName("bjxings");
        dataRequest.setPackageCode("12345678900-1-1-");
        dataRequest.setRegisterNo("MAJ-DDD-001");
        dataRequest.setScannerTime(new Date());
        dataRequest.setSource(1);

        configJsfRequest.setBusinessType(3);
        configJsfRequest.setBusinessTypeRemark("发货");
        configJsfRequest.setCreateSiteCode(910);
        configJsfRequest.setCreateSiteName("马驹桥分拣中心");
        configJsfRequest.setDbTime(new Date());
        configJsfRequest.setEndTime(new Date());
        configJsfRequest.setGantrySerialNumber("HDN-001");
        configJsfRequest.setId(2L);
        configJsfRequest.setLockStatus(1);
        configJsfRequest.setLockUserErp("wuzuxiang");
        configJsfRequest.setLockUserName("吴祖祥");
        configJsfRequest.setMachineId("HDM-001");
        configJsfRequest.setSendCode("456-132-5464687684135213");
        configJsfRequest.setStartTime(new Date());
        configJsfRequest.setVersion((byte)1);
        configJsfRequest.setYn(1);
        configJsfRequest.setOperateUserErp("wzx");
        configJsfRequest.setOperateUserName("吴祖祥");
        configJsfRequest.setOperateUserId(45454);
        configJsfRequest.setUpdateUserName("wsdf");
        configJsfRequest.setUpdateUserErp("wzx");

    } 

    @After
    public void after() throws Exception { 
    } 
    
    /** 
    * 
    * Method: dealScannerFrameConsume(UploadDataJsfRequest uploadData, GantryDeviceConfigJsfRequest config) 
    * 
    */ 
    @Test
    public void testDealScannerFrameConsume() throws Exception { 
        //TODO: Test goes here...

        service.dealScannerFrameConsume(dataRequest,configJsfRequest);
    } 

    /** 
    * 
    * Method: convert2UploadData(UploadDataJsfRequest request) 
    * 
    */ 
    @Test
    public void testConvert2UploadData() throws Exception {
        UploadData uploadData = new UploadData();
        BeanHelper.copyProperties(uploadData,dataRequest);
        System.out.println(JsonHelper.toJson(uploadData));
    }

    /** 
    * 
    * Method: convert2GantryDeviceConfig(GantryDeviceConfigJsfRequest request) 
    * 
    */ 
    @Test
    public void testConvert2GantryDeviceConfig() throws Exception {
        GantryDeviceConfig gantryDeviceConfig = new GantryDeviceConfig();
        BeanHelper.copyProperties(gantryDeviceConfig,configJsfRequest);
        System.out.println(JsonHelper.toJson(gantryDeviceConfig));
    }

} 
