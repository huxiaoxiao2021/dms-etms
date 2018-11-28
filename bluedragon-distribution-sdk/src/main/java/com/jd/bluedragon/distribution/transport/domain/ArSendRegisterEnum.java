package com.jd.bluedragon.distribution.transport.domain;

/**
 * @author yangwenshu
 * @Description: 类描述信息
 * @date 2018年11月20日 13时:36分
 */
public enum ArSendRegisterEnum {
    /**
     * 航班号没有改变新增，有变化为修改
     */
    AIR_INSERT(1,"新增发货登记"),
    AIR_UPDATE_BEFOREFLY(2,"起飞前修改航班号"),
    AIR_UPDATE_AFTERFLY(3,"起飞后修改航班号");
    private Integer code;

    private String name;

    public int getCode() {
        return code;
    }


    public String getName() {
        return name;
    }

    ArSendRegisterEnum(Integer code,String name){
        this.code = code;
        this.name = name;
    }
    public static ArSendRegisterEnum getEnum(Integer code) {
        for (ArSendRegisterEnum type : ArSendRegisterEnum.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        return null;
    }
}
