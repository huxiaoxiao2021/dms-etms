package com.jd.bluedragon.distribution.test.print;

import com.jd.bluedragon.dms.utils.BusinessUtil;
import org.junit.Test;

import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import com.jd.bluedragon.distribution.print.domain.BasePrintWaybill;
import com.jd.bluedragon.utils.BusinessHelper;

public class WaybillPrintTest extends AbstractDaoIntegrationTest{
	
	@Test
    public void testPrint() {
		BasePrintWaybill waybill = new BasePrintWaybill();
		waybill.setSpecialMark("众半提柜");
		String[] marks = {"集","城","集","城"};
		for(String mark:marks){
			waybill.appendSpecialMark(mark);
			System.err.println(waybill.getSpecialMark());
		}
		waybill.dealConflictSpecialMark("集", "柜");
		waybill.dealConflictSpecialMark("集", "城");
		waybill.dealConflictSpecialMark("集", "提");
		System.err.println(waybill.getSpecialMark());
	}
	@Test
    public void testWaybill() {
		String waybillSign = ""
				+ "1234567890"
				+ "0010000010"//1
				+ "0010000010"//2
				+ "0010010010"//3
				+ "0010000010"//4
				+ "0010000010"//5
				+ "0010000010"//6
				+ "0010000010"//7
				+ "0010000010"//8
				+ "0010000010"//9
				+ "0010000010"//10
				+ "0010000010"//11
				+ "0011000010"//12
				+ "0010000010"//13
				+ "0010000010"//14
				+ "0010000010"//15
				+ "0010000010";//16
		String sendPay = ""
				+ "1234567890"
				+ "0010000010"//1
				+ "0010000010"//2
				+ "0010010010"//3
				+ "0010000010"//4
				+ "0010000010"//5
				+ "0010000010"//6
				+ "0010000010"//7
				+ "0010000010"//8
				+ "0010000010"//9
				+ "0010000010"//10
				+ "0010000010"//11
				+ "0011000010"//12
				+ "0010000010"//13
				+ "0010000010"//14
				+ "0010000010"//15
				+ "0010000010";//16
		int[] positions = {1,36,124,146};
		for(int position:positions){
			System.err.println(BusinessUtil.isSignY(waybillSign, position));
			System.err.println(BusinessUtil.isSignY(sendPay, position));
		}
		System.err.println(BusinessUtil.isUrban(waybillSign, sendPay));
	}
	public static void main(String[] args){
		new WaybillPrintTest().testPrint();
		new WaybillPrintTest().testWaybill();
	}
}
