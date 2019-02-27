package com.jd.common.authorization;
import java.util.*;
/**
 * REST授权验证
 * Created by wangtingwei on 2016/3/9.
 */
public interface RestAuthorization {

    static String PASS_WORD_PREFIX ="RESTAUTHORIZATION";

    /**
     * 注册号
     */
    static String REGISTER_NO="RegisterNo";

    /**
     * 授权码
     */
    static String AUTHORIZATION="Authorization";

    /**
     * 上传时间
     */
    static String DATE="Date";

    /**
     * 授权认证失败
     */
    static Integer NO_AUTHORIZE=401;

    /**
     * 认证失败提示消息
     */
    static String NO_AUTHORIZE_MESSAGE="授权未通过，请检查HTTP头，RegisterNo与Authorization配置";

    /**
     * 时间间隔超过5分钟错误号
     */
    static Integer DATE_DIFF_NOT_MATCH=4002;

    /**
     * 时间间隙不一致提示信息
     */
    static String DATE_DIFF_NOT_MATCH_MESSAGE="消息发送时间与服务器时间差距超过五分钟,请检查HTTP头DATE";

    /**
     * 验证授权
     * @param key 公钥
     * @param token 密文
     * @param requestTime 请求时间[yyyy-MM-dd HH:mm:ss]
     * @return
     */
    boolean authorize(String key,String token,String requestTime);

    /**
     * 认证时间【请求客户端时间与服务器时间间隙在五分钟以内，认证通过，否则认证失败】
     * @param requestTime 请求时间[yyyy-MM-dd HH:mm:ss]
     * @return
     */
    boolean authorizeDateTime(String requestTime);

    /**
     * 生成授权码
     * @param key 序列号
     * @return
     */
    String generateAuthorizationCode(String key);
}
