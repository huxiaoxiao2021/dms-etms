package com.jd.common.authorization;

import com.jd.common.springmvc.interceptor.SpringAuthorizationInterceptor;
import com.jd.common.web.LoginContext;
import com.jd.uim.annotation.Authorization;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by shipeilin on 2018/1/25.
 */
public class DmsUimAuthorizationInterceptor extends SpringAuthorizationInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(DmsUimAuthorizationInterceptor.class);
    private String excludePath;
    private List<String> excludePathCache;

    /**
     * 忽略auth权限校验的人员，逗号分隔
     */
    private String excludeUsers;

    private List<String> ignoreAuthUsers;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Authorization annotation = null;

        try {
            if(handler instanceof HandlerMethod) {
                annotation = (Authorization)((HandlerMethod)handler).getMethodAnnotation(Authorization.class);
                if(annotation != null) {
                    String e = annotation.value();
                    if(StringUtils.isBlank(e) || isExclude(e)) {
                        return true;
                    }

                    if (curUserIgnoreAuth()) {
                        return true;
                    }

                    String username = this.getUsername();
                    if(username != null) {
                        if(LOG.isDebugEnabled()) {
                            LOG.debug("检查" + username + "是否有" + e + "权限");
                        }

                        if(this.hrmPrivilegeHelper.hasHrmPrivilege(username, e)) {
                            return true;
                        }

                        request.getRequestDispatcher("/permission/toIndex/"+e).forward(request,response);
                        return false;
                    }
                }

                return true;
            } else {
                if(LOG.isDebugEnabled()) {
                    LOG.debug("无Authorization注解配置");
                }

                return true;
            }
        } catch (Exception var7) {
            LOG.error("权限拦截异常:" + var7.getMessage());
            return false;
        }
    }
    public void setExcludePath(String excludePath) {
        this.excludePath = excludePath;
        if(StringUtils.isNotBlank(excludePath)) {
            this.excludePathCache = new ArrayList();
            String[] path = excludePath.split(",");
            String[] arr$ = path;
            int len$ = path.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                String p = arr$[i$];
                this.excludePathCache.add(p.trim());
            }
        }

    }
    /**
     * 确定资源码是否需要过滤掉
     * @param code
     * @return
     */
    private boolean isExclude(String code){
        if(this.excludePathCache != null && this.excludePathCache.contains(code)){
            return true;
        }
        return false;
    }

    public void setExcludeUsers(String excludeUsers) {
        this.excludeUsers = excludeUsers;
        if (!StringUtils.isEmpty(excludeUsers)) {
            try {
                ignoreAuthUsers = Arrays.asList(StringUtils.split(excludeUsers, ","));
            }
            catch (Exception e) {
                LOG.error("Failed to parse auth exclude users.{}", excludeUsers, e);
            }
        }
    }

    /**
     * 当前登录人是否忽略权限校验
     * @return
     */
    private boolean curUserIgnoreAuth() {
        boolean ignored = false;
        LoginContext loginContext = LoginContext.getLoginContext();
        if (loginContext != null) {
            ignored = !CollectionUtils.isEmpty(this.ignoreAuthUsers) && this.ignoreAuthUsers.contains(loginContext.getPin());
            if (ignored) {
                if (LOG.isInfoEnabled()) {
                    LOG.info("Erp User:{} ignore auth validation.", loginContext.getPin());
                }
            }
        }
        return ignored;
    }
}
