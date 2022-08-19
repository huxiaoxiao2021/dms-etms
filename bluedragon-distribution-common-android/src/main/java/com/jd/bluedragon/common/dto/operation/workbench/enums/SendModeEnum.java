package com.jd.bluedragon.common.dto.operation.workbench.enums;

/**
 * @ClassName SendModeEnum
 * @Description 发货港发货模式
 * @Author wyh
 * @Date 2022/5/17 20:20
 **/
public enum SendModeEnum {

    TRUNK_AND_BRANCH_DELIVERY(1, "干支发货", "用于干支进行发货和封车操作"),
    TRANSFER_AND_FERRY_DELIVERY(2, "传摆发货", "传站/摆渡业务进行发货操作"),
    SORTING_BIND_BOARD(3, "分拣组板", "分拣传站业务进行组板操作"),
    TRANSFER_BLOCK_CAR(4, "传摆封车", "传摆封车功能")
    ;

    private Integer code;

    private String name;

    private String desc;

    SendModeEnum(Integer code, String name, String desc) {
        this.code = code;
        this.name = name;
        this.desc = desc;
    }

    public static String getNameByCode(Integer code) {
        for (SendModeEnum _enum : SendModeEnum.values()) {
            if (_enum.code.equals(code)) {
                return _enum.name;
            }
        }
        return "";
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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
