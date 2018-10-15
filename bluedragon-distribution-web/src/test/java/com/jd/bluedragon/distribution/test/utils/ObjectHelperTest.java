package com.jd.bluedragon.distribution.test.utils;

import java.lang.reflect.Field;
import java.util.Map;

import org.junit.Test;

import com.jd.bluedragon.distribution.print.domain.BasePrintWaybill;
import com.jd.bluedragon.distribution.print.domain.PrintWaybill;
import com.jd.bluedragon.distribution.waybill.domain.LabelPrintingResponse;
import com.jd.bluedragon.utils.ObjectHelper;

public class ObjectHelperTest {
	@Test
	public void testFields() throws Exception {
		Map<String,Field> parentFields = ObjectHelper.getDeclaredFields(BasePrintWaybill.class);
		Map<String,Field> clildFields1 = ObjectHelper.getDeclaredFields(PrintWaybill.class);
		Map<String,Field> clildFields2 = ObjectHelper.getDeclaredFields(LabelPrintingResponse.class);
		System.err.println("=======PrintWaybill=========");
		for(String s:clildFields1.keySet()){
			System.err.println(s);
		}
		System.err.println("=======LabelPrintingResponse=========");
		for(String s:clildFields2.keySet()){
			System.err.println(s);
		}
		System.err.println("=======共有字段=========");
		for(String s:clildFields1.keySet()){
			if(clildFields2.keySet().contains(s)){
				System.err.println(s);
			}
		}
	}
    public static String toFirstLetterUpperCase(String str) {  
        if(str == null || str.length() < 2){  
            return str;  
        }  
         String firstLetter = str.substring(0, 1).toUpperCase();  
         return firstLetter + str.substring(1, str.length());  
     } 
}