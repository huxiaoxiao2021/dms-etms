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
    
    public static ResponsibleWorkTypeEnum getByCode(Integer code){
        if(code == null){
            return null;
        }
        for(ResponsibleWorkTypeEnum responsibleWorkTypeEnum : ResponsibleWorkTypeEnum.values()){
            if(responsibleWorkTypeEnum.getCode().equals(code)){
                return responsibleWorkTypeEnum;
            }
        }
        return null;
    }
    
    public static String getNameByCode(Integer code){
        ResponsibleWorkTypeEnum responsibleWorkTypeEnum = getByCode(code);
        if(responsibleWorkTypeEnum == null){
            return "未知";
        }
        return responsibleWorkTypeEnum.getName();
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
