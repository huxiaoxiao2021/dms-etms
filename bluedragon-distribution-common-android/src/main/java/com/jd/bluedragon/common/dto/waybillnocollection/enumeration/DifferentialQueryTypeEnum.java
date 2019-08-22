package com.jd.bluedragon.common.dto.waybillnocollection.enumeration;

/**
 * DifferentialQueryTypeEnum
 * 差异查询类型枚举
 *
 * @author jiaowenqiang
 * @date 2019/7/5
 */
public enum DifferentialQueryTypeEnum {

    SEND_CODE_QUERY_TYPE(1, "批次号查询差异"),
    BOARD_CODE_QUERY_TYPE(2, "板号查询差异"),
    BOX_CODE_QUERY_TYPE(3, "箱号查询差异");

    private Integer type;

    private String name;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    DifferentialQueryTypeEnum(Integer type, String name) {
        this.type = type;
        this.name = name;
    }
}
