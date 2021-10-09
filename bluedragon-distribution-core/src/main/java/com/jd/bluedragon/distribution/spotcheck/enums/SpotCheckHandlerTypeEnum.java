package com.jd.bluedragon.distribution.spotcheck.enums;

/**
 * 抽检处理类型枚举
 *
 * @author hujiping
 * @date 2021/8/12 3:23 下午
 */
public enum SpotCheckHandlerTypeEnum {

    ONLY_CHECK_IS_EXCESS(1, "仅仅只校验是否超标"),
    ONLY_DEAL_SPOT_CHECK(2, "仅仅只处理超标数据"),
    CHECK_AND_DEAL(3, "既校验是否超标又处理超标数据");

    SpotCheckHandlerTypeEnum(Integer code, String message){
        this.code = code;
        this.message = message;
    }

    private Integer code;
    private String message;

    public int getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
