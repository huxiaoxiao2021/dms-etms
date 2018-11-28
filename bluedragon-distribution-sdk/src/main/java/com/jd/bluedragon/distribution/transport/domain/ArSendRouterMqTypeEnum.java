package com.jd.bluedragon.distribution.transport.domain;

/**
 * @author yangwenshu
 * @Description: 类描述信息
 * @date 2018年11月23日 10时:55分
 */
public enum ArSendRouterMqTypeEnum {
    AIR_NO_SEND(1,"未发送"),
    AIR_ALREADY_SEND(2,"已发送");
    private Integer code;

    private String name;

    public int getCode() {
        return code;
    }


    public String getName() {
        return name;
    }

    ArSendRouterMqTypeEnum(Integer code,String name){
        this.code = code;
        this.name = name;
    }
    public static ArSendRouterMqTypeEnum getEnum(Integer code) {
        for (ArSendRouterMqTypeEnum type : ArSendRouterMqTypeEnum.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        return null;
    }
}
