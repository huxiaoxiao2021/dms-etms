package com.jd.bluedragon.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.xml.security.utils.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Md5Helper {

	private static final Logger log = LoggerFactory.getLogger(Md5Helper.class);
    
    public static String encode(String str) {
        return DigestUtils.md5Hex(str).toUpperCase();
    }
    
    public static String encode3PL(String str) {
        try {
			return URLEncoder.encode(Base64.encode(MessageDigest.getInstance("md5").digest
					(str.getBytes("UTF-8"))),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			return "1";
		} catch (NoSuchAlgorithmException e) {
			return "1";
		}
    }

    public static byte[] getMD5(String msg) {
    	try {
    		MessageDigest messageDigest = MessageDigest.getInstance("MD5");
    		messageDigest.update(msg.getBytes(StandardCharsets.UTF_8));
    		return messageDigest.digest();
		} catch (NoSuchAlgorithmException e) {
    		log.error("算法实例加载错误，无法加载MD5的算法对象");
			return new byte[]{};
		} catch (RuntimeException e) {
    		log.error("MD5算法失败，运行异常，参数为：{}", msg, e);
    		return new byte[]{};
		}
 	}
    public static void main(String[] args) throws NoSuchAlgorithmException, UnsupportedEncodingException{
    	String tmp1 = encode("wms");
    	System.err.println(tmp1);
    	System.err.println(new String(
    			MessageDigest.getInstance("md5").digest
				("wms".getBytes("UTF-8")),"UTF-8"));
    	String tmp2 = encode(tmp1+"wms-packagePrint");
    	System.err.println(tmp2);
    }
}
