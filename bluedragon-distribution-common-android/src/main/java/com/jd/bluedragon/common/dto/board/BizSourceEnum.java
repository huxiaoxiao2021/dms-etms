package com.jd.bluedragon.common.dto.board;

public enum BizSourceEnum {
    PDA(1),
    SORTING_MACHINE(2),
    PRINT_CLIENT(3);

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
