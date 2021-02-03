package com.jd.bluedragon.distribution.box.constants;

/**
 * @ClassName BoxTypeEnum
 * @Description 箱号类型枚举
 * @Author wyh
 * @Date 2021/2/2 19:49
 **/
public enum BoxTypeEnum {

    TYPE_BC("BC", "正向普通"),
    TYPE_TC("TC", "自动分拣机"),
    TYPE_GC("GC", "取件普通"),
    TYPE_FC("FC", "返调度再投普通"),
    TYPE_BS("BS", "正向奢侈品"),
    TYPE_TS("TS", "退货奢侈品"),
    TYPE_GS("GS", "取件奢侈品"),
    TYPE_FS("FS", "返调度再投奢侈品"),
    TYPE_BX("BX", "正向虚拟"),
    TYPE_TW("TW", "逆向内配"),
    TYPE_LP("LP", "理赔完成"),
    ;

    private String code;

    private String name;

    BoxTypeEnum(String code, String name) {
        this.code = code;
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
}
