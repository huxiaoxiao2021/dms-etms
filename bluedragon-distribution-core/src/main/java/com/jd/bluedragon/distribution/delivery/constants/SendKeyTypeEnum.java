package com.jd.bluedragon.distribution.delivery.constants;

/**
 * @ClassName SendKeyTypeEnum
 * @Description
 * @Author wyh
 * @Date 2021/8/3 21:21
 **/
public enum SendKeyTypeEnum {

    /**
     * 按运单发货
     */
    BY_WAYBILL,

    /**
     * 按包裹发货
     */
    BY_PACKAGE,

    /**
     * 按箱号发货
     */
    BY_BOX,
    /**
     * 按照板号发货
     */
    BY_BOARD,
    /**
     * 按批次号
     */

    BY_SENDCODE;

    public static SendKeyTypeEnum findEnum(String name) {
        for (SendKeyTypeEnum value : SendKeyTypeEnum.values()) {
            if (value.name().equals(name)) {
                return value;
            }
        }
        return null;
    }
}
