package com.jd.common.limiter;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @ClassName LimitTimeUnit
 * @Description
 * @Author wyh
 * @Date 2023/5/14 19:15
 **/
@AllArgsConstructor
@Getter
public enum LimitTimeUnit {

    SECOND(1, "按秒限流"),

    MINUTE(2, "按分钟限流"),

    HOUR(3, "按小时限流"),

    DAY(4, "按天限流");

    private Integer code;

    private String name;

    public static LimitTimeUnit getFromCode(Integer code) {
        return Arrays.stream(LimitTimeUnit.values()).filter(item -> item.getCode().equals(code)).findFirst().get();
    }

}
