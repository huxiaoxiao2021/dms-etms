package com.jd.bluedragon.distribution.waybill.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 增值服务枚举
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2023-08-10 09:42:54 周四
 */
public enum WaybillVasEnum {

    /**
     * 特安
     */
    WAYBILL_VAS_SPECIAL_SAFETY("ed-a-0047", "特安"),

    /**
     * 特殊保障
     */
    WAYBILL_VAS_SPECIAL_SAFEGUARD("specialSafeguard", "特殊保障"),

    /**
     * 生鲜特保
     */
    WAYBILL_VAS_SPECIAL_SAFEGUARD_COLD_FRESH_OPERATION("specialSafeguard_coldFreshOperation", "生鲜特保"),

    ;

    public static Map<String, String> ENUM_MAP;

    public static List<String> ENUM_LIST;

    private String code;

    private String desc;

    static {
        //将所有枚举装载到map中
        ENUM_MAP = new HashMap<String, String>();
        ENUM_LIST = new ArrayList<String>();
        for (WaybillVasEnum enumItem : WaybillVasEnum.values()) {
            ENUM_MAP.put(enumItem.getCode(), enumItem.getDesc());
            ENUM_LIST.add(enumItem.getCode());
        }
    }

    /**
     * 通过code获取name
     *
     * @param code 编码
     * @return string
     */
    public static String getEnumNameByCode(String code) {
        return ENUM_MAP.get(code);
    }

    WaybillVasEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * 增值服务扩展属性枚举
     */
    public enum WaybillVasOtherParamEnum{

        /**
         * 保障类型-生鲜
         */
        GUARANTEE_TYPE_COLD_FRESH_OPERATION("guaranteeType", "coldFreshOperation", "生鲜"),
        ;

        public static Map<String, String> ENUM_MAP;

        public static List<String> ENUM_LIST;

        private String code;
        private String value;

        private String desc;

        static {
            //将所有枚举装载到map中
            ENUM_MAP = new HashMap<String, String>();
            ENUM_LIST = new ArrayList<String>();
            for (WaybillVasEnum enumItem : WaybillVasEnum.values()) {
                ENUM_MAP.put(enumItem.getCode(), enumItem.getDesc());
                ENUM_LIST.add(enumItem.getCode());
            }
        }

        /**
         * 通过code获取name
         *
         * @param code 编码
         * @return string
         */
        public static String getEnumNameByCode(String code) {
            return ENUM_MAP.get(code);
        }

        WaybillVasOtherParamEnum(String code, String value, String desc) {
            this.code = code;
            this.value = value;
            this.desc = desc;
        }

        public String getCode() {
            return code;
        }

        public String getValue() {
            return value;
        }

        public String getDesc() {
            return desc;
        }

    }
}
