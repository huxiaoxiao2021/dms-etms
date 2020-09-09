package com.jd.bluedragon.distribution.ver.filter.chains;


import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dudong
 * @date 2016/2/29
 */
public class ForwardFilterChain extends FilterChain {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private int filterIndex = 0;
    private List<Filter> filterList = new ArrayList<Filter>();
    @Override
    public void doFilter(FilterContext request, FilterChain chain) throws Exception {
        logger.info("正向拦截器链路：{}, filterIndex={},filterList={}", this, filterIndex, filterList);
        if (filterIndex == filterList.size())
            return;
        filterList.get(filterIndex++).doFilter(request, chain);
    }

    public List<Filter> getFilterList() {
        return filterList;
    }

    public void setFilterList(List<Filter> filterList) {
        this.filterList = filterList;
    }

    public void destory(){
        this.filterList = null;
    }
}
