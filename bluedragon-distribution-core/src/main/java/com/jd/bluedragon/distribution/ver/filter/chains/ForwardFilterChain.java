package com.jd.bluedragon.distribution.ver.filter.chains;


import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.ObjectHelper;
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
        if (filterIndex == filterList.size())
            return;
        filterList.get(filterIndex++).doFilter(request, chain);
    }

    private boolean checkIfNeedSkipFilter(int i,FilterContext context) {
        if (ObjectHelper.isNotNull(context) && context.getBitCode() > 0 && NumberHelper.isZeroBitSet(context.getBitCode(),41, i)){
            logger.info("ForwardFilterChain curr filter {} need skip filter",filterList.get(i).getClass().getName());
            filterIndex++;
            return  true;
        }
        return false;
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
