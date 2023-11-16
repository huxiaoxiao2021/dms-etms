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


    /**
     * 个人信息安全脱敏
     */
    WAYBILL_VAS_SPECIAL_PERSONAL_INFO_SEC("personalInfoSec", "个人信息安全脱敏")

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

        /**
         *个人信息安全脱敏 收件人名字
         */
        PERSONAL_INFO_SEC_RECEIVE_MOBILE("receiveMobile", "Map", "收件人名字"),
        /**
         *个人信息安全脱敏 收件人电话
         */
        PERSONAL_INFO_SEC_RECEIVE_NAME("receiveName", "Map", "收件人电话"),

        /**
         *个人信息安全脱敏 加密信息模式
         */
        PERSONAL_INFO_SEC_ENC_MODE_1("encMode", "1", "加密信息模式"),

        /**
         *个人信息安全脱敏 虚拟号模式
         */
        PERSONAL_INFO_SEC_ENC_MODE_2("encMode", "2", "虚拟号模式"),

        /**
         *个人信息安全脱敏- 虚拟号失效时间
         */
        PERSONAL_INFO_ESC_VIRTUAL_NUMBER_EXPIRE("virtualNumberExpire", "DateTime", "虚拟号失效时间"),



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
