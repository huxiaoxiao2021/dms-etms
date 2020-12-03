package com.jd.bluedragon.core.crossbow;

import com.jd.bluedragon.core.crossbow.security.CrossbowSecurityEnum;

/**
 * <p>
 *     crossBow组件调用外部三方接口需要用到的配置，详细流程请见下面链接
 *
 * @link http://lcp.jd.com/services/queryMethodByServiceNameDetail.do?serviceName=com.jd.lsb.component.service.CrossbowService 组件接口文档地址 （组件接口已经弃用，直接使用SDK调用）
 * @link http://lcp.jdwl.com/#/docSoftwareSystem/17/44780 接入流程地址
 * @author wuzuxiang
 * @since 2019/10/17
 **/
public class CrossbowConfig {

    /**
     * 拼多多在物流网关外部服务域中配置的服务域
     */
    private String domain;

    /**
     * 拼多多在物流网关外部服务域中配置的api对应的url
     */
    private String api;

    /**
     * 拼多多订单在外部服务域中配置的appKey（在物流开放平台中入驻）
     */
    private String appKey;

    /**
     * 拼多多订单在外部服务域中配置的appKey对应的appSecret(在物流开放平台中入驻)
     */
    private String appSecret;

    /**
     * 客户标识
     */
    private String customerId;

    /**
     * 配置安全插件的内容
     * 默认处理器，空处理
     */
    private CrossbowSecurityEnum securityEnum = CrossbowSecurityEnum.default_;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public CrossbowSecurityEnum getSecurityEnum() {
        return securityEnum;
    }

    public void setSecurityEnum(CrossbowSecurityEnum securityEnum) {
        this.securityEnum = securityEnum;
    }
}
