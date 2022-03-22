package com.jd.bluedragon.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.nio.charset.Charset;

/**
 * 加密工具类
 */
public class Encrypt {
    private static final Logger LOG = LoggerFactory.getLogger(Encrypt.class);

    /**
     * 方法描述：des加密
     *
     * @param:
     * @return:
     * @version: 1.0
     * @author: libin
     * @version: 2014-2-17 下午5:49:57
     */
    public static String encodeDes(String strIn) {
        try {
            Cipher cipher = getDesCipher("encrypt");
            return byteArr2HexStr(cipher.doFinal(strIn.getBytes(Charset.forName("UTF-8"))));
        } catch (Exception e) {
            LOG.error("加密DES[" + strIn + "]失败", e);
        }
        return null;
    }

    /**
     * 方法描述：des解密
     *
     * @param:
     * @return:
     * @version: 1.0
     * @author: libin
     * @version: 2014-2-17 下午5:50:26
     */
    public static String decodeDes(String strIn) {
        try {
            Cipher cipher = getDesCipher("decode");
            return new String(cipher.doFinal(hexStr2ByteArr(strIn)), "UTF-8");
        } catch (Exception e) {
            LOG.error("解密DES[" + strIn + "]失败", e);
        }
        return null;
    }

    private static Cipher getDesCipher(String type) {
        Cipher cipher = null;
        try {
            DESKeySpec dks = new DESKeySpec("~_~mrd^^".getBytes(Charset.forName("UTF-8")));

            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");

            SecretKey secretKey = keyFactory.generateSecret(dks);
            cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            if ("encrypt".equals(type)) {
                cipher.init(1, secretKey);
            } else {
                cipher.init(2, secretKey);
            }
        } catch (Exception e) {
            LOG.error("初始化DES-Cipher失败", e);
            throw new RuntimeException("初始化dks错误");
        }
        return cipher;
    }

    public static String byteArr2HexStr(byte[] arrB) throws Exception {
        int iLen = arrB.length;
        StringBuffer sb = new StringBuffer(iLen * 2);
        for (int i = 0; i < iLen; i++) {
            int intTmp = arrB[i];
            while (intTmp < 0) {
                intTmp += 256;
            }
            if (intTmp < 16) {
                sb.append("0");
            }
            sb.append(Integer.toString(intTmp, 16).toUpperCase());
        }
        return sb.toString();
    }

    private static byte[] hexStr2ByteArr(String strIn) throws Exception {
        byte[] arrB = strIn.getBytes(Charset.forName("UTF-8"));
        int iLen = arrB.length;

        byte[] arrOut = new byte[iLen / 2];
        for (int i = 0; i < iLen; i += 2) {
            String strTmp = new String(arrB, i, 2, "UTF-8");
            arrOut[(i / 2)] = (byte) Integer.parseInt(strTmp, 16);
        }
        return arrOut;
    }


}
