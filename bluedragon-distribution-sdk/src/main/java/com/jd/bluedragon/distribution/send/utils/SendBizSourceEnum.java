package com.jd.bluedragon.distribution.send.utils;

/**
 * @author lixin39
 * @Description 发货业务来源入口
 * @ClassName SendBizSourceEnum
 * @date 2019/3/20
 */
public enum SendBizSourceEnum {
    /**
     * 旧发货
     * 包括：1-旧发货功能、2-批量旧发货功能
     */
    OLD_PACKAGE_SEND(1, "旧发货"),

    /**
     * 新发货（一车一单发货）
     * 包括：1-一车一单发货、2-批量一车一单发货、3-无人分拣发货
     */
    NEW_PACKAGE_SEND(2, "一车一单发货"),

    /**
     * 批量旧发货(无单独接口)
     */
    BATCH_OLD_PACKAGE_SEND(3, "批量旧发货"),

    /**
     * 批量新发货（批量一车一单发货）
     */
    BATCH_NEW_PACKAGE_SEND(4, "批量新发货"),

    /**
     * 离线老发货
     */
    OFFLINE_OLD_SEND(5, "离线老发货"),

    /**
     * 离线新发货
     */
    OFFLINE_NEW_SEND(6, "离线新发货"),

    /**
     * 组板发货
     */
    BOARD_SEND(7, "组板发货"),

    /**
     * 离线组板发货
     */
    OFFLINE_BOARD_SEND(8, "离线组板发货"),

    /**
     * 快运发货
     */
    RAPID_TRANSPORT_SEND(9, "快运发货"),

    /**
     * 逆向发货
     */
    REVERSE_SEND(10, "逆向发货"),

    /**
     * 整批转发发货
     */
    BATCH_FORWARD_SEND(11, "整批转发发货"),

    /**
     * 龙门架发货
     */
    SCANNER_FRAME_SEND(12, "龙门架发货"),

    /**
     * 分拣机发货
     */
    SORT_MACHINE_SEND(13, "分拣机发货"),

    /**
     * 无人分拣发货
     */
    UNMANNED_SORT_SEND(14, "无人分拣发货"),

    /**
     * 空铁离线一车一单发货
     */
    OFFLINE_AR_NEW_SEND(15, "空铁离线一车一单发货"),

    /**
     * 安卓版本PDA发货
     */
    ANDROID_PDA_SEND(16, "安卓PDA发货"),

    /**
     * 开放平台发货
     */
    OPEN_PLATFORM_SEND(17, "开放平台发货"),

    /**
     * 冷链发货
     */
    COLD_CHAIN_SEND(18, "冷链发货"),

    /**
     * 极速版本PDA发货
     */
    EXTREME_SPEED_SEND(19, "极速版本PDA发货"),


    /**
     * 安卓版本PDA装车发货
     */
    ANDROID_PDA_LOAD_SEND(20, "安卓PDA装车发货"),

    /**
     * 按运单发货
     */
    WAYBILL_SEND(21, "按运单发货"),

    /**
     * 冷链装车快运发货
     */
    COLD_LOAD_CAR_KY_SEND(22, "冷链装车快运发货"),

    /**
     * 冷链装车发货
     */
    COLD_LOAD_CAR_SEND(23, "冷链装车发货"),

    /**
     * 拣运APP发车
     */
    JY_APP_SEND(24, "拣运APP发车"),

    /**
     * 拣运APP传摆发货
     */
    JY_APP_TRANSFER_AND_FERRY_SEND(25, "拣运APP传摆发货"),

    /**
     * 按包裹无任务揽收自动发货
     */
    COLD_CHAIN_AUTO_SEND(26, "无任务揽收自动发货"),

    /**
     * B冷链装车发货-新
     * 一单单发货
     */
    COLD_LOAD_CAR_SEND_NEW(27, "B冷链装车发货-新"),

    /**
     * 快运装车发货-新
     * 一单单发货
     */
    COLD_LOAD_CAR_KY_SEND_NEW(28, "冷链快运装车发货-新"),

    /**
     * 德邦融合-嘉峪关项目
     */
    DPRH(29,"德邦发货")
    ;

    /**
     * 编码
     */
    private Integer code;

    /**
     * 名称
     */
    private String name;

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    /**
     * 构造方法
     *
     * @param code 编码
     * @param name 名称
     */
    SendBizSourceEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static SendBizSourceEnum getEnum(Integer code) {
        if (code != null) {
            for (SendBizSourceEnum bizSource : SendBizSourceEnum.values()) {
                if (bizSource.getCode().equals(code)) {
                    return bizSource;
                }
            }
        }
        return null;
    }
}
