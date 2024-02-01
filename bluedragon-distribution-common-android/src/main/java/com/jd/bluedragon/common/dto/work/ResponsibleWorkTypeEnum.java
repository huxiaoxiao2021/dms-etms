package com.jd.bluedragon.common.dto.work;

/**
 * 管理任务-责任人工种
 */
public enum ResponsibleWorkTypeEnum {
    
    FORMAL_WORKER(1, "正式工"),
    OUTWORKER(2, "外包工"),
    TEMPORARY_WORKERS(3, "临时工");
    
    

    private Integer code;
    private String name;

    ResponsibleWorkTypeEnum(Integer code, String name) {
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
