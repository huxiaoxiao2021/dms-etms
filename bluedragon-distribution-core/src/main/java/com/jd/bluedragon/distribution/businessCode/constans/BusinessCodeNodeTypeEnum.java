package com.jd.bluedragon.distribution.businessCode.constans;

/**
 * <p>
 *     业务单号的节点枚举
 *     枚举各种单号类型
 *     形如: 批次号  板号  箱号 等
 *
 * @author wuzuxiang
 * @since 2020/2/24
 **/
public enum BusinessCodeNodeTypeEnum {

    send_code("批次号"),

    board_code("板标号"),

    box_code("箱号");

    /**
     * 枚举释义
     */
    private String mark;

    BusinessCodeNodeTypeEnum(String mark) {
        this.mark = mark;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }
}
