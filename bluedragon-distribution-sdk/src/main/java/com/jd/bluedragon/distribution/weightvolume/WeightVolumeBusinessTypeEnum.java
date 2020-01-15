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
     * 安包裹称重量方
     */
    BY_PACKAGE,

    /**
     * 按运单称重量方
     */
    BY_WAYBILL,

    /**
     * 按箱号称重量方
     */
    BY_BOX,

    /**
     * 交接称重量方
     */
    BY_HANDOVER;

}
