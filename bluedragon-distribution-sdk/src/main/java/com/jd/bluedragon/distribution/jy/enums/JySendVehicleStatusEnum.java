package com.jd.bluedragon.distribution.jy.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName JySendVehicleStatusEnum
 * @Description 发车状态枚举，客户端使用
 * @Author wyh
 * @Date 2022/5/29 13:59
 **/
public enum JySendVehicleStatusEnum {

    TO_SEND(JyBizTaskSendStatusEnum.TO_SEND.getCode(), JyBizTaskSendStatusEnum.TO_SEND.getName(), 1),
    SENDING(JyBizTaskSendStatusEnum.SENDING.getCode(), JyBizTaskSendStatusEnum.SENDING.getName(), 2),
    TO_SEAL(JyBizTaskSendStatusEnum.TO_SEAL.getCode(), JyBizTaskSendStatusEnum.TO_SEAL.getName(), 3),
    SEALED(JyBizTaskSendStatusEnum.SEALED.getCode(), JyBizTaskSendStatusEnum.SEALED.getName(), 4)
    ;

    private Integer code;
    private String name;
    private Integer order;

    JySendVehicleStatusEnum(Integer code, String name, Integer order) {
        this.code = code;
        this.name = name;
        this.order = order;
    }

    public Integer getCode() {
        return this.code;
    }

    public String getName() {
        return this.name;
    }

    public Integer getOrder() {
        return this.order;
    }

    public static Map<Integer, String> ENUM_MAP;

    public static List<Integer> ENUM_LIST;

    public static String getNameByCode(Integer code) {
        for(JySendVehicleStatusEnum enumData: JySendVehicleStatusEnum.values()) {
            if(enumData.getCode().equals(code)) {
                return enumData.getName();
            }
        }
        return "";
    }

    static {
        //将所有枚举装载到map中
        ENUM_MAP = new HashMap<Integer, String>();
        ENUM_LIST = new ArrayList<Integer>();
        for (JySendVehicleStatusEnum enumItem : JySendVehicleStatusEnum.values()) {
            ENUM_MAP.put(enumItem.getCode(), enumItem.getName());
            ENUM_LIST.add(enumItem.getCode());
        }
    }
}
