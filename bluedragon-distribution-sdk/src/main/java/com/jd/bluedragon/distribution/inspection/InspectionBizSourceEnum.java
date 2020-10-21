package com.jd.bluedragon.distribution.inspection;

public enum InspectionBizSourceEnum {

        /**
         * PDA：自营分拣->验货
         */
        SELF_SUPPORT_INSPECTION(31,"自营验货"),

        /**
         * PDA: 逆向分拣->验货-自营退货验货
         */
        SELF_RETURN_INSPECTION(32,"自营退货验货"),

        /**
         * PDA: 逆向分拣->验货-三方退货验货
         */
        THIRD_RETURN_INSPECTION(33,"三方退货验货"),

        /**
         * PDA: 逆向分拣->验货-分拣中心退货验货
         */
        DISTRIBUTION_RETURN_INSPECTION(34,"分拣中心退货验货"),

        /**
         * PDA: 离线->自营分拣->验货
         */
        OFFLINE_SELF_SUPPORT_INSPECTION(35, "离线自营验货"),

        /**
         * PDA: 离线->逆向分拣->自营验货
         */
        OFFLINE_SELF_RETURN_INSPECTION(36, "离线逆向自营验货"),

        /**
         * 安卓: 验货
         */
        ANDROID_INSPECTION(37, "安卓验货"),

        /**
         * 开放平台系统
         */
        OPEN_PLATE_INSPECTION(38, "开放平台验货"),

        /**
         * 龙门架验货
         */
        AUTOMATIC_GANTRY_INSPECTION(39, "龙门架验货"),

        /**
         * 自动化分拣机验货
         */
        AUTOMATIC_SORTING_MACHINE_INSPECTION(40, "分拣机验货"),

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

        InspectionBizSourceEnum(Integer code, String name) {
            this.code = code;
            this.name = name;
        }

        public static InspectionBizSourceEnum getEnum(Integer code) {
            if (code != null) {
                for (InspectionBizSourceEnum bizSource : InspectionBizSourceEnum.values()) {
                    if (bizSource.getCode().equals(code)) {
                        return bizSource;
                    }
                }
            }
            return null;
        }
    }