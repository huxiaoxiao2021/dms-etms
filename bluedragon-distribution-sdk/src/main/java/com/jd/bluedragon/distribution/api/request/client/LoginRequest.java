package com.jd.bluedragon.distribution.api.request.client;

import java.io.Serializable;

/**
 * 登录入参
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2022-12-06 19:21:01 周二
 */
public class LoginRequest implements Serializable {

    private static final long serialVersionUID = 6241259710411733650L;

    /**
     * 用户ERP
     */
    private String userErp;

    /**
     * 密码
     */
    private String password;

    /**
     * 设备信息
     */
    private DeviceInfo deviceInfo;

    /**
     * 操作时间
     */
    private Long operateTime;
}
