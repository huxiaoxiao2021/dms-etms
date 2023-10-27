package com.jd.bluedragon.distribution.box.constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 新版箱号枚举
 * @author fanggang7
 * @time 2023-10-25 10:20:44 周三
 */
public enum BoxTypeV2Enum {

    TYPE_BCTC("BCTC", "BC", "同城"),
    TYPE_BCLY("BCLY", "BC", "陆运"),
    TYPE_BCHK("BCHK", "BC", "航空"),
    TYPE_BCTL("BCTL", "BC", "铁路"),
    TYPE_TCTH("TCTH", "TC", "退货组"),
    TYPE_TCBJ("TCBJ", "TC", "备件库"),
    TYPE_WJ("WJ", "WJ", "文件"),
    TYPE_TA("TA", "TA", "特安"),
    TYPE_LL("LL", "LL", "笼车/围板箱"),
    ;

    private String code;

    /**
     * 显示的code
     */
    private String codeShow;

    private String name;

    public static Map<String, String> ENUM_MAP;

    public static List<String> ENUM_LIST;

    BoxTypeV2Enum(String code, String codeShow, String name) {
        this.code = code;
        this.codeShow = codeShow;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getCodeShow() {
        return codeShow;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return this.getCode() + "-" + this.getCodeShow() + "-" + this.getName();
    }

    public static BoxTypeV2Enum getFromCode(String code) {
        for (BoxTypeV2Enum item : BoxTypeV2Enum.values()) {
            if (item.code.equals(code)) {
                return item;
            }
        }
        return null;
    }

    public static Map<String,String> getMap(){
        Map<String,String> result = new HashMap<String, String>();
        for(BoxTypeV2Enum boxTypeEnum : BoxTypeV2Enum.values()){
            result.put(boxTypeEnum.getCode(),boxTypeEnum.getName());
        }
        return result;
    }

    static {
        //将所有枚举装载到map中
        ENUM_MAP = new HashMap<String, String>();
        ENUM_LIST = new ArrayList<String>();
        for (BoxTypeV2Enum enumItem : BoxTypeV2Enum.values()) {
            ENUM_MAP.put(enumItem.getCode(), enumItem.getName());
            ENUM_LIST.add(enumItem.getCode());
        }
    }

}
