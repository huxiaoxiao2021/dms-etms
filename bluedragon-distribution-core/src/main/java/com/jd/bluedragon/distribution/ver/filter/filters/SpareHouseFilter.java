package com.jd.bluedragon.distribution.ver.filter.filters;

import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.product.service.ProductService;
import com.jd.bluedragon.distribution.reverse.domain.ReverseReceive;
import com.jd.bluedragon.distribution.reverse.service.ReverseReceiveService;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.utils.SiteHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author dudong
 * @version 1.0
 * @date 2016/3/3
 */
public class SpareHouseFilter implements Filter {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ReverseReceiveService reverseReceiveService;

    @Autowired
    private ProductService productService;

    @Override
    public void doFilter(FilterContext request, FilterChain chain) throws Exception {

        Boolean isSpsHouse = SiteHelper.isSpsHouse(request.getReceiveSite());
        if (isSpsHouse) {
            ReverseReceive reverseReceive = reverseReceiveService.findByPackageCode(request.getWaybillCode());
            //reveser_receive.can_receive==1时库已收货
            if (reverseReceive != null && reverseReceive.getReceiveType() == 1 && reverseReceive.getCanReceive() == 1) {
                throw new SortingCheckException(SortingResponse.CODE_10010,SortingResponse.MESSAGE_WAYBILL_STOREHOUSE_RECEIVED);
            }
            Integer result = productService.getWaybillLossResult(request.getWaybillCode());
            if(result != -1)
                throw new SortingCheckException(SortingResponse.CODE_10011,SortingResponse.MESSAGE_WAYBILL_HAS_LOSS);
        }

        chain.doFilter(request, chain);
    }
}
