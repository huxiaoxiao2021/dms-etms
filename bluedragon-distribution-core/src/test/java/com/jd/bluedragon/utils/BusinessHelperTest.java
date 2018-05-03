package com.jd.bluedragon.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.jd.bluedragon.distribution.api.response.WaybillPrintResponse;
import com.jd.bluedragon.distribution.print.domain.BasePrintWaybill;
import com.jd.bluedragon.distribution.print.domain.PrintWaybill;

public class BusinessHelperTest {
	
	BusinessHelper helper = new BusinessHelper();

	@Test
	public void testIsBoxcode() {
		String boxCode="TC010A001010F00500039001";
		boolean isBoxCode = helper.isBoxcode(boxCode).booleanValue();
		assertTrue(isBoxCode);
		
		String waybillCode="T18655150305";
		isBoxCode = helper.isBoxcode(waybillCode).booleanValue();
		assertFalse(isBoxCode);
	}

	@Test
	public void testIsWaybillCode() {
		String waybillCodeT="T18655150305";
		boolean isWaybillCode = helper.isWaybillCode(waybillCodeT).booleanValue();
		assertTrue(isWaybillCode);
		
		String waybillCodeF="F18655150305";
		isWaybillCode = helper.isWaybillCode(waybillCodeF).booleanValue();
		assertTrue(isWaybillCode);
		
		String packageCode="T18655150305-1-1-1";
		isWaybillCode = helper.isWaybillCode(packageCode).booleanValue();
		assertFalse(isWaybillCode);
		
		String boxCode="TC010A001010F00500039001";
		isWaybillCode = helper.isWaybillCode(boxCode).booleanValue();
		assertFalse(isWaybillCode);
	}
	
