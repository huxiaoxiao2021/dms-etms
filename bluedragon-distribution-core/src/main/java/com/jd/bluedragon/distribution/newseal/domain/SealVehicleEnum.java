package com.jd.bluedragon.distribution.newseal.domain;

/**
 * @author shipeilin
 * @Description: 类描述信息
 * @date 2019年03月12日 21时:54分
 */
public enum SealVehicleEnum {

    /**
     * 航班号没有改变新增，有变化为修改
     */
    CANCEL_PRE_SEAL(-1,"取消预封车"),
    PRE_SEAL(0,"预封车"),
    SEAL(1,"封车"),
    DE_SEAL(2,"解封车"),
    CANCEL_SEAL(3,"取消封车");
    private Integer code;

    private String name;

    public int getCode() {
        return code;
    }


    public String getName() {
        return name;
    }

    SealVehicleEnum(Integer code,String name){
        this.code = code;
        this.name = name;
    }
    public static SealVehicleEnum getEnum(Integer code) {
        for (SealVehicleEnum type : SealVehicleEnum.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        return null;
    }

}
