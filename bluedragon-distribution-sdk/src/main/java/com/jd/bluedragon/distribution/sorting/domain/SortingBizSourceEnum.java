package com.jd.bluedragon.distribution.sorting.domain;

public enum SortingBizSourceEnum {

    /**
     * PDA：自营分拣-分拣理货-小件分拣
     */
    PDA_SELF_SORTING_SMALL_PACK(61, "PDA自营分拣-小件分拣"),

    /**
     * PDA：自营分拣-分拣理货-大件分拣
     */
    PDA_SELF_SORTING_LARGE_PACK(62, "PDA自营分拣-大件分拣"),

    /**
     * PDA：自营分拣-批量分拣
     */
    PDA_SELF_SORTING_BATCH(63, "PDA自营分拣-批量分拣"),

    /**
     * PDA：自营分拣-接货仓-分拣理货
     */
    PDA_SELF_SORTING_RECEIVING_WAREHOUSE(64, "PDA自营分拣-接货仓-分拣理货"),

    /**
     * PDA：逆向分拣-分拣理货
     */
    PDA_REVERSE_SORTING(65, "PDA逆向分拣"),

    /**
     * PDA：三方分拣-分拣
     */
    PDA_THIRD_SORTING(66, "PDA三方分拣"),

    /**
     * PDA：离线分拣-小件分拣
     */
    PDA_OFFLINE_SELF_SORTING_SMALL_PACK(67, "PDA离线-自营分拣-小件分拣"),

    /**
     * PDA: 离线分拣-大件分拣
     */
    PDA_OFFLINE_SELF_SORTING_LARGE_PACK(68, "PDA离线-自营分拣-大件分拣"),

    /**
     * PDA: 离线分拣-三放分拣
     */
    PDA_OFFLINE_THIRD_SORTING(69, "PDA离线-三方分拣"),

    /**
     * 安卓：分拣-分拣理货
     */
    ANDROID_SORTING(70, "安卓-分拣-分拣理货"),

    /**
     * 安卓：分拣-批量分拣
     */
    ANDROID_SORTING_BATCH(71, "安卓-分拣-批量分拣"),

    /**
     * 开放平台分拣
     */
    OPEN_PLAT_SORTING(72, "开放平台分拣"),

    /**
     * 龙门架分拣
     */
    GANTRY_SORTING(73, "龙门架分拣"),

    /**
     * 分拣机分拣
     */
    AUTOMATIC_SORTING_MACHINE_SORTING(74, "分拣机分拣"),

    ;

    /**
     * 编码
     */
    private final Integer code;

    /**
     * 名称
     */
    private final String name;

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    SortingBizSourceEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }
}
