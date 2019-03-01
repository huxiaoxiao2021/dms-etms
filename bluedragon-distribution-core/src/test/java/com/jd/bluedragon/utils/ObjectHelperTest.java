package com.jd.bluedragon.utils;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.jd.bluedragon.distribution.print.domain.PrintWaybill;

/**
 * 
 * @ClassName: ObjectHelperTest
 * @Description: TODO
 * @author: wuyoude
 * @date: 2019年1月16日 下午4:37:05
 *
 */
public class ObjectHelperTest {

    @Test
    public void printFiles(){
    	String[] strs = {"packageCode","packageIndex","packageWeight","waybillCode","additionalComment","brandImageKey","consigneeCompany","consigneeCompany","consigner","consignerAddress","consignerTelText","customerName","destinationCrossCode","destinationDmsName","destinationTabletrolleyCode","freightText","goodsPaymentText","goodsPaymentText","mobileFirst","mobileLast","originalCrossCode","originalDmsName","originalTabletrolleyCode","popularizeMatrixCode","printAddress","printSiteName","remark","rodeCode","senderCompany","telFirst","telLast","transportMode","waybillSignText","examineFlag","securityCheck"};
		String[] strs1 = {"promiseText","deliveryTimeCategory","customerOrderTime","distributTypeText","jZDFlag","telLast","telFirst","mobileLast","mobileFirst","busiOrderCode","busiCode","consignerTelText","consigner","additionalComment","rodeCode","originalDmsName","destinationDmsName","originalCrossCode","originalTabletrolleyCode","destinationCrossCode","destinationTabletrolleyCode","printSiteName","packageIndex","specialRequirement","consignerAddress","senderCompany","printAddress","consigneeCompany","customerName","goodsPaymentText","backupSiteName","waybillSignText","packageCode","waybillCode","waybillCode","muslimSignText","remark","printTime","freightText","weightFlagText","packageCode"};
    	Set<String> set = new HashSet();
    	Set<String> set1 = new HashSet();
		for(String s:strs){
			set.add(s);
		}
		for(String s:strs1){
			set1.add(s);
		}
		
    	Class[] beans = new Class[]{PrintWaybill.class};
    	System.err.println(set.size());
    	System.err.println("==========");
		for(Class clazz:beans){
			List<Field> fields = ObjectHelper.getAllFieldsList(clazz);
			for(Field f:fields){
				String fieldName = f.getName();
				if(set.contains(fieldName)){
					System.err.println(fieldName);
//					set.remove(fieldName);
				}
			}
		}
		System.err.println("==========");
		for(String s:set){
			System.err.println(s);
		}
		System.err.println("==========");
		for(String s:set1){
			if(!set.contains(s)){
				System.err.println(s);
			}
			
		}
	}

}
