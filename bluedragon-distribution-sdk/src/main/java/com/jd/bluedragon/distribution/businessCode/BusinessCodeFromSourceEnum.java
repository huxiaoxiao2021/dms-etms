package com.jd.bluedragon.distribution.businessCode;

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
     * 分拣WincePDA
     */
    DMS_WINCE_PDA,

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
    DMS_AUTOMATIC_WEB_SYS,
    /**
     * 拣运应用
     */
    JY_APP,

    /**
     * 德邦融合项目-外部门创建
     */
    DBRH,
    /**
     * 分拣app
     */
    DMS_APP,
    /**
     * 未知来源
     */
    UNKNOWN_SYS;

    /**
     * 从String 中获取到当前枚举
     * @param name
     * @return
     */
    public static BusinessCodeFromSourceEnum getFromName(String name) {
        for (BusinessCodeFromSourceEnum item : BusinessCodeFromSourceEnum.values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return UNKNOWN_SYS;

    }

}
