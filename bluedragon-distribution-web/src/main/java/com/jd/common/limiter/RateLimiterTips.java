package com.jd.common.limiter;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName RateLimiterTips
 * @Description
 * @Author wyh
 * @Date 2023/5/14 16:26
 **/
@Data
public class RateLimiterTips implements Serializable {

    private static final long serialVersionUID = -8635307713115966140L;

    /**
     * 请求地址
     */
    private String uri;

    /**
     * 描述信息等 明细-一级菜单-功能  聚合--一级菜单--功能
     */
    private String desc;

    /**
     * 降级提示语
     */
    private String tip;

}
