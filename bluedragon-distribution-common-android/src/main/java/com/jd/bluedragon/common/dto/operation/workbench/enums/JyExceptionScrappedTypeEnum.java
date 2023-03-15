package com.jd.bluedragon.common.dto.operation.workbench.enums;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/3/9 18:36
 * @Description: 异常报废类型枚举
 */
public enum JyExceptionScrappedTypeEnum {

    SCRAPPED_FRESH(1,"生鲜报废")
    ;

    JyExceptionScrappedTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    private Integer code;
    private String name;

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

    public static JyExceptionScrappedTypeEnum valueOf(Integer code){
        for (JyExceptionScrappedTypeEnum e:JyExceptionScrappedTypeEnum.values()){
            if (code.equals(e.getCode())){
                return e;
            }
        }
        return null;
    }
}
