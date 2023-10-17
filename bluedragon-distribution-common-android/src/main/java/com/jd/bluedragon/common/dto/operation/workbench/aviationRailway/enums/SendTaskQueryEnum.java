package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums;

/**
 * @author liwenji
 * @description 发货任务查询枚举
 * @date 2023-08-28 15:42
 */
public enum SendTaskQueryEnum {

    SEND_TASK_LIST(0,"任务列表查询"),
    TASK_RECOMMEND(1,"推荐任务查询");
    
    private Integer code;
    private String name;


    SendTaskQueryEnum(Integer code, String name) {
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
