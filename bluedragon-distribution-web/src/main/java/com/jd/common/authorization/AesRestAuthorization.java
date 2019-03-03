package com.jd.common.authorization;

import com.jd.bluedragon.utils.StringHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

/**
 * Created by wangtingwei on 2016/3/10.
 */
@Service("restAuthriztation")
public class AesRestAuthorization implements RestAuthorization {

    private static final Log logger= LogFactory.getLog(AesRestAuthorization.class);

    @Override
    public boolean authorize(String key, String token, String requestTime) {
        if (StringHelper.isEmpty(key)||StringHelper.isEmpty(token)||StringHelper.isEmpty(requestTime)){
            return false;
        }
        byte[] sourceArr=encrypt(key,RestAuthorization.PASS_WORD_PREFIX);
        if(sourceArr == null){
            return false;
        }
        String middleAuthorization=parseByte2HexStr(sourceArr);
        //byte[] targetArr=encrypt(middleAuthorization,requestTime);
        //String targetAuthorization=parseByte2HexStr(targetArr);
        return middleAuthorization.equals(token);
    }

    @Override
    public boolean authorizeDateTime(String requestTime) {
        return true;
    }

    /**
     * 生成授权码
     * @param key 序列号
     * @return
     */
    @Override
    public String generateAuthorizationCode(String key) {
        if(StringHelper.isEmpty(key)){
            return null;
        }
        byte[] sourceArr=encrypt(key,RestAuthorization.PASS_WORD_PREFIX);
        if(sourceArr == null){
            return null;
        }
        String middleAuthorization=parseByte2HexStr(sourceArr);
        return middleAuthorization;
    }

    /**
     * 加密
     *
     * @param content 需要加密的内容
     * @param password  加密密码
     * @return
     */
    private static byte[] encrypt(String content, String password) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(password.getBytes());
            kgen.init(128, random);
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            byte[] byteContent = content.getBytes("utf-8");
            cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
            byte[] result = cipher.doFinal(byteContent);
            return result; // 加密
        } catch (Throwable throwable){
            logger.error("AESREST加密",throwable);
        }
        return null;
    }

    /**解密
     * @param content  待解密内容
     * @param password 解密密钥
     * @return
     */
    private static byte[] decrypt(byte[] content, String password) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(password.getBytes());
            kgen.init(128, random);
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            cipher.init(Cipher.DECRYPT_MODE, key);// 初始化
            byte[] result = cipher.doFinal(content);
            return result; // 加密
        } catch (Throwable throwable){
            logger.error("AESREST解密", throwable);
        }
        return null;
    }

    /**将二进制转换成16进制
     * @param buf
     * @return
     */
    private static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**将16进制转换为二进制
     * @param hexStr
     * @return
     */
    private static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length()/2];
        for (int i = 0;i< hexStr.length()/2; i++) {
            int high = Integer.parseInt(hexStr.substring(i*2, i*2+1), 16);
            int low = Integer.parseInt(hexStr.substring(i*2+1, i*2+2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }
}
