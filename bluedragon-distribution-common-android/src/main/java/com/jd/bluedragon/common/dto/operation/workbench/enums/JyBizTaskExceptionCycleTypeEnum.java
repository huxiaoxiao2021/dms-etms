package com.jd.bluedragon.common.dto.operation.workbench.enums;
/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 陆庆林（luqinglin3）
 * @Date: 2022/4/6
 * @Description:
 */
public enum JyBizTaskExceptionCycleTypeEnum {

    UPLOAD(0, "上报"),
    DISTRIBUTION(1, "分配"),
    RECEIVE(2, "领取"),
    PROCESS(3, "处理"),
    PROCESSED(4, "处理成功"),
    UP_SHELF(5, "暂存上架"),
    DOWN_SHELF(6, "暂存上架"),
    CLOSE(7, "完成"),
    ;

    private Integer code;
    private String name;


    JyBizTaskExceptionCycleTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
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
