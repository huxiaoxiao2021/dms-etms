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
}
