package com.jd.bluedragon.distribution.interceptor;

import com.jd.common.web.LoginContext;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DmsSSOInterceptor implements HandlerInterceptor {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private String excludePath;
    private List<String> excludePathCache;

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object obj, Exception e) throws Exception {

    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object obj,
                           ModelAndView model) throws Exception {
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object obj)
            throws Exception {
        if (this.isExclude(request)) {
            return true;
        }
        String currUserCode = request.getHeader("currusercode");
        LoginContext loginContext = LoginContext.getLoginContext();
        if (StringUtils.isEmpty(currUserCode) || loginContext == null) {
            return true;
        }
        if (!currUserCode.equals(loginContext.getPin())) {
            log.warn("用户不一致：{} != {}",currUserCode, loginContext.getPin());
            response.setStatus(888);
            return false;
        }
        return true;
    }

    public boolean isExclude(HttpServletRequest request) {
        return this.isExclude(request.getRequestURI());
    }

    public boolean isExclude(String uri) {
        if (this.excludePathCache != null && !this.excludePathCache.isEmpty()) {
            Iterator i$ = this.excludePathCache.iterator();

            String path;
            do {
                if (!i$.hasNext()) {
                    return false;
                }

                path = (String) i$.next();
            } while (!uri.startsWith(path));

            return true;
        } else {
            return false;
        }
    }

    public void setExcludePath(String excludePath) {
        this.excludePath = excludePath;
        if (StringUtils.isNotBlank(excludePath)) {
            this.excludePathCache = new ArrayList();
            String[] path = excludePath.split(",");
            String[] arr$ = path;
            int len$ = path.length;

            for (int i$ = 0; i$ < len$; ++i$) {
                String p = arr$[i$];
                this.excludePathCache.add(p.trim());
            }
        }

    }
}
