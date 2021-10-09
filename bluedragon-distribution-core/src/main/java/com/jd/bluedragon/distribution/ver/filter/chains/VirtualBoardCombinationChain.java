package com.jd.bluedragon.distribution.ver.filter.chains;

import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;

import java.util.ArrayList;
import java.util.List;

/**
 * 虚拟组板扫描拦截链
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-08-20 09:43:18 周五
 */
public class VirtualBoardCombinationChain extends FilterChain {

    private int filterIndex = 0;
    private List<Filter> filterList = new ArrayList<Filter>();
    @Override
    public void doFilter(FilterContext request, FilterChain chain) throws Exception {
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
