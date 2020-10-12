package com.jd.bluedragon.distribution.ver.filter.filters;

import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.product.domain.Product;
import com.jd.bluedragon.distribution.product.service.ProductService;
import com.jd.bluedragon.distribution.receive.domain.CanReceiveTypeEnum;
import com.jd.bluedragon.distribution.receive.domain.ReceiveTypeEnum;
import com.jd.bluedragon.distribution.reverse.dao.ReverseReceiveDao;
import com.jd.bluedragon.distribution.reverse.domain.ReverseReceive;
import com.jd.bluedragon.distribution.reverse.service.ReverseReceiveService;
import com.jd.bluedragon.distribution.rule.domain.Rule;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.utils.SiteHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author dudong
 * @version 1.0
 * @date 2016/3/3
 */
public class WarehouseFilter implements Filter {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ReverseReceiveService reverseReceiveService;

    @Autowired
    private ProductService productService;

    @Override
    public void doFilter(FilterContext request, FilterChain chain) throws Exception {
        if(logger.isInfoEnabled()){
            logger.info("do filter process...");
        }
        Rule rule4 = request.getRuleMap().get("1100"); // FDC仓库编号
        Boolean isStoreHouse = SiteHelper.isStoreHouse(request.getReceiveSite());
        //是否退库（仓库）
        if (isStoreHouse) {

            ReverseReceive reverseReceive = null;
            try {
                reverseReceive = reverseReceiveService.findByPackageCode(request.getWaybillCode());
            } catch (Exception e) {
                logger.error("获取逆向收货信息失败，运单号{}", request.getWaybillCode(), e);
            }
            if (reverseReceive != null
                    && reverseReceive.getReceiveType() == ReceiveTypeEnum.REVERSE_RECEIVE_WMS.getCode()
                    && reverseReceive.getCanReceive() == CanReceiveTypeEnum.REVERSE_RECEIVE.getCode()) {
                throw new SortingCheckException(SortingResponse.CODE_10009 , SortingResponse.MESSAGE_WAYBILL_STOREHOUSE_RECEIVED);
            }

            Integer result = productService.getWaybillLossResult(request.getWaybillCode());
            if (result == 1)
                throw new SortingCheckException(SortingResponse.CODE_10010, SortingResponse.MESSAGE_WAYBILL_WHOLE_LOSS);
            //退库库房是否FDC仓
            if (!SiteHelper.matchSiteRule(rule4.getContent(), request.getsReceiveSiteCode())) {
                //验证运单库房ID(即预分拣站点字段)与逆向库房ID(即分拣目的地)是否一致
                if (!request.getsReceiveSiteCode().equals(request.getWaybillSite().getCode().toString())) {
                    throw new SortingCheckException(SortingResponse.CODE_39120, SortingResponse.MESSAGE_39120);
                }
            }
        }

        chain.doFilter(request, chain);
    }
}
