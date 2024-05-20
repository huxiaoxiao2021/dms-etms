package com.jd.common.limiter;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName RateLimiter
 * @Description
 * @Author wyh
 * @Date 2023/5/14 16:26
 **/
@Data
public class RateLimiter implements Serializable {

    private static final long serialVersionUID = 8255487128020241754L;

    /**
     * 资源唯一标识
     */
    private String identifier;

    /**
     * 描述信息等 明细-一级菜单-功能  聚合--一级菜单--功能
     */
    private String desc;

    /**
     * 限流类型。全局-1、IP-2、用户-3。全局优先级最高
     * @see LimitType
     */
    private Integer limitType;

    /**
     * 限流时长类型。默认按分钟限流
     * @see LimitTimeUnit
     */
    private Integer timeUnit = LimitTimeUnit.MINUTE.getCode();

    /**
     * 限流时长
     */
    private Integer timeout;

    /**
     * 限流次数。配置小于0的值禁止访问
     */
    private Integer max;

}
