package com.jd.bluedragon.common.dto.collectpackage.enums;

/**
 * @author liwenji
 * @description 小件集包任务枚举
 * @date 2023-10-12 17:31
 */
public enum CollectionPackageTaskStatusEnum {
    TO_COLLECTION(0, "待集包"),
    COLLECTION(1, "集包中"),
    COMPLETE_COLLECT(2,"已完成");
    
    private Integer code;
    private String name;
    
    CollectionPackageTaskStatusEnum(Integer code, String name) {
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
