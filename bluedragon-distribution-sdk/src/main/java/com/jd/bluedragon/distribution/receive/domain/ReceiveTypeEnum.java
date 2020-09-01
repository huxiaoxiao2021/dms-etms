package com.jd.bluedragon.distribution.receive.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * 收货类型枚举
 *
 * @author: hujiping
 * @date: 2020/8/25 15:21
 */
public enum ReceiveTypeEnum {

    REVERSE_RECEIVE_WMS(1, "仓储"),
    REVERSE_RECEIVE_ASM(2, "售后"),
    REVERSE_RECEIVE_SPWMS(3, "备件库"),
    REVERSE_RECEIVE_WXWD(4, "维修外单售后"),
    REVERSE_RECEIVE_KFPT(5, "开放平台仓"),
    REVERSE_RECEIVE_CLOUD(6, "云仓"),
    REVERSE_RECEIVE_ECLP_WAREHOUSE(7, "ECLP仓配"),
    REVERSE_RECEIVE_ECLP_PUREMATCH(8, "ECLP纯配"),
    REVERSE_RECEIVE_ECLP_C2C(10, "ECLP C2C");

    private int code;
    private String name;
    public static List<Integer> getAllCode(){
        ReceiveTypeEnum[] list = ReceiveTypeEnum.values();
        List<Integer> codes = new ArrayList<Integer>(list.length);
        for(ReceiveTypeEnum receiveType : list){
            codes.add(receiveType.code);
        }
        return codes;
    }
    ReceiveTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

}
