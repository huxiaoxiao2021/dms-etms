package com.jd.bluedragon.distribution.ver.filter.chains;


import com.jd.bluedragon.distribution.ver.domain.FilterRequest;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xumei3 on 2018/4/28.
 */
public class BoardCombinationChain extends FilterChain {

    private int filterIndex = 0;
    private List<Filter> filterList = new ArrayList<Filter>();
    @Override
    public void doFilter(FilterRequest request, FilterChain chain) throws Exception {
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
