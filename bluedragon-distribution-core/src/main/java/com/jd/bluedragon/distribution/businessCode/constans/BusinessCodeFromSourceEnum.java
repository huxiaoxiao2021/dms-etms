package com.jd.bluedragon.distribution.businessCode.constans;

/**
 * <p>
 *     业务单号的来源枚举
 *
 * @author wuzuxiang
 * @since 2020/2/25
 **/
public enum BusinessCodeFromSourceEnum {

    /**
     * 分拣桌面打印客户端
     */
    DMS_DESKTOP_CLIENT,

    /**
     * 分拣web页面
     */
    DMS_WEB_SYS,

    /**
     * 分拣worker系统
     */
    DMS_WORKER_SYS,

    /**
     * 分拣自动化worker系统
     */
    DMS_AUTOMATIC_WORKER_SYS,

    /**
     * 分拣自动化系统
     */
    DMS_AUTOMATIC_WEB_SYS;

}
