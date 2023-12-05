package com.jd.bluedragon.distribution.api.enums;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/11/15 10:27
 * @Description: 返调度审核完结标识枚举
 */
public enum ReassignWaybillCheckEndFlagEnum {


    UNKNOWN(0,  "未知"),
    NO_END(1,  "审批未完成"),
    END(2,"审批完毕"),
    ;


    private Integer code;
    private String name;


    private ReassignWaybillCheckEndFlagEnum(Integer code, String name) {
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
        ReassignWaybillCheckEndFlagEnum data = getEnum(code);
        if(data != null) {
            return data.getName();
        }
        return null;
    }
    /**
     * 根据code获取enum
     * @param code
     * @return
     */
    public static ReassignWaybillCheckEndFlagEnum getEnum(Integer code) {
        for (ReassignWaybillCheckEndFlagEnum value : ReassignWaybillCheckEndFlagEnum.values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }

    /**
     * 判断code是否存在
     * @param code
     * @return
     */
    public static boolean exist(Integer code) {
        return null != getEnum(code);
    }


}
