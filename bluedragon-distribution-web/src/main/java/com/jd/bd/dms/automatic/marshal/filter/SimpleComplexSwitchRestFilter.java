package com.jd.bd.dms.automatic.marshal.filter;

import com.jd.bluedragon.core.simpleComplex.SimpleComplexSwitchContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * resteasy接口简繁切换过滤器
 *
 * @author hujiping
 * @date 2023/7/28 5:07 PM
 */
@Slf4j
public class SimpleComplexSwitchRestFilter extends OncePerRequestFilter {
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            // 简繁切换标识
            boolean needSimpleComplexFlag = Objects.equals(request.getHeader(SimpleComplexSwitchContext.SIMPLE_COMPLEX_SWITCH_FLAG), SimpleComplexSwitchContext.COMPLEX);
            if(needSimpleComplexFlag){
                SimpleComplexSwitchContext.SimpleComplexRestInfo simpleComplexInfo = new SimpleComplexSwitchContext.SimpleComplexRestInfo();
                simpleComplexInfo.setSimpleComplexFlag(SimpleComplexSwitchContext.COMPLEX);
                SimpleComplexSwitchContext.addRestThreadInfo(simpleComplexInfo);
            }
            filterChain.doFilter(request, response); 
        }finally {
            try {
                // 清除标识
                SimpleComplexSwitchContext.clearRestThreadInfo();
            }catch (Exception e){
                log.error("清除标识失败!");
            }   
        }
    }
}
