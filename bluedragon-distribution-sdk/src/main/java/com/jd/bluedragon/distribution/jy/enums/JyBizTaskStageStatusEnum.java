package com.jd.bluedragon.distribution.jy.enums;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2022/4/1
 * @Description: 卸车子任务状态
 */
public enum JyBizTaskStageStatusEnum {

    DOING(1,"进行中"),

    COMPLETE(2,"完成");

    private Integer code;
    private String name;

    JyBizTaskStageStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }


}
