package com.jd.bluedragon.distribution.inspection.service.impl;

import com.jd.bluedragon.distribution.inspection.domain.InspectionMQBody;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.SerialRuleUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/** 
* InspectionNotifyServiceImpl Tester. 
* 
* @author <wuzuxiang> 
* @since <pre>12/20/2017</pre> 
* @version 1.0 
*/ 
public class InspectionNotifyServiceImplTest {//extends TestBase {

//    @Autowired
//    private InspectionNotifyService inspectionNotifyService;

    @Before
    public void before() throws Exception { 
    } 

    @After
    public void after() throws Exception { 
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
