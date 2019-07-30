package com.jd.bluedragon.distribution.inventory.domain;


public enum InventoryScopeEnum {
    CUSTOMIZE(1,"自定区域"),
    ALLDIRECTION(2,"全场范围"),
    EXCEPTION(3,"异常区");

    private Integer code;

    private String desc;

    InventoryScopeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static String getDescByCode(Integer code){
        for (InventoryScopeEnum scope : InventoryScopeEnum.values()){
            if (scope.getCode().equals(code)){
                return scope.getDesc();
            }
        }
        return null;
    }
}
