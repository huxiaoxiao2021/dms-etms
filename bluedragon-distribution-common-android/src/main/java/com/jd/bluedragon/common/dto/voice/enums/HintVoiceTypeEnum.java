package com.jd.bluedragon.common.dto.voice.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 提示音类型枚举
 *
 * @author hujiping
 * @date 2022/10/18 5:32 PM
 */
public enum HintVoiceTypeEnum {

    HINT_VOICE_TYPE_HINT(0, "提示"),
    HINT_VOICE_TYPE_WARN(1, "警告"),
    HINT_VOICE_TYPE_ERROR(2, "错误"),
    HINT_VOICE_TYPE_EX(3, "异常")
    ;

    public static final Map<Integer, String> enumMap;

    static {
        enumMap = new HashMap<Integer, String>(4);
        for (HintVoiceTypeEnum value : HintVoiceTypeEnum.values()) {
            enumMap.put(value.getCode(), value.getName());
        }
    }

    HintVoiceTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    private Integer code;
    private String name;

    public static HintVoiceTypeEnum getEnum(Integer code) {
        for (HintVoiceTypeEnum value : HintVoiceTypeEnum.values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
