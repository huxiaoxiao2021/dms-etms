package com.jd.bluedragon.distribution.station.enums;

import java.util.HashMap;

public enum ScheduleEnum {

    TIME_1(1, "time1"),
    TIME_2(2, "time2"),
    TIME_3(3, "time3");

    private Integer code;
    private String name;

    private static HashMap<Integer, String> codeToNameMap = new HashMap<Integer, String>();
    private static HashMap<String, Integer> nameToCodeMap = new HashMap<String, Integer>();

    static {
        for (ScheduleEnum waveTypeEnum : ScheduleEnum.values()){
            codeToNameMap.put(waveTypeEnum.getCode(), waveTypeEnum.getName());
            nameToCodeMap.put(waveTypeEnum.getName(), waveTypeEnum.getCode());
        }
    }
    ScheduleEnum(Integer code, String name){
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

    public static String getNameByCode(Integer code) {
        return codeToNameMap.get(code);
    }

    public static Integer getCodeByName(String name){
        return nameToCodeMap.get(name);
    }
}
