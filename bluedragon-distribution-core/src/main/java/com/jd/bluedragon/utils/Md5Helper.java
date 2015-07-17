package com.jd.bluedragon.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.xml.security.utils.Base64;

public class Md5Helper {
    
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
}
