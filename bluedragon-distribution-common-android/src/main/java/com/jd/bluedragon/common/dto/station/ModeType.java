package com.jd.bluedragon.common.dto.station;

/**
 * @author laoqingchang1
 * @description 身份证拍照签到枚举
 * @date 2022-08-03 18:32
 */
public enum ModeType {
    PHOTO(1), NO_PHOTO(0);
    private final Integer value;

    ModeType(Integer value){
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }
}
