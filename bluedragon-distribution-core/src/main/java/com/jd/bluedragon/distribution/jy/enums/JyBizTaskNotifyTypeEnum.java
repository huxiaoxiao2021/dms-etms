package com.jd.bluedragon.distribution.jy.enums;

import java.util.Objects;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 陆庆林（luqinglin3）
 * @Date: 2022/4/6
 * @Description:
 */
public enum JyBizTaskNotifyTypeEnum {

    DISTRIBUTE(0, "分配成功通知"),
    TIMEOUT(1, "超时通知"),
    ;

    private Integer code;
    private String name;


    JyBizTaskNotifyTypeEnum(Integer code, String name) {
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

    public static JyBizTaskNotifyTypeEnum valueOf(Integer code){
        for (JyBizTaskNotifyTypeEnum e:JyBizTaskNotifyTypeEnum.values()){
            if (Objects.equals(e.getCode(),code)){
                return e;
            }
        }
        return null;
    }

}
