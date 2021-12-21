package com.jd.bluedragon.distribution.board.domain;

public enum BizSourceEnum {
    PDA(1),
    SORTING_MACHINE(2);
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
