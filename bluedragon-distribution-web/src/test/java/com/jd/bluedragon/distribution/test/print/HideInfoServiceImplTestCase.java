package com.jd.bluedragon.distribution.test.print;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.jd.bluedragon.distribution.print.domain.BasePrintWaybill;
import com.jd.bluedragon.distribution.print.service.HideInfoServiceImpl;

public class HideInfoServiceImplTestCase {
    @Test
    public void testDealSopJZD() throws Exception{
    	BasePrintWaybill waybill = new BasePrintWaybill();
    	HideInfoServiceImpl hideInfoServiceImpl = new HideInfoServiceImpl();
    	
    	List<String> sendPays = new ArrayList<String>();
    	sendPays.add(getSignChar(188,'0'));
    	sendPays.add(getSignChar(188,'1'));
    	sendPays.add(getSignChar(188,'2'));
    	sendPays.add(getSignChar(188,'3'));
    	sendPays.add(getSignChar(188,'4'));
    	sendPays.add(getSignChar(188,'5'));
    	sendPays.add(getSignChar(188,'6'));
    	sendPays.add(getSignChar(188,'7'));
    	for(String sendPay:sendPays){
    		hideInfoServiceImpl.setHideInfo(null, sendPay, waybill);
    	}
    }
    private String getSignChar(int position,char c){
    	char[] chars = "00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000".toCharArray();
    	chars[position - 1] = c;
    	return new String(chars);
    }
}
