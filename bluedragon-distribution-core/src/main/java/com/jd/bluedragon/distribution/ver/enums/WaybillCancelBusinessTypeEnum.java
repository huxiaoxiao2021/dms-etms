package com.jd.bluedragon.distribution.ver.enums;

/**
 * <P>
 *     dms_basic.waybill_cancel表的businessType值枚举类
 * </p>
 * <code>1</code> 锁定
 * <code>2</code> 解锁
 *
 * @author wuzuxiang
 * @date 2019/3/27
 */
public enum WaybillCancelBusinessTypeEnum {

    /**
     * 锁定：1
     */
    LOCK(1,"锁定"),

    /**
     * 解锁：2
     */
    UNLOCK(2,"解锁");

    /* 枚举值 */
    private Integer code;

    /* 枚举值说明 */
    private String name;

    /**
     * 枚举类的构造参数
     * @param code 枚举值
     * @param name 枚举值说明
     */
    WaybillCancelBusinessTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
