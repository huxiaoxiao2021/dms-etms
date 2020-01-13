package com.jd.bluedragon.distribution.weightvolume;

/**
 * <p>
 *     对接分拣web的系统上游数据来源的系统编码枚举
 *
 * @author wuzuxiang
 * @since 2020/1/8
 **/
public enum FromSourceEnum {

    /**
     * 分拣PDA程序
     */
    DMS_PDA,

    /**
     * 分拣安卓程序
     */
    DMS_ANDROID,

    /**
     * 分拣web页面
     */
    DMS_WEB,

    /**
     * 分拣桌面程序
     */
    MDS_DESK_CLIENT,

    /**
     * 分拣自动化龙门架设备
     */
    DMS_AUTOMATIC_MACHINE,

}
