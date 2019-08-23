package com.jd.bluedragon.distribution.external.gateway;

import sun.misc.BASE64Encoder;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * 物流网关客户端参数 组装类
 * 本地测试可以参考此类 打印方法，用户postman测试
 * b2b客户端调用例子：http://suo.im/5eZ2Gg
 * 服务域模式与b2b模式的区别是在请求里添加了一个参数LOP-DN=dms-u.etms.jd.com；cf链接http://suo.im/4ZWcqD
 * 如：https://网关地址/xxxxxx?LOP-DN=dms-u.etms.jd.com（在网关申请的服务域，注意参数名称为大写字母） ，或者在Http头信息中加入名称LOP-DN，值dms-u.etms.jd.com
 * @author : xumigen
 * @date : 2019/6/28
 */
public class HmacUtil {
    public enum Algorithm {
        hmac_sha1("hmac-sha1"),md5_salt("md5-salt");
        private String name;
        Algorithm(String name){
            this.name=name;
        }
        public String getName() {
            return name;
        }
    }
    private static final String MAC_NAME = "HmacSHA1";
    private static final String ENCODING = "UTF-8";
    //获取GMT时间（注意时区问题，非GMT的一定要转成GMT）
    private static String getServerTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(calendar.getTime());
    }
    //获取MD5签名摘要
    public static String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }//获取hamc-sha1签名摘要
    private static byte[] HmacSHA1Encrypt(String encryptText, String encryptKey) throws Exception {
        byte[] data=encryptKey.getBytes(ENCODING);
        //根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
        SecretKey secretKey = new SecretKeySpec(data, MAC_NAME);
        //生成一个指定 Mac 算法 的 Mac 对象
        Mac mac = Mac.getInstance(MAC_NAME);
        //用给定密钥初始化 Mac 对象
        mac.init(secretKey);
        byte[] text = encryptText.getBytes(ENCODING);
        //完成 Mac 操作
        byte[] digest = mac.doFinal(text);
        //注意此处进行了base64编码
        return digest ;
    }

    /******
     * 获取Hmac签名头信息，调用方需要将此方法生成的头信息放到HTTP头信息中
     * @param userName 签名用户名
     * @param secret 签名密钥
     * @param algorithm 签名算法
     * @param extendSignProperties 签名扩展属性，可选
     * @return
     */
    public static Map<String,String> makeHmacHeaders(String userName,String secret,Algorithm algorithm,Map<String, String> extendSignProperties){
        if(userName==null || secret ==null || algorithm==null ){
            throw new IllegalArgumentException("用户名/密码/签名算法都不能为空");
        }
        Map<String,String> result=new HashMap<String, String>();
        String X_Date= getServerTime() ;
        String txt="X-Date: " + X_Date;
        String headers="X-Date";
        result.put("X-Date",X_Date);
        if (extendSignProperties!=null) {
            for (Map.Entry<String, String> entry : extendSignProperties.entrySet()) {
                txt = txt + "\n" + entry.getKey() + ": " + entry.getValue();
                headers = headers + " " + entry.getKey();
                result.put(entry.getKey(), entry.getValue());
            }
        }
        String sign=null;
        if (algorithm == Algorithm.hmac_sha1) {
            //hmac-sha1算法获取签名串方法
            try {
                byte[] digest = HmacSHA1Encrypt(txt, secret);
                //对摘要进行base64编码
                sign = new BASE64Encoder().encode(digest);
            }catch (Exception e){
                throw new RuntimeException(e);
            }
        }else if (algorithm == Algorithm.md5_salt) {
            //md5-salt算法获取签名串方法
            String md5Sign = MD5(secret + txt + secret);
            sign = md5Sign.toLowerCase(); //如果已经是小写了则不需要转
        }else
        {
            throw new RuntimeException("不支持的签名算法:"+algorithm.getName());
        }
        result.put("Authorization", "hmac username=\""+userName+"\", algorithm=\""+algorithm.getName()+"\", headers=\""+headers+"\",signature=\""+sign+"\"");
        return result;
    }

    public static void main(String[] args) {
        // 参数说明：
        // userName:用户名：由服务提供方提供
        // secret:密码：由服务提供方提供
        // algorithm 算法类型，一般使用hmac_sha1，若接入方系统不支持hmac_sha1算法，可以选择使用md5_salt算法，需要调用方参考算法的java代码自行实现。
        // extendProperties 扩展属性集合：扩展属性用于增加签名生成的复杂度，提高网关调用的安全性，可以根据需要自行增加，也可以不定义扩展属性
        Map<String, String> extendProperties = new HashMap<String, String>();
        //例如需要支持防篡改，可以增加属性名称为md5-content扩展属性（属性名称可自定义），值为请求体的Md5摘要
        extendProperties.put("md5-content",HmacUtil.MD5("{\n" +
                "  \"operateType\" : 10,\n" +
                "  \"packageCode\" : \"JD0000000184267\",\n" +
                "  \"createSiteCode\" : 910,\n" +
                "  \"createSiteName\" : \"\",\n" +
                "  \"operateUserCode\" : 0,\n" +
                "  \"operateUserName\" : \"\",\n" +
                "  \"operateTime\" : \"\",\n" +
                "  \"receiveSiteCode\" : 910,\n" +
                "  \"businessType\" : 10,\n" +
                "  \"boxCode\" : \"JD0000000184267\",\n" +
                "  \"isLoss\" : 0\n" +
                "}"));
        Map<String, String> headers = HmacUtil.makeHmacHeaders("pdaandroiduseruat","1235pdaandroiduseruat", Algorithm.hmac_sha1, extendProperties);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            System.out.println(entry.getKey() + "="+ entry.getValue());
        }
        /******
         * 上述代码参考输出如下，请将生成的这些字段放到HTTP请求头中一起发送给网关
         * Authorization=hmac username="user1", algorithm="hmac-sha1", headers="X-Date md5-content",signature="UNSFSTZbiUJUnP2bXGbeQY2Ixjk="
         * X-Date=Wed, 13 Mar 2019 09:21:56 GMT
         * md5-content=ee2ae9240c717598de2a3cb86bc9fa1d
         */
        System.out.println(new Date());
    }
}