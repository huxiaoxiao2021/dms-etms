package com.jd.bluedragon.core.jsf.adapter;

import lombok.Data;

/**
 * 字节解密服务返回值
 */
@Data
public class AdapterOutOfPlatformDecryRouter {
    /**
     * 返回returnCode，0为成功，其他为失败
     */
    String returnCode;

    /**
     * 返回描述
     */
    String message;
    /**
     * 是否成功
     */
    Boolean result;
    /**
     * 返回数据结构体，失败时为空
     */
    AdapterData data;


    @Data
    public class AdapterData {
        /**
         *商家订单号(唯一，可用于幂等性判断
         */
        String orderId;
        /**
         * 电子面单号
         */
        String waybillCode;
        /**
         * 发件人信息
         */
        AdapterContact sender;
        /**
         * 收件人信息
         */
        AdapterContact receiver;

    }
    @Data
    public class AdapterContact {
        /**
         * 姓名
         */
        String name;
        /**
         * 手机号，与phone至少有一个
         */
        String mobile;
        /**
         * 电话，与mobile至少有一个
         */
        String phone;
        /**
         * 虚拟手机号，若该字段有值，phone/mobile字段为空值
         */
        String virtualMobile;
        /**
         * 虚拟手机号过期时间，和virtualMobile同步有值或者同步为空
         */
        String virtualMobileTime;
    }
}

