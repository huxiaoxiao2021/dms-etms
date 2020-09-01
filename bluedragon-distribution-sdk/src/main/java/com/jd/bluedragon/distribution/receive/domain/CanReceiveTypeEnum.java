package com.jd.bluedragon.distribution.receive.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * 逆向是否可收货类型
 *
 * @author: hujiping
 * @date: 2020/8/25 15:36
 */
public enum CanReceiveTypeEnum {

    REVERSE_REJECT(0, "拒收"),
    REVERSE_RECEIVE(1, "收货"),
    REVERSE_RECEIVE_PART(2, "部分收货");

    private int code;
    private String name;
    public static List<Integer> getAllCode(){
        CanReceiveTypeEnum[] list = CanReceiveTypeEnum.values();
        List<Integer> codes = new ArrayList<Integer>(list.length);
        for(CanReceiveTypeEnum canReceiveType : list){
            codes.add(canReceiveType.code);
        }
        return codes;
    }
    CanReceiveTypeEnum(int code, String name) {
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
