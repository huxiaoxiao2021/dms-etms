package test.com.jd.bluedragon.distribution.test.reverse;

import com.jd.bluedragon.distribution.send.service.ReverseDeliveryService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;

/** 
* ReverseDeliveryServiceImpl Tester. 
* 
* @author <wuzuxiang> 
* @since <pre>06/26/2017</pre> 
* @version 1.0 
*/
@RunWith(SpringJUnit4ClassRunner.class)  //使用junit4进行测试
@ContextConfiguration(locations = {"classpath:/spring/distribution-core-context-test.xml"})
public class ReverseDeliveryServiceImplTest {

    @Autowired
    private ReverseDeliveryService reversedeliveryService;

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception { 
    } 
    
    /** 
    * 
    * Method: findsendMToReverse(Task task) 
    * 
    */ 
    @Test
    public void testFindsendMToReverse() throws Exception { 
        //TODO: Test goes here... 
    } 
    
    /** 
    * 
    * Method: updateIsCancelToWaybillByBox(SendM tSendM, List<SendDetail> tlist) 
    * 
    */ 
    @Test
    public void testUpdateIsCancelToWaybillByBox() throws Exception { 
        //TODO: Test goes here... 
    } 
    
    /** 
    * 
    * Method: updateIsCancelByBox(SendM tSendM, List<SendDetail> tlist) 
    * 
    */ 
    @Test
    public void testUpdateIsCancelByBox() throws Exception { 
        //TODO: Test goes here... 
    } 
    
    /** 
    * 
    * Method: updateIsCancelToWaybillByPackageCode(SendM tSendM, SendDetail tSendDatail) 
    * 
    */ 
    @Test
    public void testUpdateIsCancelToWaybillByPackageCode() throws Exception { 
        //TODO: Test goes here... 
    } 
    
    /** 
    * 
    * Method: updateIsCancelByPackageCode(SendM tSendM, SendDetail tSendDatail) 
    * 
    */ 
    @Test
    public void testUpdateIsCancelByPackageCode() throws Exception { 
        //TODO: Test goes here... 
    } 
    
    /** 
    * 
    * Method: batchProcessOrderInfo2DSF(List<SendM> tSendMList) 
    * 
    */ 
    @Test
    public void testBatchProcessOrderInfo2DSF() throws Exception { 
        //TODO: Test goes here... 
    } 
    
    /** 
    * 
    * Method: batchProcesstoEmsServer(List<SendM> tSendMList) 
    * 
    */ 
    @Test
    public void testBatchProcesstoEmsServer() throws Exception { 
        //TODO: Test goes here... 
    } 
    
    /** 
    * 
    * Method: batchProcessOrderInfo3PL(List<SendM> tSendMList, Integer siteCode, String siteName) 
    * 
    */ 
    @Test
    public void testBatchProcessOrderInfo3PL() throws Exception { 
        //TODO: Test goes here... 
    } 
    
    /** 
    * 
    * Method: md5(String input) 
    * 
    */ 
    @Test
    public void testMd5() throws Exception { 
        //TODO: Test goes here... 
    } 
    
    /** 
    * 
    * Method: getWhemsWaybill(List<String> wlist) 
    * 
    */ 
    @Test
    public void testGetWhemsWaybill() throws Exception { 
        //TODO: Test goes here... 
    } 
    
    /** 
    * 
    * Method: pushWhemsWaybill(List<String> wlist) 
    * 
    */ 
    @Test
    public void testPushWhemsWaybill() throws Exception { 
        //TODO: Test goes here...
        List<String> waybills = Arrays.asList("VA00011427533","","VA00011433881");
        reversedeliveryService.pushWhemsWaybill(waybills);
    }
    
    /** 
    * 
    * Method: getWhemsClientService() 
    * 
    */ 
    @Test
    public void testGetWhemsClientService() throws Exception { 
        //TODO: Test goes here... 
    } 
    
    /** 
    * 
    * Method: setWhemsClientService(Ems4JingDongPortType whemsClientService) 
    * 
    */ 
    @Test
    public void testSetWhemsClientService() throws Exception { 
        //TODO: Test goes here... 
    } 
    
    /** 
    * 
    * Method: getSend3plConfigMap(HashMap send3plConfigMap) 
    * 
    */ 
    @Test
    public void testGetSend3plConfigMap() throws Exception { 
        //TODO: Test goes here... 
    } 
    
    /** 
    * 
    * Method: toEmsServer(List<String> waybillList) 
    * 
    */ 
    @Test
    public void testToEmsServer() throws Exception { 
        //TODO: Test goes here... 
    } 
    
    /** 
    * 
    * Method: decrypt(String mingwen) 
    * 
    */ 
    @Test
    public void testDecrypt() throws Exception { 
        //TODO: Test goes here... 
    } 
    
    /** 
    * 
    * Method: getWaybillInfo(String waybillCode) 
    * 
    */ 
    @Test
    public void testGetWaybillInfo() throws Exception { 
        //TODO: Test goes here... 
    } 
    
