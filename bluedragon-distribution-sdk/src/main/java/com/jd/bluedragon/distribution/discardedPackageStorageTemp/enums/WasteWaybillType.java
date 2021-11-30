package com.jd.bluedragon.distribution.discardedPackageStorageTemp.enums;

/**
 * 弃件操作类型
 * Created by wuyoude on 2021/11/11
 */
public enum WasteWaybillType {

    PACKAGE(1, "包裹类(存365天)"),
    LETTER(2, "信件类(存185天)");

    private Integer code;
    private String name;

    WasteWaybillType(Integer code, String name) {
        this.code = code;
        this.name = name;
    }
    public static String getNameByCode(Integer code) {
		for(WasteWaybillType enumData:WasteWaybillType.values()) {
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
