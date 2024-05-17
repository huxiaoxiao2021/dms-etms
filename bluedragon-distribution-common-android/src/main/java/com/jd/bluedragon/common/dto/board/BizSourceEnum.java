package com.jd.bluedragon.common.dto.board;

/**
 * 组板操作来源
 * @date 2024-05-11
 * @author weixiaofeng
 */
public enum BizSourceEnum {
    /**
     * pda操作
     */
    PDA(1),
    /**
     * 自动化设备
     */
    SORTING_MACHINE(2),
    /**
     * 打印客户端
     */
    PRINT_CLIENT(3),

    /**
     * 自动化设备（自动装笼）
     */
    SORTING_MACHINE_AUTO_CAGE(5),
    /**
     * pda操作(自动装笼)
     */
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
