package com.jd.bluedragon.distribution.api.enums;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/11/1 9:50
 * @Description: 操作返调度原因类型枚举
 */
public enum ReassignWaybillReasonTypeEnum {

    UNABLE_TO_DELIVER(1,0,"预分拣站点无法派送"),
    CONTROL_CONTRABAND(2,0,"特殊时期管制违禁品"),
    POSTAL_REJECTION(3,0,"邮政拒收"),
    NO_PRE_SORTING_STATION(4,0,"无预分拣站点"),
    RECOMMENDS_WAREHOUSE_NOT_ACC(5,1,"系统推荐仓不收"),
    JUDGMENT_REASSIGN(6,1,"根据判责结果反调"),
    NO_ROUTING(7,1,"退仓无线上路由");


    private Integer code;

    /**
     * 0 非退货组
     * 1 退货组
     */
    private Integer type;

    private String name;


    private ReassignWaybillReasonTypeEnum(Integer code, Integer type,String name) {
        this.code = code;
        this.name = name;
        this.type = type;
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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
