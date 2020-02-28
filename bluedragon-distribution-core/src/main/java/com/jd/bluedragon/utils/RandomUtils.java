package com.jd.bluedragon.utils;

import org.apache.commons.lang.StringUtils;

import java.util.Date;
import java.util.Random;

public class RandomUtils {
	    public static final String allChar = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	    public static final String letterChar = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	    public static final String numberChar = "0123456789";

	   
	    public static String generateString(int length) {
	        StringBuffer sb = new StringBuffer();
	        Random random = new Random();
	        for (int i = 0; i < length; i++) {
	            sb.append(numberChar.charAt(random.nextInt(numberChar.length())));
	        }
	        return sb.toString();
	    }

	   
	    public static String generateMixString(int length) {
	        StringBuffer sb = new StringBuffer();
	        Random random = new Random();
	        for (int i = 0; i < length; i++) {
	            sb.append(allChar.charAt(random.nextInt(letterChar.length())));
	        }
	        return sb.toString();
	    }

	    public static String generateLowerString(int length) {
	        return generateMixString(length).toLowerCase();
	    }

	   
	    public static String generateUpperString(int length) {
	        return generateMixString(length).toUpperCase();
	    }

	    
	    public static String generateZeroString(int length) {
	        StringBuffer sb = new StringBuffer();
	        for (int i = 0; i < length; i++) {
	            sb.append('0');
	        }
	        return sb.toString();
	    }

	   
	    public static String toFixdLengthString(long num, int fixdlenth) {
	        StringBuffer sb = new StringBuffer();
	        String strNum = String.valueOf(num);
	        if (fixdlenth - strNum.length() >= 0) {
	            sb.append(generateZeroString(fixdlenth - strNum.length()));
	        } else {
	            throw new RuntimeException("������" + num + "ת��Ϊ����Ϊ" + fixdlenth + "���ַ����쳣��");
	        }
	        sb.append(strNum);
	        return sb.toString();
	    }

	   
	    public static String toFixdLengthString(int num, int fixdlenth) {
	        StringBuffer sb = new StringBuffer();
	        String strNum = String.valueOf(num);
	        if (fixdlenth - strNum.length() >= 0) {
	            sb.append(generateZeroString(fixdlenth - strNum.length()));
	        } else {
	            throw new RuntimeException("������" + num + "ת��Ϊ����Ϊ" + fixdlenth + "���ַ����쳣��");
	        }
	        sb.append(strNum);
	        return sb.toString();
	    }

	    public static String generateRandomNumByCurDate() {
	        Date serverDate = DateHelper.parseDate(DateHelper.formatDate(new Date()));
            final int prime = 31;
	        String randomStr = serverDate.getTime() * prime + "";
	        return StringUtils.substring(randomStr, 3 , 9);
        }

	    public static void main(String[] args) {
	        System.out.println(generateUpperString(15));
	        System.out.println(toFixdLengthString(123, 5));
            System.out.println(generateRandomNumByCurDate());
	    }
}
