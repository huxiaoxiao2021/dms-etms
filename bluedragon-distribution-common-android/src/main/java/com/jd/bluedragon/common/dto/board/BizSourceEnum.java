package com.jd.bluedragon.common.dto.board;

/**
 * 组板操作来源
 * @date 2024-05-11
 * @author weixiaofeng
 */
public enum BizSourceEnum {
    PDA(1),
    SORTING_MACHINE(2),
    PRINT_CLIENT(3),

    SORTING_MACHINE_AUTO_CAGE(5),
    PDA_AUTO_CAGE(6);

    private int value;

    BizSourceEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
