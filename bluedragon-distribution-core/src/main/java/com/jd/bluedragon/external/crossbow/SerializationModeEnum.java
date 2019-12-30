package com.jd.bluedragon.external.crossbow;

/**
 * <p>
 *     定义序列化反序列化方式
 *
 * @author wuzuxiang
 * @since 2019/12/18
 **/
public enum SerializationModeEnum {

    /**
     * XML序列化反序列化方式
     */
    XML,

    /**
     * JSON序列化反序列化方式
     */
    JSON,

    /**
     * SOAP的报文
     */
    SOAP,

    /**
     * SOAP的报文，使用base64加密消息体
     */
    SOAP_BASE64;

}
