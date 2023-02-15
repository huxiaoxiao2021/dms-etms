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

	/**
	 * 获取字符串的MD5密文
	 *
	 * @param s
	 * @return
	 */
	public static String getMd5(String s) {
		return getMd5(s, "UTF-8");
	}


	/**
	 * 获取字符串的MD5密文
	 *
	 * @param s
	 * @return
	 */
	public static String getMd5(String s,String  charset) {
		String md5 = "";
		final char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
		try {
			byte[] btInput = s.getBytes(charset);
			// 获得MD5摘要算法的 MessageDigest 对象
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			// 使用指定的字节更新摘要
			mdInst.update(btInput);
			// 获得密文
			byte[] md = mdInst.digest();
			// 把密文转换成十六进制的字符串形式
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte per_byte = md[i]; // 转换成16进制（2位数字）
				str[k++] = hexDigits[per_byte >>> 4 & 0xf];
				str[k++] = hexDigits[per_byte & 0xf];
			}
			// 截取中间16位
			md5 = new String(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return md5;
	}
}
