package com.jd.bd.dms.automatic.marshal.filter;

import com.jd.bd.dms.automatic.marshal.GodHeader;
import com.jd.bluedragon.utils.PropertiesHelper;
import com.jd.bluedragon.utils.ServletRequestHelper;
import com.jd.bluedragon.utils.StringHelper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <p>
 *     分拣web自定义的接口拦截链处理类，过渡期自定义，如果没有JD-Authorization请求头的话，认为客户端处在过渡期，将不进行拦截
 *     过渡期完事之后，将删除该类，用父类进行替代，即对所有配置的接口访问进行拦截处理
 *
 * @see com.jd.bd.dms.automatic.marshal.filter.DmsAuthorizationFilter
 * @author wuzuxiang
 * @since 2019/11/20
 **/
@Deprecated
public class DmsOAuthInterceptFilter extends DmsAuthorizationFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(DmsOAuthInterceptFilter.class);

    private static final String secretKey = "dms.rest.interceptor.protected.code";

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

        String opCode = httpServletRequest.getHeader(GodHeader.OP_CODE);
        String authorization = httpServletRequest.getHeader(GodHeader.AUTHORIZATION);
        boolean secretBool = StringHelper.isNotEmpty(opCode) && opCode.equals(PropertiesHelper.newInstance().getValue(secretKey));/* 内部后门 */
        boolean temporaryBool = StringHelper.isEmpty(authorization);/* 过渡期的临时保护 */
        /* 过渡期间对传空的的进行保护处理，和内部后门JD-opCode的特殊值进行保护处理 */
        if (temporaryBool) {
            LOGGER.warn("该客户端本次调用未进行rest加密鉴权,如果过渡期已过，将进行强制拦截，客户端IP:{}", ServletRequestHelper.getRealIpAddress(httpServletRequest));
            filterChain.doFilter(httpServletRequest,httpServletResponse);
        } else if (secretBool) {
            LOGGER.info("内部调用，未拦截，客户端IP:{}", ServletRequestHelper.getRealIpAddress(httpServletRequest));
            filterChain.doFilter(httpServletRequest,httpServletResponse);
        } else {
            super.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);
        }
    }
}
