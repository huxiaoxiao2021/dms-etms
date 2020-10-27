package com.jd.bluedragon.distribution.test.utils;

public class UtilsForTestCase {
	/**
	 * 生成一个打标的字符串
	 * @param length 总长度
	 * @param position 打标位
	 * @param signChar 打标值
	 * @return
	 */
    public static String getSignString(int length, int position ,char signChar){
    	char[] chars = new char[length];
    	for(int i=0;i<length;i++){
    		if(i==(position-1)){
    			chars[i] = signChar;
    		}else{
    			chars[i] = '0';
    		}
    	}
    	return new String(chars);
    }
	/**
	 * 给字符串打标
	 * @param 字符串 
	 * @param position 打标位
	 * @param signChar 打标值
	 * @return
	 */
    public static String markSignChar(String str, int position ,char signChar){
    	if(str != null){
    		if(str.length() >= position){
    			char[] chars = str.toCharArray();
    			chars[position-1] = signChar;
    			return new String(chars);
    		}else{
    			int addLength = position-str.length();
    			return str+getSignString(addLength,addLength,signChar);
    		}
    	}else{
    		return getSignString(position,position,signChar);
    	}
    }
}
