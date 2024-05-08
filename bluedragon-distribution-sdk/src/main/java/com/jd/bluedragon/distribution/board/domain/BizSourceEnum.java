package com.jd.bluedragon.distribution.board.domain;

public enum BizSourceEnum {
    /**
     * pda
     */
    PDA(1),
    /**
     * 自动化组板
     */
    SORTING_MACHINE(2),
    /**
     * 打印客户端
     */
    PRINT_CLIENT(3),
    /**
     * 自动化自动装笼
     */
    SORTING_MACHINE_AUTO_CAGE(4),
    /**
     * pda自动装笼
     */
    PDA_AUTO_CAGE(5);
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
