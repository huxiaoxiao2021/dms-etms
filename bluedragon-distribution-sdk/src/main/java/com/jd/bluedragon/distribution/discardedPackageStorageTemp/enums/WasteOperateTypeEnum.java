package com.jd.bluedragon.distribution.discardedPackageStorageTemp.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 弃件操作类型
 * Created by wuyoude on 2021/11/11
 */
public enum WasteOperateTypeEnum {

    STORAGE(1, "弃件暂存"),
    SCRAP(2, "弃件废弃");

    private Integer code;
    private String name;

    public static Map<Integer, String> ENUM_MAP;

    public static List<Integer> ENUM_LIST;
    
    WasteOperateTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }
    public static String getNameByCode(Integer code) {
		for(WasteOperateTypeEnum enumData: WasteOperateTypeEnum.values()) {
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
        ENUM_MAP = new HashMap<Integer, String>();
        ENUM_LIST = new ArrayList<Integer>();
        for (WasteOperateTypeEnum enumItem : WasteOperateTypeEnum.values()) {
            ENUM_MAP.put(enumItem.getCode(), enumItem.getName());
            ENUM_LIST.add(enumItem.getCode());
        }
    }
}
