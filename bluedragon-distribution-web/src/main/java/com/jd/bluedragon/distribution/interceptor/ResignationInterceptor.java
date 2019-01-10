package com.jd.bluedragon.distribution.interceptor;

import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.common.web.LoginContext;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ResignationInterceptor implements HandlerInterceptor {

    private final Logger logger = Logger.getLogger(this.getClass());
    private String excludePath;
    private List<String> excludePathCache;

    @Autowired
    private BaseMajorManager baseMajorManager;

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
        LoginContext loginContext = LoginContext.getLoginContext();
        try {
            if(loginContext != null){
                BaseStaffSiteOrgDto basestaffDto = baseMajorManager.getBaseStaffIgnoreIsResignByErp(loginContext.getPin());
                if(basestaffDto != null && basestaffDto.getIsResign() != null &&  basestaffDto.getIsResign() == 1){
                    return true;
                }
                this.logger.error("该登陆用户:" + loginContext.getPin() + "已离职！");
                //跳转到提示页面
                this.sendErrorMessage(request, response);
            }
        }catch (Exception e){
            this.logger.error("通过" + loginContext.getPin() + "调用基础资料接口失败！");
        }
        return false;
    }

    private void sendErrorMessage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=UTF-8");
        response.sendError(402);
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
