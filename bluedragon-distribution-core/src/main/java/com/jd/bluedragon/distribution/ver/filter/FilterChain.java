package com.jd.bluedragon.distribution.ver.filter;


import com.jd.bluedragon.distribution.ver.domain.FilterContext;

/**
 * @author dudong
 * @date 2016/2/29
 */
public abstract class FilterChain implements Filter {

    @Override
    public abstract void doFilter(FilterContext request, FilterChain chain) throws Exception;
}
