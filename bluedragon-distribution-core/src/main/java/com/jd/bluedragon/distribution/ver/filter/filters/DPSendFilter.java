package com.jd.bluedragon.distribution.ver.filter.filters;

import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.etms.waybill.domain.Waybill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 德邦发货校验
 */
public class DPSendFilter implements Filter {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private UccPropertyConfiguration uccConfiguration;
    @Autowired
    private WaybillQueryManager waybillQueryManager;

    /**
     * 1. 非德邦一单一件的不能发到德邦虚拟分拣中心
     * @param request
     * @param chain
     * @throws Exception
     */
    @Override
    public void doFilter(FilterContext request, FilterChain chain) throws Exception {
        String waybillCode = request.getWaybillCode();
        Integer receiveSiteCode = request.getReceiveSiteCode();
        List<Integer> dpSiteCodeList = uccConfiguration.getDpSiteCodeList();
        int packageNum = WaybillUtil.getPackNumByPackCode(request.getPackageCode());

        // 非一单一件发到德邦分拣中心时拦截
        if(packageNum > 1 && BusinessHelper.isDPSiteCode(dpSiteCodeList, receiveSiteCode)){
            logger.warn("非一单一件不能发到德邦分拣中心,waybill:{},receiveSiteCode:{}", waybillCode, receiveSiteCode);
            throw new SortingCheckException(SortingResponse.CODE_DP_SEND_ERROR,
                    HintService.getHintWithFuncModule(HintCodeConstants.NOT_DP_WAYBILL_WRONG_SEND_MSG, request.getFuncModule()));
        }

        //非德邦单发到德邦分拣中心时拦截
        if(BusinessHelper.isDPSiteCode(dpSiteCodeList, receiveSiteCode)){
            Waybill waybill = waybillQueryManager.queryWaybillByWaybillCode(waybillCode);
            if(waybill != null && waybill.getWaybillExt() != null){
                if(!BusinessHelper.isDPWaybill(waybill.getWaybillExt().getThirdCarrierFlag())){
                    logger.warn("非德邦单不能发到德邦分拣中心,waybill:{},receiveSiteCode:{},ThirdCarrierFlag:{}", waybillCode,
                            receiveSiteCode, waybill.getWaybillExt().getThirdCarrierFlag());
                    throw new SortingCheckException(SortingResponse.CODE_DP_SEND_ERROR,
                            HintService.getHintWithFuncModule(HintCodeConstants.NOT_DP_WAYBILL_WRONG_SEND_MSG, request.getFuncModule()));
                }
            }
        }
        chain.doFilter(request, chain);
    }


}
