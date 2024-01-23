package com.jd.bluedragon.distribution.jy.service.exception.capabilityDomain.businessIntercept.helper;

/**
 * 异常拦截任务相关常量
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2024-01-21 20:23:35 周日
 */
public class JyExceptionBusinessInterceptTaskConstants {

    // 业务主键ID模板 场地ID:包裹号:拦截类型
    // public static final String BIZ_ID_TEMPLATE = "EXP_INTERCEPT_%s_%s_%s";

    // 包裹与拦截场地关联关系关键key 包裹:拦截类型
    public static final String PACKAGE_CODE_ASSOCIATE_SITE_KEY = "EXP_INTERCEPT_PACK_CODE_ASSOC_SITE:%s:%s";
}
