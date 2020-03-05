package com.jd.bluedragon.distribution.inspection.service.impl;

import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.inspection.domain.InspectionMQBody;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/** 
* InspectionNotifyServiceImpl Tester. 
* 
* @author <wuzuxiang> 
* @since <pre>12/20/2017</pre> 
* @version 1.0 
*/
@RunWith(MockitoJUnitRunner.class)
public class InspectionNotifyServiceImplTest {

    @InjectMocks
    private InspectionNotifyServiceImpl inspectionNotifyService;

    @Mock
    @Qualifier("inspectionDataSyncMQ")
    private DefaultJMQProducer inspectionDataSyncMQ;

    @Mock
    @Qualifier("cycleMaterialSendMQ")
    private DefaultJMQProducer cycleMaterialSendMQ;

    private InspectionMQBody body;

    @Before
    public void before() throws Exception {
        body = new InspectionMQBody();
        body.setWaybillCode("JDJ000121845488");
        body.setCreateUserCode(11);
        body.setCreateUserName("bjxings");
        body.setInspectionSiteCode(910);
        body.setOperateTime(new Date());

    } 

    @After
    public void after() throws Exception { 
    }

    @Test
    public void send(){
        try {
            DefaultJMQProducer ss = Mockito.mock(inspectionDataSyncMQ.getClass());
            Mockito.doAnswer(new Answer<Object>() {
                public Object answer(InvocationOnMock invocation) {
                    Object[] args = invocation.getArguments();
                    return "called with arguments: " + args;
                }
            }).when(ss).sendOnFailPersistent(Mockito.anyString(),Mockito.anyString());

            DefaultJMQProducer aa = Mockito.mock(cycleMaterialSendMQ.getClass());
            Mockito.doAnswer(new Answer<Object>() {
                public Object answer(InvocationOnMock invocation) {
                    Object[] args = invocation.getArguments();
                    return "called with arguments: " + args;
                }
            }).when(aa).sendOnFailPersistent(Mockito.anyString(),Mockito.anyString());

            inspectionNotifyService.send(body);
            Assert.assertTrue(Boolean.TRUE);
        }catch (Exception e){
            //异常则断言失败
            Assert.assertTrue(Boolean.FALSE);
        }
    }
    
    /** 
    * 
    * Method: send(InspectionMQBody body) 
    * 
    */ 
    @Test
    public void testSend() throws Exception { 
        //TODO: Test goes here...
        InspectionMQBody body = new InspectionMQBody();
        List<String> waybills = waybills();
        int i  = 1;
        for (String waybill:waybills){
            body.setWaybillCode(waybill);
            if (checkWaybill(body.getWaybillCode())){
                System.out.println("运单：" + waybill);
//                inspectionNotifyService.send(body);
            }
            if (checkVWaybill(waybill)){
                System.out.println("外单：" + waybill);
            }
            System.out.println("\n");
        }
    }

    private boolean checkWaybill(String s){
        if(!WaybillUtil.isWaybillCode(s)){
            return false;
        }
        return true;
    }

    private boolean checkVWaybill(String s){
        if(!WaybillUtil.isBusiWaybillCode(s)){
            return false;
        }
        return true;
    }

    private List<String> waybills(){
        List<String> waybills = new ArrayList<String>();
        waybills.add("VA38410906601");
        waybills.add("VA38463040162");
        waybills.add("VB38404961722");
        waybills.add("VB38449313361");
        waybills.add("VC38856249972");
        waybills.add("VC39747437354");
        waybills.add("VD38433848563");
        waybills.add("VD38871943972");
        waybills.add("VE38948984023");
        waybills.add("VE39785427916");
        waybills.add("VY38345298202");
        waybills.add("VY39069639663");
        waybills.add("V038469782805");
        waybills.add("V038950823472");
        waybills.add("VG39496137774");
        waybills.add("VF39481178251");
        waybills.add("VF39784055916");
        waybills.add("VTF39784055916");
        waybills.add("VG39497462336");
        waybills.add("69537669621");
        waybills.add("70089544750");
        waybills.add("69933430726");
        waybills.add("70089374827");
        waybills.add("69553517491");
        waybills.add("T69553517491");
        return waybills;
    }
    
} 
