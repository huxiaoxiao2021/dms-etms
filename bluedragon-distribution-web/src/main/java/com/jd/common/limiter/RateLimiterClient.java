package com.jd.common.limiter;

/**
 * @ClassName RateLimiterClient
 * @Description
 * @Author wyh
 * @Date 2023/5/17 20:11
 **/
public interface RateLimiterClient {

    /**
     * 查询限流排名 参数：ip或erp 小时维度，看前多少名 黑名单不统计排名的url  24小时过期。
     * @return
     */
    Object queryRateListRanking();

}
