package com.jd.bluedragon.distribution.discardedPackageStorageTemp.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 弃件操作类型
 * Created by wuyoude on 2021/11/11
 */
public enum WasteWaybillTypeEnum {

    PACKAGE(1, "包裹类(存365天)"),
    LETTER(2, "信件类(存185天)");

    private Integer code;
    private String name;

    public static Map<Integer, String> ENUM_MAP;

    public static List<Integer> ENUM_LIST;

    WasteWaybillTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }
    public static String getNameByCode(Integer code) {
		for(WasteWaybillTypeEnum enumData: WasteWaybillTypeEnum.values()) {
			if(enumData.getCode().equals(code)) {
				return enumData.getName();
			}
		}
        return "";
    }
    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    static {
        //将所有枚举装载到map中
        ENUM_MAP = new HashMap<Integer, String>();
        ENUM_LIST = new ArrayList<Integer>();
        for (WasteOperateTypeEnum enumItem : WasteOperateTypeEnum.values()) {
            ENUM_MAP.put(enumItem.getCode(), enumItem.getName());
            ENUM_LIST.add(enumItem.getCode());
        }
    }
}
