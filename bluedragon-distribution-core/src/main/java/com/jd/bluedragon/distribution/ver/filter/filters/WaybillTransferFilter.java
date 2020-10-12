package com.jd.bluedragon.distribution.ver.filter.filters;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.base.domain.BlockResponse;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.MessageFormat;
import java.util.List;

public class WaybillTransferFilter implements Filter {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private WaybillService waybillService;

    private static final Integer FEATURE_TYPE_B2C = 8;
    private static final Integer WAYBILL_SIZE_TO_WARN = 3;

    @Override
    public void doFilter(FilterContext request, FilterChain chain) throws Exception {
        logger.info("TransferHandler处理开始");

        String waybillCode = request.getWaybillCode();
        BlockResponse response = waybillService.checkWaybillBlock(waybillCode, FEATURE_TYPE_B2C);
        logger.info("调用拦截接口-cancelWaybillService.checkWaybillBlock返回值:" + JSON.toJSONString(response));

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
                throw new SortingCheckException(SortingResponse.CODE_29410, MessageFormat.format(SortingResponse.MESSAGE_29410,waybillCode, warnMessage.toString(),countNoReprint));
            }
        }

        chain.doFilter(request, chain);
    }
}
