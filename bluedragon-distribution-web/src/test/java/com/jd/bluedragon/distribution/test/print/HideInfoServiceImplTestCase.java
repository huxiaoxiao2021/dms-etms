package com.jd.bluedragon.distribution.test.print;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.jd.bluedragon.distribution.print.domain.BasePrintWaybill;
import com.jd.bluedragon.distribution.print.service.HideInfoServiceImpl;
import com.jd.bluedragon.utils.JsonHelper;

public class HideInfoServiceImplTestCase {
    @Test
    public void testSetHideInfo() throws Exception{
    	BasePrintWaybill waybill = new BasePrintWaybill();
    	HideInfoServiceImpl hideInfoServiceImpl = new HideInfoServiceImpl();
    	List<String> sendPays = new ArrayList<String>();
    	sendPays.add(null);
    	sendPays.add(getSignChar(188,'0'));
    	sendPays.add(getSignChar(188,'1'));
    	sendPays.add(getSignChar(188,'2'));
    	sendPays.add(getSignChar(188,'3'));
    	sendPays.add(getSignChar(188,'4'));
    	sendPays.add(getSignChar(188,'5'));
    	sendPays.add(getSignChar(188,'6'));
    	sendPays.add(getSignChar(188,'7'));
    	sendPays.add(getSignChar(188,'8'));
    	for(String sendPay:sendPays){
        	waybill.setCustomerName("收件人姓名");
        	waybill.setPrintAddress("收件人地址67890123456");
        	waybill.setTelFirst("1501001");
        	waybill.setTelLast("1002");
        	waybill.setMobileFirst("1511002");
        	waybill.setMobileLast("1003");
        	waybill.setCustomerContacts(waybill.getTelFirst()+waybill.getTelLast()+","+waybill.getMobileFirst()+waybill.getMobileLast());
    		hideInfoServiceImpl.setHideInfo(null, sendPay, waybill);
    		System.err.println(JsonHelper.toJson(waybill));
    	}
    }
    private String getSignChar(int position,char c){
    	char[] chars = "00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000".toCharArray();
    	chars[position - 1] = c;
    	return new String(chars);
    }
}
