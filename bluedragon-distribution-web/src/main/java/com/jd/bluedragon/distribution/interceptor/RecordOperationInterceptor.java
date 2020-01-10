package com.jd.bluedragon.distribution.interceptor;

import com.jd.bluedragon.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;

public class RecordOperationInterceptor implements HandlerInterceptor {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

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
        Enumeration<String> paramNames = request.getParameterNames();
        StringBuilder params = new StringBuilder();

        String url = request.getServletPath().toString();

        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            String paramValue = request.getParameter(paramName);

            params.append(paramName + "=" + paramValue);

            if (paramNames.hasMoreElements()) {
                params.append(Constants.SEPARATOR_COMMA);
            }
        }

        this.log.info("url:{}, ip:{}, params:{}",url, request.getRemoteAddr(), params.toString());

        return true;
    }

}
