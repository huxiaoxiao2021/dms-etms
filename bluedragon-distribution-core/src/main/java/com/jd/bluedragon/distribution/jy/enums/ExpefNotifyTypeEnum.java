package com.jd.bluedragon.distribution.jy.enums;


/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 陆庆林（luqinglin3）
 * @Date: 2022/4/6
 * @Description:
 */
public enum ExpefNotifyTypeEnum {

    MATCH_SUCCESS(0, "匹配成功"),
    PROCESSED(1, "处理完成"),
    ;

    private Integer code;
    private String name;


    ExpefNotifyTypeEnum(Integer code, String name) {
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

    public static ExpefNotifyTypeEnum valueOf(Integer code){
        for (ExpefNotifyTypeEnum e: ExpefNotifyTypeEnum.values()){
            if (code.equals(e.getCode())){
                return e;
            }
        }
        return null;
    }

}
