package com.jd.common.limiter;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @ClassName LimitType
 * @Description
 * @Author wyh
 * @Date 2023/5/14 19:09
 **/
@AllArgsConstructor
@Getter
public enum LimitType {

    /**
     * 全局限流
     */
    DEFAULT(1, "全局限流"),

    /**
     * 按IP限流
     */
    IP(2, "按客户端IP限流"),

    /**
     * 按用户限流
     */
    USER(3, "按用户限流");

    private Integer code;

    private String name;

    public static LimitType getFromCode(Integer code) {
        return Arrays.stream(LimitType.values()).filter(item -> item.getCode().equals(code)).findFirst().get();
    }
}
