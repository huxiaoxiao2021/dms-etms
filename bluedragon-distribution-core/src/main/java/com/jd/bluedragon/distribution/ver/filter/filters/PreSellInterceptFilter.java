package com.jd.bluedragon.distribution.ver.filter.filters;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.WaybillQueryManager;
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
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.dto.WaybillAbilityAttrDto;
import com.jd.etms.waybill.dto.WaybillAbilityDto;
import com.jd.etms.waybill.dto.WaybillProductDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 预售发货拦截处理
 * @author wuyoude
 *
 */
public class PreSellInterceptFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private FuncSwitchConfigService funcSwitchConfigService;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    /**
     * 运单预售未付尾款
     */
    private static final String PRODUCT_ABILITY_OF_PRE_SELL_NO_PAY  = "VI007-01";

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
        //增加接货仓 KA安踏预售逻辑 (只考虑ECLP仓配外单场景  减少调用)
        if(BusinessUtil.isEclpAndWmsForDistribution(waybillSign) && request.getCreateSite() != null && request.getCreateSite().getSubType() !=null &&
                Integer.valueOf(Constants.JHC_SITE_TYPE).equals(request.getCreateSite().getSubType())){
            BaseEntity<List<WaybillProductDto>> productAbilities = waybillQueryManager.getProductAbilityInfoByWaybillCode(request.getWaybillCode());
            //获取能力信息
            if(productAbilities != null && !CollectionUtils.isEmpty(productAbilities.getData())){
                for(WaybillProductDto productDto : productAbilities.getData()){
                   if(!CollectionUtils.isEmpty(productDto.getAbilityItems())){
                       for(WaybillAbilityDto abilityDto : productDto.getAbilityItems()){
                           if(!CollectionUtils.isEmpty(abilityDto.getAttrItems())){
                            for( WaybillAbilityAttrDto abilityAttrDto : abilityDto.getAttrItems()){
                                if(PRODUCT_ABILITY_OF_PRE_SELL_NO_PAY.equals(abilityAttrDto.getAttrCode())){
                                    throw new SortingCheckException(DmsMessageConstants.CODE_29420,
                                            HintService.getHintWithFuncModule(HintCodeConstants.PRE_SELL_WITHOUT_FULL_PAY, request.getFuncModule()));
                                }
                            }
                           }
                       }
                   }
                }
            }
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
