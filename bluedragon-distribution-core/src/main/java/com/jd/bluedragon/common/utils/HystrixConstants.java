package com.jd.bluedragon.common.utils;

import com.netflix.hystrix.HystrixCommandGroupKey;

/**
 * @program: bluedragon-distribution
 * @description: hystrix 常量
 * @author: xumigen
 * @create: 2021-08-11 18:03
 **/
public class HystrixConstants {

    /**
     * 如账户服务定义一个group key，订单服务定义另一个group key。
     */
    public static final String PRINT_HYSTRIX_COMMAND_GROUPKEY = "printGroup";

    /**
     * 线程池key 同一类业务请求 最好一个，注意互相影响
     */
    public static final String PRINT_HYSTRIX_THREADPOOL_KEY = "printThreadPool";

}
