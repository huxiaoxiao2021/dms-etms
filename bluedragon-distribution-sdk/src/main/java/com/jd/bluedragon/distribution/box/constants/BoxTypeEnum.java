package com.jd.bluedragon.distribution.box.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName BoxTypeEnum
 * @Description 箱号类型枚举
 * @Author wyh
 * @Date 2021/2/2 19:49
 **/
public enum BoxTypeEnum {

    TYPE_BC("BC", "BC","正向普通"),
    TYPE_TC("TC", "BC", "退货普通"),
    TYPE_GC("GC", "BC", "取件普通"),
    TYPE_FC("FC", "BC", "返调度再投普通"),
    TYPE_BS("BS", "BC", "正向奢侈品"),
    TYPE_TS("TS", "BC", "退货奢侈品"),
    TYPE_GS("GS", "BC", "取件奢侈品"),
    TYPE_FS("FS", "BC", "返调度再投奢侈品"),
    TYPE_BX("BX", "BC", "正向虚拟"),
    TYPE_TW("TW", "BC", "逆向内配"),
    TYPE_LP("LP", "BC", "理赔完成"),
    TYPE_WJ("WJ", "BC", "文件"),
    TYPE_ZH("ZH", "BC", "航空件"),
    TYPE_ZF("ZF", "BC", "同城混包"),
    TYPE_ZK("ZK", "BC", "特快混包"),
    TYPE_ZQ("ZQ", "BC", "其他混包"),
    TYPE_ZS("ZS", "BC", "售后件"),
    RECYCLE_BASKET("AK", "BC", "周转筐"),
    TYPE_MS("MS", "BC", "医药直发"),
    TYPE_LL("LL", "BC", "笼车/围板箱"),
    TYPE_BCTC("BCTC", "BC", "同城"),
    TYPE_BCLY("BCLY", "BC", "陆运"),
    TYPE_BCHK("BCHK", "BC", "航空"),
    TYPE_BCTL("BCTL", "BC", "铁路"),
    TYPE_TCTH("TCTH", "TC", "退货组"),
    TYPE_TCBJ("TCBJ", "TC", "备件库"),
    TYPE_TA("TA", "TA", "特安"),
    ;

    private String code;

    /**
     * 显示的code
     */
    private String codeShow;

    private String name;

    BoxTypeEnum(String code, String codeShow, String name) {
        this.code = code;
        this.codeShow = codeShow;
        this.name = name;
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

    public static BoxTypeEnum getFromCode(String code) {
        for (BoxTypeEnum item : BoxTypeEnum.values()) {
            if (item.code.equals(code)) {
                return item;
            }
        }
        return null;
    }

    public static Map<String,String> getMap(){
        Map<String,String> result = new HashMap<String, String>();
        for(BoxTypeEnum boxTypeEnum : BoxTypeEnum.values()){
            result.put(boxTypeEnum.getCode(),boxTypeEnum.getName());
        }
        return result;
    }
}
