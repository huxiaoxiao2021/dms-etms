package com.jd.bluedragon.distribution.jy.enums;

import com.jd.bluedragon.distribution.api.enums.OperatorTypeEnum;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/9/14 21:14
 * @Description:
 */
public class SiteTypeLevel {

    /**
     * 一级站点类型
     */
    public enum SiteTypeOneLevelEnum{

        TERMINAL_SITE(4,"营业部"),
        THIRD_PARTY(16,"第三方")

        ;

        private Integer code;

        private String name;

        SiteTypeOneLevelEnum(Integer code, String name) {
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

    /**
     * 二级站点类型
     */
    public enum SiteTypeTwoLevelEnum{
        TERMINAL_SITE(4,"营业部"),
        CONVENIENT_SERVICE_POINT(46,"便民服务点"),
        DEEP_COOPERATION_SELF_PICKUP_CABINETS(48,"深度合作自提柜"),
        CAMPUS_JD_SCHOOL(128,"校园京东派"),
        SHARE_DISTRIBUTION_STATION(1605,"共配站"),
        ;

        private Integer code;

        private String name;

        SiteTypeTwoLevelEnum(Integer code, String name) {
            this.code = code;
            this.name = name;
        }

        public Integer getCode() {
            return code;
        }

        public String getName() {
            return name;
        }

        /**
         * 根据code获取enum
         * @param code
         * @return
         */
        public static SiteTypeTwoLevelEnum getEnum(Integer code) {
            for (SiteTypeTwoLevelEnum value : SiteTypeTwoLevelEnum.values()) {
                if (value.code.equals(code)) {
                    return value;
                }
            }
            return null;
        }
    }

    /**
     * 三级站点类型
     */
    public enum SiteTypeThreeLevelEnum{
        TERMINAL_SITE(1,"营业部"),
        CAMPUS_SCHOOL(12801,"校园派"),
        JD_STAR_DISTRIBUTION(12802,"京东星配"),
        TOWN_SHARE_DISTRIBUTION_STATION(160501,"乡镇共配站"),
        CITY_SHARE_DISTRIBUTION_STATION(160502,"城市共配站"),
        ;

        private Integer code;

        private String name;

        SiteTypeThreeLevelEnum(Integer code, String name) {
            this.code = code;
            this.name = name;
        }

        public Integer getCode() {
            return code;
        }

        public String getName() {
            return name;
        }

        /**
         * 根据code获取enum
         * @param code
         * @return
         */
        public static SiteTypeThreeLevelEnum getEnum(Integer code) {
            for (SiteTypeThreeLevelEnum value : SiteTypeThreeLevelEnum.values()) {
                if (value.code.equals(code)) {
                    return value;
                }
            }
            return null;
        }
    }

}
