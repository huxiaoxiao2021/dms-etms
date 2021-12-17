package com.jd.bluedragon.distribution.discardedPackageStorageTemp.enums;

import java.util.List;
import java.util.Map;

/**
 * 弃件操作类型
 * Created by wuyoude on 2021/11/11
 */
public enum WasteOperateType {

    STORAGE(1, "弃件暂存"),
    SCRAP(2, "弃件废弃");

    private Integer code;
    private String name;
    
    WasteOperateType(Integer code, String name) {
        this.code = code;
        this.name = name;
    }
    public static String getNameByCode(Integer code) {
		for(WasteOperateType enumData:WasteOperateType.values()) {
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
}
