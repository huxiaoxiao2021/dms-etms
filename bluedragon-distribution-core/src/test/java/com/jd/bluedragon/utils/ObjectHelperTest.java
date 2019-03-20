package com.jd.bluedragon.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.distribution.print.domain.BasePrintWaybill;
import com.jd.bluedragon.distribution.print.domain.PrintWaybill;
import com.jd.bluedragon.distribution.waybill.domain.LabelPrintingResponse;

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
    @Test
    public void printPrintResponse(){
    	Set<String> set0 = new HashSet();
    	Set<String> set1 = new HashSet();
    	Set<String> set2 = new HashSet();
    	Set<String> set22 = new HashSet();
    	Set<String> setAll = new HashSet();
		List<Field> fields0 = ObjectHelper.getAllFieldsList(BasePrintWaybill.class);
		for(Field f:fields0){
			String fieldName = f.getName();
			set0.add(fieldName);
		}
		List<Field> fields1 = ObjectHelper.getAllFieldsList(PrintWaybill.class);
		for(Field f:fields1){
			String fieldName = f.getName();
			set1.add(fieldName);
		}
		List<Field> fields2 = ObjectHelper.getAllFieldsList(LabelPrintingResponse.class);
		for(Field f:fields2){
			String fieldName = f.getName();
			set2.add(fieldName);
		}
		List<Field> fields22 = ObjectHelper.getAllFieldsList(Waybill.class);
		for(Field f:fields22){
			String fieldName = f.getName();
			set22.add(fieldName);
		}
		
		setAll.addAll(set1);
		setAll.addAll(set2);
		//相同但是没有抽到公共类的
		Set<String> setHas12 = new HashSet();
		//1
		Set<String> setHas1 = new HashSet();
		//2
		Set<String> setHas2 = new HashSet();
		//22
		Set<String> setHas22 = new HashSet();
		for(String f:setAll){
			if(set1.contains(f) && set2.contains(f) && !set0.contains(f)){
				setHas12.add(f);
			}
			if(set1.contains(f) && !set2.contains(f)){
				setHas1.add(f);
			}
			if(set2.contains(f) && !set1.contains(f)){
				setHas2.add(f);
			}
			if(set22.contains(f) && !set0.contains(f)){
				setHas22.add(f);
			}
		}
		System.err.println("PrintWaybill有的："+setHas1);
		System.err.println("LabelPrintingResponse有的："+setHas2);
		System.err.println("Waybill有的："+setHas22);
		System.err.println("公共的："+setHas12);
		List<String> list = new ArrayList<String>();
		int cn = 1;
		while(cn<=10){
			list.add(""+cn);
			cn++;
		}
		System.err.println(list);
		for(int i=0;i<list.size();i++){
			String val = list.get(i);
			int index = Integer.parseInt(val);
			if(index<3 || index>5){
				list.remove(val);
				i--;
			}
		}
		System.err.println(list);
    }
}
