package com.jd.bluedragon.distribution.weightAndVolumeCheck;


/**
 * 抽检来源枚举
 *
 * @author: hujiping
 * @date: 2020/8/3 15:14
 */
public enum SpotCheckSourceEnum {

    /**
     * 打印客户端：平台打印
     * */
    SPOT_CHECK_CLIENT_PLATE,
    /**
     * 自动化设备：DWS
     * */
    SPOT_CHECK_DWS,
    /**
     * B网抽检：网页
     * */
    SPOT_CHECK_DMS_WEB,
    /**
     * B网抽检：安卓
     * */
    SPOT_CHECK_ANDROID,
    /**
     * 其他
     * */
    SPOT_CHECK_OTHER

}
