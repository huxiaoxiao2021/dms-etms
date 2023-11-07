package com.jd.bluedragon.distribution.api.enums;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/11/1 9:50
 * @Description: 操作返调度原因类型枚举
 */
public enum ReassignWaybillReasonTypeEnum {

    UNABLE_TO_DELIVER(1,"预分拣站点无法派送"),
    CONTROL_CONTRABAND(2,"特殊时期管制违禁品"),
    POSTAL_REJECTION(3,"邮政拒收"),
    NO_PRE_SORTING_STATION(4,"无预分拣站点");




    private Integer code;
    private String name;


    private ReassignWaybillReasonTypeEnum(Integer code, String name) {
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
        ReassignWaybillReasonTypeEnum data = getEnum(code);
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
    public static ReassignWaybillReasonTypeEnum getEnum(Integer code) {
        for (ReassignWaybillReasonTypeEnum value : ReassignWaybillReasonTypeEnum.values()) {
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
