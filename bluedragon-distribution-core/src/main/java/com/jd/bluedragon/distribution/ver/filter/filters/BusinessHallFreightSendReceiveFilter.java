package com.jd.bluedragon.distribution.ver.filter.filters;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.WaybillTraceManager;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.service.common.utils.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 寄付营业厅运单，校验是否有揽收完成节点，没有揽收完成 进行拦截
 * @author : xumigen
 * @date : 2019/5/28
 */
public class BusinessHallFreightSendReceiveFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    WaybillTraceManager waybillTraceManager;

    @Override
    public void doFilter(FilterContext request, FilterChain chain) throws Exception {
        logger.info("do BusinessHallFreightSendReceiveFilter process packageCode[{}]" + request.getPackageCode());
        if(! this.businessHallFreightSendReceiveCheck(request.getWaybillCode(),request.getWaybillCache().getWaybillSign())){
            throw new SortingCheckException(SortingResponse.CODE_29414, SortingResponse.MESSAGE_29414);
        }
        chain.doFilter(request, chain);
    }

    private boolean businessHallFreightSendReceiveCheck(String waybillCode,String waybillSign) {
        if(! BusinessUtil.isBusinessHallFreightSendAndForward(waybillSign)) {
            return Boolean.TRUE;
        }
        Set<Integer> stateSet = new HashSet<>();
        stateSet.add(Constants.WAYBILL_TRACE_STATE_RECEIVE);
        List result = waybillTraceManager.getAllOperationsByOpeCodeAndState(waybillCode, stateSet);
        return CollectionUtils.isNotEmpty(result)? Boolean.TRUE : Boolean.FALSE;
    }
}
