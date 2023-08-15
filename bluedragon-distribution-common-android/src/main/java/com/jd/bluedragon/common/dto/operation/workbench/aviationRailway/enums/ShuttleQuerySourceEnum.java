package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums;

import java.util.HashMap;
import java.util.Map;

public enum ShuttleQuerySourceEnum {

    SEAL_Y(1,"封车列表页查询摆渡已封车数据"),
    SEAL_N(2,"摆渡任务绑定确认车辆查询未封车数据"),
    ;
    private Integer code;
    private String desc;

    private static final Map<Integer, ShuttleQuerySourceEnum> codeMap;
    static {
        codeMap = new HashMap<Integer, ShuttleQuerySourceEnum>();
        for (ShuttleQuerySourceEnum _enum : ShuttleQuerySourceEnum.values()) {
            codeMap.put(_enum.getCode(), _enum);
        }
    }
    public static ShuttleQuerySourceEnum getSourceByCode(Integer code) {
        ShuttleQuerySourceEnum _enum = codeMap.get(code);
        if (_enum != null) {
            return _enum;
        }
        return null;
    }

    ShuttleQuerySourceEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
