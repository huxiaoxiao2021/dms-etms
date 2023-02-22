package com.jd.bluedragon.distribution.jy.enums;

public enum ExcepScanTypeEnum {
    INTERCEPTED(1,"拦截","拦截"),
    FORCE_SEND(2,"强发","强发"),
    INTERCEPTED_AND_FORCE(3,"拦截/强发","拦截或强发"),
    HAVE_SCAN(4,"已扫","已扫"),
    INCOMPELETE(5,"不齐运单","不齐(已验未扫（特指一单多件）和未到)"),
    HAVE_INSPECTION_BUT_NOT_SEND(6,"已到未扫","已验未扫（特指一单一件）"),
    INCOMPELETE_DEAL(7,"不齐运单处理","不齐（已扫部分）");
    private Integer code;
    private String name;
    private String desc;

    ExcepScanTypeEnum(Integer code, String name, String desc) {
        this.code = code;
        this.name = name;
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
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

    public static ExcepScanTypeEnum getExcepScanTypeEnum(int code){
        for(ExcepScanTypeEnum excepScanTypeEnum : ExcepScanTypeEnum.values()){
            if(excepScanTypeEnum.getCode() == code){
                return excepScanTypeEnum;
            }
        }
        return null;
    }
}
