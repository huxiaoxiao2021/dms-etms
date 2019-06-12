package com.jd.bluedragon;
/**
 * 
 * @ClassName: UmpConstants
 * @Description: Ump相关的常量
 * @author: wuyoude
 * @date: 2019年5月14日 上午11:18:56
 *
 */
public interface UmpConstants {
    /**
     * UMP监控key
     */
    String UMP_KEY_BASE= "dmsWeb.";
    /**
     * jsf客户端监控key前缀
     */
    String UMP_KEY_JSF_CLIENT= UMP_KEY_BASE + "jsf.client.";
    /**
     * jsf服务端监控key前缀
     */
    String UMP_KEY_JSF_SERVER= UMP_KEY_BASE + "jsf.server.";
}
