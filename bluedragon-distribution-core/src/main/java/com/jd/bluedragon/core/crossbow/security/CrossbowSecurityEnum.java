package com.jd.bluedragon.core.crossbow.security;

/**
 * <p>
 *     crossbow支持的加密处理方式 这里用枚举表示
 *
 * @author wuzuxiang
 * @since 2019/11/5
 **/
public enum CrossbowSecurityEnum {

    /**
     * 默认的空处理器
     * @see DefaultSecurityProcessor
     */
    default_,

    /**
     * 消息签名插件
     * (插件来自外部服务域)
     * @see MessageSignSecurityProcessor
     */
    message_sign,

    /**
     * Oauth认证插件
     * (插件来自外部服务域)
     */
    o_auto_authentication,

    /**
     * HTTPS认证插件
     * (插件来自外部服务域)
     */
    https_authentication;

}
