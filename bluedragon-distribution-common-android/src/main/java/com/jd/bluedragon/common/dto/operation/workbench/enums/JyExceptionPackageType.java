package com.jd.bluedragon.common.dto.operation.workbench.enums;

/**
 * @Auther: 徐文瑞（ext.xuwenrui3）
 * @Date: 2023/07/26
 * @Description: 异常包裹类型
 */
public class JyExceptionPackageType {
    // 客服反馈未处理数量
    public static final String TO_PROCESS_DAMAGE_EXCEPTION="DMS:JYAPP:EXP:DAMAGE:TO:PROCESS:";
    // 客服反馈新增未处理数量
    public static final String TO_PROCESS_DAMAGE_EXCEPTION_ADD="DMS:JYAPP:EXP:DAMAGE:TO:PROCESS:ADD:";

    /**
     * 异常包裹类型
     */
    public enum ExceptionPackageTypeEnum {
        SCRAPPED_FRESH(1, "生鲜报废"), DAMAGE(2, "破损修复");
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
        OUTSIDE_PACKING_DAMAGE(1, "外包装破损"), INSIDE_OUTSIDE_DAMAGE(2, "内破外破");
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
        REPAIR(1, "修复"), REPLACE_PACKAGING(2, "更换包装"), HANDOVER(3, "直接下传");

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
        WORTHLESS(1, "无残余价值"), INSIDE_MILD_DAMAGE(2, "内物轻微破损"), INSIDE_SEVERE_DAMAGE(3, "内物严重破损");

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

    public enum SaveTypeEnum {
        DRAFT(0, "暂存"), SBUMIT_NOT_FEEBACK(1, "提交待反馈"), SBUMIT_FEEBACK(2, "提交已反馈");

        private Integer code;
        private String name;

        SaveTypeEnum(Integer code, String name) {
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
     * 客服反馈类型（1：修复下传 2：直接下传 3：更换包装下传 4：报废 5：逆向退回）
     */
    public enum FeedBackTypeEnum {
        DEFAULT(0, "默认值"), REPAIR_HANDOVER(1, "修复下传"), HANDOVER(2, "直接下传"),
        REPLACE_PACKAGING_HANDOVER(3, "更换包装下传"), DESTROY(4, "报废"),
        REVERSE_RETURN(5, "逆向退回");

        private Integer code;
        private String name;

        FeedBackTypeEnum(Integer code, String name) {
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
