package com.jd.bluedragon.common.dto.operation.workbench.send.request;

import java.io.Serializable;
import java.util.Map;

/**
 * 拦截规则忽略条件
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2022-09-11 15:03:13 周日
 */
public class ValidateIgnore implements Serializable {
    private static final long serialVersionUID = -6257942488694697446L;

    public static final String MATCH_TYPE_IN = "in";
    public static final String MATCH_TYPE_NOT_IN = "not in";
    public static final String MATCH_TYPE_EQ = "eq";
    public static final String MATCH_TYPE_NEQ = "neq";

    /**
     * 忽略路由下一站校验
     */
    public static final String IGNORE_ROUTER_NEXT_SITE = "ignore_router_next_site";

    /**
     * 路由校验忽略条件
     */
    private ValidateIgnoreRouterCondition validateIgnoreRouterCondition;

    public ValidateIgnoreRouterCondition getValidateIgnoreRouterCondition() {
        return validateIgnoreRouterCondition;
    }

    public void setValidateIgnoreRouterCondition(ValidateIgnoreRouterCondition validateIgnoreRouterCondition) {
        this.validateIgnoreRouterCondition = validateIgnoreRouterCondition;
    }
}
