package com.jd.bluedragon.distribution.ver.filter.filters;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.base.domain.BlockResponse;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.distribution.waybill.domain.CancelWaybill;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.MessageFormat;
import java.util.List;


/**
 * @Description 订单修改配送方式或配送时间
 * @author jinjingcheng
 * @date 2019/6/3.
 */
public class WaybillDistributeTypeChangeFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private WaybillService waybillService;

    private static final Integer WAYBILL_SIZE_TO_WARN = 3;

    @Override
    public void doFilter(FilterContext request, FilterChain chain) throws Exception {
        logger.info("TransferHandler处理开始");

        String waybillCode = request.getWaybillCode();
        BlockResponse response = waybillService.checkWaybillBlock(waybillCode, CancelWaybill.FEATURE_TYPE_ORDER_MODIFY);
        logger.info(MessageFormat.format("查询运单号：{0},是否为修改配送方式拦截，返回值{1}", waybillCode, JSON.toJSONString(response)));

        if (response != null && response.getResult()!= null &&!response.getResult()) {
            Long countNoReprint = response.getBlockPackageCount();
            List<String> noPrintPackages = response.getBlockPackages();
            if(noPrintPackages != null && noPrintPackages.size()>0){
                StringBuilder warnMessage = new StringBuilder(noPrintPackages.get(0));
                for(int i = 1; i< noPrintPackages.size();i++){
                    if(i >= WAYBILL_SIZE_TO_WARN){
                        break;
                    }
                    warnMessage.append(",").append(noPrintPackages.get(i));
                }
                throw new SortingCheckException(SortingResponse.CODE_29411, MessageFormat.format(SortingResponse.MESSAGE_29411,waybillCode, warnMessage.toString(),countNoReprint));
            }
        }

        chain.doFilter(request, chain);
    }
}