	@Test
	public void testIsECLPCode() {
		boolean shoudFalse = false;
		boolean shoudTrue = true;
		
		String sourceCodeNUll=null;
		shoudFalse = helper.isECLPCode(sourceCodeNUll);
		assertFalse(shoudFalse);
		
		String sourceCodeMMC="mmc";
		shoudFalse = helper.isECLPCode(sourceCodeMMC);
		assertFalse(shoudFalse);
		
		String sourceCodeECLP="ECLP";
		shoudTrue = helper.isECLPCode(sourceCodeECLP);
		assertTrue(shoudTrue);
	}
	@Test
	public void testGetStoreId() {
		String[] codes = {"wms-6-1","wms-61-2","wwwwms-622-3","wmsw-622-44"};
		for(String code:codes){
			Integer storeId = SerialRuleUtil.getStoreIdFromStoreCode(code);
			System.err.println(code+"->"+storeId);
		}
	}
	@Test
	public void testIsReverseSpareCode() {
		String[] codes = {"un1234567890123456","null1234567890123456",
				"zA1234567890123456","Az1234567890123456",
				"1234567890123456","A11234567890123456",
				"null2017122600001004","^#2017122600001004"};
		for(String code:codes){
			boolean storeId = BusinessHelper.isReverseSpareCode(code);
			System.err.println(code+"->"+storeId);
		}
	}
	@Test
	public void testIsSopOrExternal() {
		System.err.println("根据waybillSign判断是否SOP和纯外单");
		String[] codes = {"40000000000000301000000000000000000000000000000000","20000000000000301000000000000000000000000000000000",
				"K0000000000000301000000000000000000000000000000000","60000000000000301000000000000000000000000000000000",
				"T0000000000000301000000000000000000000000000000000","k11234567890123456",
				"30000000000000301000000000000000000000000000000000","17122600001004"};
		for(String code:codes){
			boolean storeId = BusinessHelper.isSopOrExternal(code);
			System.err.println(code+"->"+storeId);
		}
	}
	@Test
	public void testJsonHelper() {
		String jsonStr = "{\n  \"originalDmsCode\" : 364605,\n  \"originalDmsName\" : \"北京通州分拣中心\",\n  \"busiId\" : 32805,\n  \"busiName\" : \"集团行政部\",\n  \"originalCityCode\" : 2809,\n  \"originalCityName\" : \"通州区\",\n  \"transportMode\" : \"特惠送\",\n  \"priceProtectFlag\" : 0,\n  \"priceProtectText\" : \"\",\n  \"signBackText\" : \"\",\n  \"distributTypeText\" : \"普通\",\n  \"consigner\" : \"杨芳慧\",\n  \"consignerTel\" : \"89125559\",\n  \"consignerMobile\" : \"17080133690\",\n  \"consignerAddress\" : \"北京大兴区五环至六环之间京东总部亦庄\",\n  \"busiOrderCode\" : \"1\",\n  \"specialMark\" : \"众\",\n  \"jZDFlag\" : \"\",\n  \"road\" : \"0\",\n  \"sopOrExternalFlg\" : true,\n  \"printTime\" : \"2018-04-19 10:24:14\",\n  \"bjCheckFlg\" : false,\n  \"muslimSignText\" : \"清真\",\n  \"templateVersion\" : 0,\n  \"freightText\" : \"\",\n  \"goodsPaymentText\" : \"在线支付\",\n  \"remark\" : \"重要资料，请务必交由本人签收，谢谢！\",\n  \"waybillCode\" : \"VA35880401351\",\n  \"type\" : 10000,\n  \"statusCode\" : 200,\n  \"statusMessage\" : \"OK\",\n  \"quantity\" : 1,\n  \"popSupId\" : 32805,\n  \"popSupName\" : \"杨芳慧\",\n  \"normalText\" : \"无\",\n  \"purposefulDmsCode\" : 364605,\n  \"userLevel\" : \"\",\n  \"purposefulDmsName\" : \"北京通州分拣中心\",\n  \"originalTabletrolley\" : \"E06\",\n  \"purposefulTableTrolley\" : \"E06\",\n  \"originalCrossCode\" : \"3\",\n  \"purposefulCrossCode\" : \"3\",\n  \"prepareSiteName\" : \"北京荣丰站\",\n  \"prepareSiteCode\" : 242636,\n  \"printAddress\" : \"北京市丰台区西三环中路88号国电接待中心15F  国电物流有限公司\",\n  \"timeCategory\" : \"\",\n  \"packagePrice\" : \"在线支付\",\n  \"customerName\" : \"郑云龙\",\n  \"customerContacts\" : \"13681111611,010-58688474\",\n  \"mobileFirst\" : \"1368111\",\n  \"mobileLast\" : \"1611\",\n  \"telFirst\" : \"010-5868\",\n  \"telLast\" : \"8474\",\n  \"sendPay\" : \"00000000000000000000000000000000000000000000000000\",\n  \"waybillSign\" : \"30000000016900000000000000000000000000000000000100\",\n  \"packList\" : [ {\n    \"packageCode\" : \"VA35880401351-1-1-\",\n    \"weight\" : 1.0\n  } ],\n  \"printInvoice\" : false,\n  \"isAir\" : false,\n  \"isSelfService\" : false\n}";
		BasePrintWaybill resp = JsonHelper.fromJson(jsonStr, BasePrintWaybill.class);
		PrintWaybill resp1 = JsonHelper.fromJson(jsonStr, PrintWaybill.class);
		WaybillPrintResponse resp2 = JsonHelper.fromJson(jsonStr, WaybillPrintResponse.class);
		System.err.println(resp);
	}
	@Test
	public void testGetHashKeyByPackageCode() {
		String[] codes = {null,
				"",
				"72945262907N3S5H30",
				"72945262907N200S500H30",
				"72945262907N201S500H30",
				"72945262907N400S300H30",
				"72945262907N500S300H30",
				"72945262907-1-500-",
				"72945262907-200-500-",
				"72945262907-201-500-",
				"72945262907-400-500-",
				"72945262907-500-500-",
				"WW72990321844-1-1-4"
				};
		for(String code:codes){
			String[] keys = BusinessHelper.getHashKeysByPackageCode(code);
			if(keys!=null){
				System.err.println(code+" "+BusinessHelper.getWaybillCode(code)+"->{"+keys[0]+":"+keys[1]+"}");
			}else{
				System.err.println(code+"->无效包裹号");
			}
		}
	}
}
