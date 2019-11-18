package com.jd.bluedragon.utils;

import junit.framework.Assert;

import org.junit.Test;

import com.jd.bluedragon.dms.utils.BusinessUtil;

public class BusinessUtilTest {
    @Test
    public void testIsSendCode(){
    	String[] sucCodes = {"1-1-123456789012345","123456789-123456789-123456789012345"
    			,"10186-36215-20190326095612016","10186-10374-20190603112759592","704728-25022-20190626145752080",
    			"923456781-923456781-12345678901234567"};
    	String[] errorCodes = {null,"","  ","0-0-123456789","1-1-12345678901234"," 1-1-12345678901234"
    			,"0-1-123456789012345","0-0-123456789012345","01-0-123456789012345","1-01-123456789012345"
    			,"01-01-123456789012345","1234567890-1-123456789012345","1-1234567890-123456789012345"
    			,"910-364605-123456789012345678"
    			,"1234567890-1234567890-123456789012345"
    			,"w1234567890-1234567890-123456789012345"
    			,"1234567890-w1234567890-123456789012345"
    			,"1234567890-1234567890-w123456789012345"};
    	for(String code:sucCodes){
    		Assert.assertTrue(BusinessUtil.isSendCode(code));
    	}
    	for(String code:errorCodes){
    		Assert.assertFalse(BusinessUtil.isSendCode(code));
    	}
    }
    @Test
    public void testGetSiteCodeBySendCode(){
    	String[] sucCodes = {"1-1-123456789012345","123456789-123456789-123456789012345"
    			,"10186-36215-20190326095612016","10186-10374-20190603112759592","704728-25022-20190626145752080"
    			,"Y1-1-123456789012345","Y123456789-123456789-123456789012345"
    			,"Y10186-36215-20190326095612016","Y10186-10374-20190603112759592","Y704728-25022-20190626145752080"};
    	String[] errorCodes = {null,"","  ","0-0-123456789","1-1-1234567890123"," 1-1-1234567890123"
    			,"0-1-123456789012345","0-0-123456789012345","01-0-123456789012345","1-01-123456789012345"
    			,"01-01-123456789012345","1234567890-1-123456789012345","1-1234567890-123456789012345"
    			,"1234567890-1234567890-123456789012345"
    			,"w1234567890-1234567890-123456789012345"
    			,"1234567890-w1234567890-123456789012345"
    			,"1234567890-1234567890-w123456789012345"};
    	for(String code:sucCodes){
    		Integer[] siteCodes = BusinessUtil.getSiteCodeBySendCode(code);
    		System.err.println(code+":"+siteCodes[0]+","+siteCodes[1]);
    		Assert.assertTrue(siteCodes[0]>0 && siteCodes[1]>0);
    	}
    	for(String code:errorCodes){
    		System.err.println(code);
    		Integer[] siteCodes = BusinessUtil.getSiteCodeBySendCode(code);
    		Assert.assertFalse(siteCodes[0]>0 && siteCodes[1]>0);
    	}
    }
}
