package com.jd.bluedragon.common.dto.operation.workbench.enums;

/**
 * @Auther: 徐文瑞（ext.xuwenrui3）
 * @Date: 2023/07/26
 * @Description: 异常包裹类型
 */
public class JyExceptionPackageType {
    /**
     * 异常包裹类型
     */
    public enum ExceptionPackageTypeEnum {
        SCRAPPED_FRESH(1, "生鲜报废"),
        DAMAGE(2, "外包装破损");
        private Integer code;
        private String name;

        ExceptionPackageTypeEnum(Integer code, String name) {
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
     * 破损类型
     */
    public enum DamagedTypeEnum {
        OUTSIDE_PACKING_DAMAGE(1, "外包装破损"),
        INSIDE_OUTSIDE_DAMAGE(2, "内破外破");
        private Integer code;
        private String name;

        DamagedTypeEnum(Integer code, String name) {
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
     * 外包装破损修复类型
     */
    public enum OutPackingDamagedRepairTypeEnum {
        REPAIR(1, "修复"),
        REPLACE_PACKAGING(2, "更换包装"),
        HANDOVER(3, "直接下传");

        private Integer code;
        private String name;

        OutPackingDamagedRepairTypeEnum(Integer code, String name) {
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
     * 内破外破修复类型
     */
    public enum InsideOutsideDamagedRepairTypeEnum {
        WORTHLESS(1, "无残余价值"),
        INSIDE_MILD_DAMAGE(2, "内物轻微破损"),
        INSIDE_SEVERE_DAMAGE(3, "内物严重破损");

        private Integer code;
        private String name;

        InsideOutsideDamagedRepairTypeEnum(Integer code, String name) {
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
}
