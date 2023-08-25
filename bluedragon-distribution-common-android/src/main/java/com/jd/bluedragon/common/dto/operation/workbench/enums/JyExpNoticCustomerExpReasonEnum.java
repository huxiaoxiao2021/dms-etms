package com.jd.bluedragon.common.dto.operation.workbench.enums;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/8/25 10:24
 * @Description:
 */
public class JyExpNoticCustomerExpReasonEnum {

    public enum ExpBusinessIDEnum{

        BUSINESS_ID_HK_HO(413,"港澳业务id"),
        BUSINESS_ID_MAIN_LAND(422,"大陆务id"),
        ;

        private Integer code;
        private String name;


        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        ExpBusinessIDEnum(Integer code, String name) {
            this.code = code;
            this.name = name;
        }
    }

    public enum ExpReasonOneLevelEnum{

        //大陆单 上报客服一级异常原因
        MAIN_LAND_DAMAGE_NO_DOWNLOAD("4220100","破损无法下传"),
        MAIN_LAND_SALE_PACKAGE_DAMAGE("4220200","销售包装破损"),
        MAIN_LAND_CONTRABAND("4220300","违禁品"),

        //港澳单 上报客服一级异常原因
        HA_MO_DELIVERY_EXCEPTION_REPORT("4130100","配送异常报备")
        ;

        private String code;
        private String name;


        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        ExpReasonOneLevelEnum(String code, String name) {
            this.code = code;
            this.name = name;
        }
    }

    public enum ExpReasonTwoLevelEnum{

        //大陆单 上报客服二级异常原因
        MAIN_LAND_PART_DAMAGE("4220101","部分破损"),
        MAIN_LAND_SERIOUS_DAMAGE("4220102","严重破损"),
        MAIN_LAND_CHANGE_PACKAGE("4220201","更换包装"),
        MAIN_LAND_RETURN("4220301","退回"),
        MAIN_LAND_AIR_CHANGE_LAND("4220302","航空转陆运"),




        //港澳单 上报客服二级异常原因
        HA_MO_DAMAGE_NO_DOWNLOAD("4130101","破损无法下传"),
        HA_MO_CONTRABAND_RETURN("4130102","违禁品退回"),
        HA_MO_CONTRABAND_CHANGE_LAND("4130103","违禁品转陆运")
        ;

        private String code;
        private String name;


        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        ExpReasonTwoLevelEnum(String code, String name) {
            this.code = code;
            this.name = name;
        }


        public static ExpReasonTwoLevelEnum getEnumByCode(String code) {
            if (code == null || code.equals("")) {
                return null;
            }
            for (ExpReasonTwoLevelEnum value : ExpReasonTwoLevelEnum.values()) {
                if (code.equals(value.getCode())) {
                    return value;
                }
            }
            return null;
        }

    }

}
