package com.jd.bluedragon.distribution.ver.filter.filters;

import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.reverse.domain.ReverseStockInDetailTypeEnum;
import com.jd.bluedragon.distribution.reverse.service.ReverseStockInDetailService;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.utils.SiteHelper;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @program: ql-dms-dmsver
 * @description: 逆向入库单收货拦截
 *
 *
 * @author: liuduo8
 * @create: 2020-01-02 16:55
 **/

public class ReverseInStockCheckFilter implements Filter {

    @Autowired
    private ReverseStockInDetailService reverseStockInDetailService;

    @Override
    public void doFilter(FilterContext request, FilterChain chain) throws Exception {

        if(SiteHelper.isSpsHouse(request.getReceiveSite())){
            //现阶段只处理C2C入备件库 如需其他业务场景请自行扩展
            if(reverseStockInDetailService.isReceive(request.getWaybillCode(), ReverseStockInDetailTypeEnum.C2C_REVERSE_SPWMS)) {
                throw new SortingCheckException(SortingResponse.CODE_C2C_SPWMS_29317, SortingResponse.MESSAGE_C2C_SPWMS_29317);
            }
        }
        chain.doFilter(request,chain);
    }
}
