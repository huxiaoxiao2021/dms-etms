package com.jd.bluedragon.common.dto.operation.workbench.enums;

/**
 * 违禁品上报类型
 */
public class JyExceptionContrabandEnum {
    /**
     * 违禁品上报类型枚举
     */
    public enum ContrabandTypeEnum {
        DETAIN_PACKAGE(1, "扣件"),
        AIR_TO_LAND(2, "航空转陆运"),
        RETURN(3,"退回");

        ContrabandTypeEnum(Integer code, String name) {
            this.code = code;
            this.name = name;
        }

        private Integer code;
        private String name;

        public Integer getCode() {
            return code;
        }

        public String getName() {
            return name;
        }

        public static ContrabandTypeEnum getEnumByCode(Integer code) {
            for (ContrabandTypeEnum typeEnum : ContrabandTypeEnum.values()) {
                if (typeEnum.getCode().equals(code)) {
                    return typeEnum;
                }
            }
            return null;
        }
    }

    /**
     * 同步发送消息给客服，消息体同破损上报与客服交互的消息，区分新的异常类型
     * 1.更换包装下传：更换包装
     * 2.再派、未联系上、下传：直接下传
     * 3.破损已理赔、退回：退回
     * 4.报废：报废
     * 5.补单、补差：补单
     */
    public enum FeedBackTypeEnum {
        DEFAULT(0, "待客服反馈"),    // 更换包装下传：更换包装
        REPLACE_PACKAGING(1, "更换包装下传"), // 再派、未联系上、下传：直接下传
        HANDOVER(2, "再派、未联系上、下传"),  // 破损已理赔、退回：退回
        RETURN(3, "破损已理赔、退回"),  // 破损已理赔、退回：退回
        DESTROY(4, "报废"), // 报废：报废
        SUPPLEMENT(5, "补单、补差"); // 补单、补差：补单
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

        public static String getNameByCode(Integer code) {
            for (FeedBackTypeEnum type : FeedBackTypeEnum.values()) {
                if (type.getCode().equals(code)) {
                    return type.getName();
                }
            }
            return null;
        }
    }
}
