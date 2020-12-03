package com.jd.bluedragon.distribution.waybill.domain;

/**
 * @author lixin39
 * @Description TODO
 * @ClassName WaybillCancelInterceptTypeEnum
 * @date 2019/8/8
 */
public enum WaybillCancelInterceptTypeEnum {

    /**
     * 取消订单拦截
     */
    CANCEL(1, "取消订单拦截"),

    /**
     * 拒收订单拦截
     */
    REFUSE(2, "拒收订单拦截"),

    /**
     * 恶意订单拦截
     */
    MALICE(3, "恶意订单拦截"),

    /**
     * 分拣中心拦截
     */
    SORTING(4, "分拣中心拦截"),

    /**
     * 仓储异常拦截
     */
    STORAGE_EXCEPTION(5, "仓储异常拦截"),

    /**
     * 白条强制拦截
     */
    WHITE(6, "白条强制拦截"),

    /**
     * 仓储病单拦截
     */
    STORAGE_SICK(7, "仓储病单拦截"),

    /**
     * 伽利略拒收订单拦截
     */
    GALILEO(8, "伽利略拒收订单拦截"),

    /**
     * 京沃取消订单拦截
     */
    JD_WMT(9, "京沃取消订单拦截"),

    /**
     * 理赔拦截
     */
    COMPENSATE(10, "理赔拦截"),

    /**
     * 取消订单拦截；仓已拦截成功
     */
    CANCEL_STORAGE(11, "取消订单拦截；仓已拦截成功"),

    /**
     * 理赔破损拦截
     */
    CLAIM_DAMAGED(12, "理赔破损拦截");

    private final int code;

    private final String desc;

    WaybillCancelInterceptTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return this.code;
    }

    public String getDesc() {
        return this.desc;
    }

    public static WaybillCancelInterceptTypeEnum getEnum(int code) {
        for (WaybillCancelInterceptTypeEnum typeEnum : WaybillCancelInterceptTypeEnum.values()) {
            if (typeEnum.getCode() == code) {
                return typeEnum;
            }
        }
        return null;
    }
}
