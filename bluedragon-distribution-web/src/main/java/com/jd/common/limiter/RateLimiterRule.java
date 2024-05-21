package com.jd.common.limiter;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName RateLimiterConfig
 * @Description
 * @Author wyh
 * @Date 2023/5/19 17:43
 **/
@Data
public class RateLimiterRule implements Serializable {

    /**
     * 兜底限流规则
     */
    private RateLimiter defaultLimitRule;

    /**
     * 按URL配置的具体限流规则
     */
    private List<RateLimiter> urlLimitRule;
}
