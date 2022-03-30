package com.jd.bluedragon.distribution.spotcheck.enums;

/**
 * 抽检状态枚举
 *
 * @author hujiping
 * @date 2021/12/20 11:00 AM
 */
public enum SpotCheckStatusEnum {

    SPOT_CHECK_STATUS_VERIFY(1, "待核实"),
    SPOT_CHECK_STATUS_RZ(2, "认责"),
    SPOT_CHECK_STATUS_RZ_SYSTEM_ERP_N(3, "系统认责待确认责任人"),
    SPOT_CHECK_STATUS_RZ_SYSTEM_ERP_Y(4, "系统认责已确认责任人"),
    SPOT_CHECK_STATUS_PZ_UPGRADE(5, "升级判责"),
    SPOT_CHECK_STATUS_PZ_EFFECT(6, "判责有效"),
    SPOT_CHECK_STATUS_PZ_INVALID(7, "判责无效"),
    SPOT_CHECK_STATUS_COMPLETE(8, "处理完成"),
    SPOT_CHECK_STATUS_RZ_OVERTIME(9, "超时认责"),
    SPOT_CHECK_STATUS_DOING(101, "抽检中"),
    SPOT_CHECK_STATUS_INVALID(102, "抽检无效");

    private Integer code;

    private String name;

    SpotCheckStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String analysisNameFromCode(int code){
        for (SpotCheckStatusEnum value : SpotCheckStatusEnum.values()) {
            if(value.getCode() == code){
                return value.getName();
            }
        }
        return null;
    }

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
}
