package com.jd.bd.dms.automatic.marshal.filter;

import com.jd.bd.dms.automatic.marshal.DmsRequestWrapper;
import com.jd.bd.dms.automatic.marshal.GodHeader;
import com.jd.bd.dms.automatic.marshal.filerList.PathAdaptor;
import com.jd.bd.dms.automatic.marshal.filterchain.FilterContext;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by xumei3 on 2018/8/27.
 */
public class DmsAuthorizationFilter extends OncePerRequestFilter {
    private Logger logger = LoggerFactory.getLogger(DmsAuthorizationFilter.class);
    private FilterContext filterContext;
    private PathAdaptor pathAdaptor;

    public DmsAuthorizationFilter() {
    }

    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        DmsRequestWrapper requestWrapper = new DmsRequestWrapper(httpServletRequest);
        //在过渡阶段，不一定所有的客户端都能传authorization信息，为了不影响现场操作，判断如果authorization为空的则放行
        if (!this.pathAdaptor.accept(requestWrapper.getRequestURI()) ||
                StringUtils.isBlank(requestWrapper.getHeader(GodHeader.AUTHORIZATION)) ||
                this.filterContext.doFilterChain(requestWrapper, httpServletResponse)) {
            filterChain.doFilter(requestWrapper, httpServletResponse);
        }

    }

    public void setFilterContext(FilterContext filterContext) {
        this.filterContext = filterContext;
    }

    public void setPathAdaptor(PathAdaptor pathAdaptor) {
        this.pathAdaptor = pathAdaptor;
    }

    protected void initFilterBean() throws ServletException {
        WebApplicationContext app = WebApplicationContextUtils.getWebApplicationContext(this.getFilterConfig().getServletContext());
        this.filterContext = (FilterContext) app.getBean("filterContext");
        this.pathAdaptor = (PathAdaptor) app.getBean("pathAdaptor");
        super.initFilterBean();
    }
}
