package com.jd.bluedragon.distribution.box.constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 新版箱号子类型枚举
 * @author fanggang7
 * @time 2023-11-20 11:25:27 周一
 */
public enum BoxSubTypeEnum {

    TYPE_BCTC("BCTC", BoxTypeV2Enum.TYPE_BC.getCode(), "同城"),
    TYPE_BCLY("BCLY", BoxTypeV2Enum.TYPE_BC.getCode(), "陆运"),
    TYPE_BCHK("BCHK", BoxTypeV2Enum.TYPE_BC.getCode(), "航空"),
    TYPE_BCTL("BCTL", BoxTypeV2Enum.TYPE_BC.getCode(), "铁路"),
    TYPE_TCTH("TCTH", BoxTypeV2Enum.TYPE_TC.getCode(), "退货组"),
    TYPE_TCBJ("TCBJ", BoxTypeV2Enum.TYPE_TC.getCode(), "备件库"),
    TYPE_WJ("WJPT", BoxTypeV2Enum.TYPE_WJ.getCode(), "文件"),
    TYPE_TA("TAPT", BoxTypeV2Enum.TYPE_TA.getCode(), "特安"),
    TYPE_LL("LLPT", BoxTypeV2Enum.TYPE_LL.getCode(), "笼车/围板箱"),
    ;

    private String code;

    /**
     * 父级类型
     */
    private String parentTypeCode;

    private String name;

    public static Map<String, String> ENUM_MAP;
    public static Map<String, String> PARENT_ASSOCIATE_NORMAL_SBU_TYPE_MAP;

    public static List<String> ENUM_LIST;

    BoxSubTypeEnum(String code, String parentTypeCode, String name) {
        this.code = code;
        this.parentTypeCode = parentTypeCode;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getParentTypeCode() {
        return parentTypeCode;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return this.getCode() + "-" + this.getParentTypeCode() + "-" + this.getName();
    }

    public static BoxSubTypeEnum getFromCode(String code) {
        for (BoxSubTypeEnum item : BoxSubTypeEnum.values()) {
            if (item.code.equals(code)) {
                return item;
            }
        }
        return null;
    }

    public static Map<String,String> getMap(){
        Map<String,String> result = new HashMap<String, String>();
        for(BoxSubTypeEnum BoxTypeV2Enum : BoxSubTypeEnum.values()){
            result.put(BoxTypeV2Enum.getCode(),BoxTypeV2Enum.getName());
        }
        return result;
    }

    static {
        //将所有枚举装载到map中
        ENUM_MAP = new HashMap<String, String>();
        ENUM_LIST = new ArrayList<String>();
        PARENT_ASSOCIATE_NORMAL_SBU_TYPE_MAP = new HashMap<String, String>();
        for (BoxSubTypeEnum enumItem : BoxSubTypeEnum.values()) {
            ENUM_MAP.put(enumItem.getCode(), enumItem.getName());
            ENUM_LIST.add(enumItem.getCode());

            if (!PARENT_ASSOCIATE_NORMAL_SBU_TYPE_MAP.containsKey(enumItem.getParentTypeCode())) {
                PARENT_ASSOCIATE_NORMAL_SBU_TYPE_MAP.put(enumItem.getParentTypeCode(), enumItem.getCode());
            }
        }
    }

}
