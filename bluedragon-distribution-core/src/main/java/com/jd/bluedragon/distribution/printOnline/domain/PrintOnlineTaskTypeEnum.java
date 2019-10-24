package com.jd.bluedragon.distribution.printOnline.domain;


import java.util.HashMap;
import java.util.Map;

public enum PrintOnlineTaskTypeEnum {

    REVERSE_PRINT_1("1","逆向交接清单打印");

    PrintOnlineTaskTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    private String code;
    private String name;

    private static Map<String, PrintOnlineTaskTypeEnum> codeMap;
    public static Map<String, String> PrintOnlineTaskTypeMap;

    static {
        //将所有枚举装载到map中
        codeMap = new HashMap<String, PrintOnlineTaskTypeEnum>();
        PrintOnlineTaskTypeMap = new HashMap<String, String>();
        for (PrintOnlineTaskTypeEnum _enum : PrintOnlineTaskTypeEnum.values()) {
            codeMap.put(_enum.getCode(), _enum);
            PrintOnlineTaskTypeMap.put(_enum.getCode(), _enum.getName());
        }
    }
    
    /**
     * 通过编码获取规则类型名称
     *
     * @param code 编码
     * @return 规则类型
     */
    public static String getNameByKey(String code) {
        PrintOnlineTaskTypeEnum _enum = codeMap.get(code);
        if (_enum != null) {
            return _enum.getName();
        }
        return "未知规则类型";
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return this.getCode() + "-" + this.getName();
    }
}
