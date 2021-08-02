package com.jd.bluedragon.distribution.ver.filter.filters;

import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.distribution.funcSwitchConfig.FuncSwitchConfigDto;
import com.jd.bluedragon.distribution.funcSwitchConfig.FuncSwitchConfigEnum;
import com.jd.bluedragon.distribution.funcSwitchConfig.service.FuncSwitchConfigService;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.distribution.whitelist.DimensionEnum;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.DmsMessageConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 预售发货拦截处理
 * @author wuyoude
 *
 */
public class PreSellInterceptFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private FuncSwitchConfigService funcSwitchConfigService;

    @Override
    public void doFilter(FilterContext request, FilterChain chain) throws Exception {
        String sendPay = null;
        if (request.getWaybillCache() != null) {
        	sendPay = request.getWaybillCache().getSendPay();
        }
        String waybillSign = null;
        if(request.getWaybillCache() != null){
            waybillSign = request.getWaybillCache().getWaybillSign();
        }
        //预售未付款，提示退仓（正向单）
        if((BusinessUtil.isWaybillMarkForward(waybillSign) || BusinessUtil.isForeignForward(waybillSign))
                && BusinessUtil.isPreSellWithNoPayToWms(sendPay)) {
            throw new SortingCheckException(DmsMessageConstants.CODE_29419, HintService.getHintWithFuncModule(HintCodeConstants.PRE_SELL_WITHOUT_FINAL_PAY, request.getFuncModule()));
        }
        //预售未付款，提示暂存分拣（正向单）
        if((BusinessUtil.isWaybillMarkForward(waybillSign) || BusinessUtil.isForeignForward(waybillSign))
                && BusinessUtil.isPreSellWithNoPayStorage(sendPay)
                && checkIsPreSellStorageSite(request.getCreateSiteCode())) {
            throw new SortingCheckException(DmsMessageConstants.CODE_29420, HintService.getHintWithFuncModule(HintCodeConstants.PRE_SELL_WITHOUT_FULL_PAY, request.getFuncModule()));
        }
        chain.doFilter(request, chain);
    }

    /**
     * 判断当前站点是否配置预售分拣暂存拦截
     * @param siteCode
     * @return
     */
    private boolean checkIsPreSellStorageSite(Integer siteCode) {
        try {
            if(siteCode == null){
                return false;
            }
            FuncSwitchConfigDto dto = new FuncSwitchConfigDto();
            dto.setMenuCode(FuncSwitchConfigEnum.FUNCTION_PRE_SELL.getCode());
            dto.setDimensionCode(DimensionEnum.SITE.getCode());
            dto.setSiteCode(siteCode);
            return funcSwitchConfigService.checkIsConfigured(dto);
        }catch (Exception e){
            logger.error("查询当前站点是否配置预售分拣暂存拦截异常!",e);
        }
        return false;
    }
}
