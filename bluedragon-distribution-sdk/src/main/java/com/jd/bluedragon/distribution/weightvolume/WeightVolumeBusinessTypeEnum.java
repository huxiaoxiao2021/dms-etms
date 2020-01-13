package com.jd.bluedragon.distribution.weightvolume;

/**
 * <p>
 *     分拣称重流水业务场景类型枚举
 *
 * @author wuzuxiang
 * @since 2020/1/8
 **/
public enum WeightVolumeBusinessTypeEnum {

    /**
     * 分拣交接
     */
    DMS_HANDOVER,

    /**
     * 终端站点交接
     * 这个枚举的存在背景：加盟商业务分拣做了一个交接称重的业务，终端站点的不想自己写了，就调用了我们的JSF接口...
     */
    TERMINAL_SITE_HANDOVER,

    /**
     * 分拣自动化测量
     */
    DMS_AUTOMATIC_MEASURE,

    /**
     * 分拣客户端测量
     */
    DMS_CLIENT_MEASURE,

    /**
     * 分拣手工录入
     */
    DMS_MANUAL_LOGGED;

}
