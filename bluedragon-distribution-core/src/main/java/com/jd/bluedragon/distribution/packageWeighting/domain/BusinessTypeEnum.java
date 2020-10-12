package com.jd.bluedragon.distribution.packageWeighting.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * 运单包裹称重操作类型
 * Created by jinjingcheng on 2018/4/22.
 */
public enum BusinessTypeEnum {
    //1-分拣  2-接货仓配送员接货  3-接货中心驻场操作  4-仓库操作  5-车队操作 6总重量（快运称重）
    DMS(1, "分拣"),
    PICKER(2, "接货仓配送员接货"),
    PICK_RESIDENT(3, "接货中心驻场操作"),
    PICK(4, "仓库操作"),
    CAR_TEAM(5, "车队操作"),
    TOTAL(6, "总重量");

    private int code;
    private String name;
    public static List<Integer> getAllCode(){
        BusinessTypeEnum[] list = BusinessTypeEnum.values();
        List<Integer> codes = new ArrayList<Integer>(list.length);
        for(BusinessTypeEnum businessType : list){
            codes.add(businessType.code);
        }
        return codes;
    }
    BusinessTypeEnum(int code, String name) {
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