    /** 
    * 
    * Method: getEmsWaybillInfo(String waybillCode) 
    * 
    */ 
    @Test
    public void testGetEmsWaybillInfo() throws Exception { 
        //TODO: Test goes here... 
    } 
    
    /** 
    * 
    * Method: getWaybillQuickProduce(String waybillCode) 
    * 
    */ 
    @Test
    public void testGetWaybillQuickProduce() throws Exception { 
        //TODO: Test goes here... 
    } 
    
    /** 
    * 
    * Method: main(String[] args) 
    * 
    */ 
    @Test
    public void testMain() throws Exception { 
        //TODO: Test goes here... 
    } 
    
    
    /** 
    * 
    * Method: splitList(List<String> transresult) 
    * 
    */ 
    @Test
    public void testSplitList() throws Exception { 
        //TODO: Test goes here... 
        /* 
        try { 
           Method method = ReverseDeliveryServiceImpl.getClass().getMethod("splitList", List<String>.class); 
           method.setAccessible(true); 
           method.invoke(<Object>, <Parameters>); 
        } catch(NoSuchMethodException e) { 
        } catch(IllegalAccessException e) { 
        } catch(InvocationTargetException e) { 
        } 
        */ 
    } 

    /** 
    * 
    * Method: splitList1(List<String> transresult) 
    * 
    */ 
    @Test
    public void testSplitList1() throws Exception { 
        //TODO: Test goes here... 
        /* 
        try { 
           Method method = ReverseDeliveryServiceImpl.getClass().getMethod("splitList1", List<String>.class); 
           method.setAccessible(true); 
           method.invoke(<Object>, <Parameters>); 
        } catch(NoSuchMethodException e) { 
        } catch(IllegalAccessException e) { 
        } catch(InvocationTargetException e) { 
        } 
        */ 
    } 

    /** 
    * 
    * Method: dataTo3PlServer(List<String> waybillList, Integer siteCode, String siteName) 
    * 
    */ 
    @Test
    public void testDataTo3PlServer() throws Exception { 
        //TODO: Test goes here... 
        /* 
        try { 
           Method method = ReverseDeliveryServiceImpl.getClass().getMethod("dataTo3PlServer", List<String>.class, Integer.class, String.class); 
           method.setAccessible(true); 
           method.invoke(<Object>, <Parameters>); 
        } catch(NoSuchMethodException e) { 
        } catch(IllegalAccessException e) { 
        } catch(InvocationTargetException e) { 
        } 
        */ 
    } 

    /** 
    * 
    * Method: sendMqToWhsmsServer(List<String> waybillList) 
    * 
    */ 
    @Test
    public void testSendMqToWhsmsServer() throws Exception { 
        //TODO: Test goes here... 
//        try {
//           Method method = ReverseDeliveryServiceImpl.getClass().getMethod("sendMqToWhsmsServer", List<String>.class);
//           method.setAccessible(true);
//           method.invoke(<Object>, <Parameters>);
//        } catch(NoSuchMethodException e) {
//        } catch(IllegalAccessException e) {
//        } catch(InvocationTargetException e) {
//        }
    }

    /** 
    * 
    * Method: toWaybill(com.jd.bluedragon.common.domain.Waybill waybillQP, JoinDetail tJoinDetail) 
    * 
    */ 
    @Test
    public void testToWaybill() throws Exception { 
        //TODO: Test goes here... 
        /* 
        try { 
           Method method = ReverseDeliveryServiceImpl.getClass().getMethod("toWaybill", com.jd.bluedragon.common.domain.Waybill.class, JoinDetail.class); 
           method.setAccessible(true); 
           method.invoke(<Object>, <Parameters>); 
        } catch(NoSuchMethodException e) { 
        } catch(IllegalAccessException e) { 
        } catch(InvocationTargetException e) { 
        } 
        */ 
    } 

    /** 
    * 
    * Method: generateAllPackageCodes(String input, JoinDetail tJoinDetail) 
    * 
    */ 
    @Test
    public void testGenerateAllPackageCodes() throws Exception { 
        //TODO: Test goes here... 
        /* 
        try { 
           Method method = ReverseDeliveryServiceImpl.getClass().getMethod("generateAllPackageCodes", String.class, JoinDetail.class); 
           method.setAccessible(true); 
           method.invoke(<Object>, <Parameters>); 
        } catch(NoSuchMethodException e) { 
        } catch(IllegalAccessException e) { 
        } catch(InvocationTargetException e) { 
        } 
        */ 
    } 

    /** 
    * 
    * Method: getWaillCodeListMessge(WChoice queryWChoice, List<String> wlist) 
    * 
    */ 
    @Test
    public void testGetWaillCodeListMessge() throws Exception { 
        //TODO: Test goes here... 
        /* 
        try { 
           Method method = ReverseDeliveryServiceImpl.getClass().getMethod("getWaillCodeListMessge", WChoice.class, List<String>.class); 
           method.setAccessible(true); 
           method.invoke(<Object>, <Parameters>); 
        } catch(NoSuchMethodException e) { 
        } catch(IllegalAccessException e) { 
        } catch(InvocationTargetException e) { 
        } 
        */ 
    } 

} 
