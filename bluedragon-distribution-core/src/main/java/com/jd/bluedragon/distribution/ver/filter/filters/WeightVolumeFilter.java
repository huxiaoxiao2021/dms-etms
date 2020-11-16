package com.jd.bluedragon.distribution.ver.filter.filters;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.WaybillCache;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.funcSwitchConfig.FuncSwitchConfigEnum;
import com.jd.bluedragon.distribution.funcSwitchConfig.TraderMoldTypeEnum;
import com.jd.bluedragon.distribution.funcSwitchConfig.YnEnum;
import com.jd.bluedragon.distribution.funcSwitchConfig.dao.FuncSwitchConfigDao;
import com.jd.bluedragon.distribution.funcSwitchConfig.domain.FuncSwitchConfigCondition;
import com.jd.bluedragon.distribution.funcSwitchConfig.service.FuncSwitchConfigService;
import com.jd.bluedragon.distribution.packageWeighting.PackageWeightingService;
import com.jd.bluedragon.distribution.rule.domain.Rule;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.distribution.waybill.service.WaybillCacheService;
import com.jd.bluedragon.distribution.whitelist.DimensionEnum;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.ldop.basic.api.BasicTraderAPI;
import com.jd.ldop.basic.dto.BasicTraderNeccesaryInfoDTO;
import com.jd.ldop.basic.dto.ResponseDTO;
import com.jd.ql.dms.common.cache.CacheService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 漏称重量方校验
 *
 * @author tangchunqing
 * @Description: 类描述信息
 * @date 2018年04月23日 10时:51分
 */
public class WeightVolumeFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private WaybillCacheService waybillCacheService;

    @Autowired
    PackageWeightingService packageWeightingService;

    @Resource
    private UccPropertyConfiguration uccPropertyConfiguration;

    @Autowired
    private FuncSwitchConfigService funcSwitchConfigService;

    private static final String RULE_WEIGHT_VOLUMN_SWITCH = "1123";
    private static final String SWITCH_OFF = "1";

    @Override
    public void doFilter(FilterContext request, FilterChain chain) throws Exception {
        //是否开启C网运单校验 标识
        boolean switchOn = false;
        //默认false 不开全国校验
        if(uccPropertyConfiguration.getWeightVolumeFilterWholeCountryFlag()){
            switchOn = true;
        }else{
        //加一个分拣规则
            Rule rule = null;
            try {
                rule = request.getRuleMap().get(RULE_WEIGHT_VOLUMN_SWITCH);
            } catch (Exception e) {
                logger.warn("站点 [" + request.getCreateSiteCode() + "] 类型 [" + RULE_WEIGHT_VOLUMN_SWITCH + "] 没有匹配的规则");
            }
            if (rule != null && SWITCH_OFF.equals(rule.getContent())) {//为了上部分站点测试，暂时改变参数的含义 推全国再改回去
                switchOn = true;
            }
        }
        String waybillCode = request.getWaybillCode();
        String waybillSign = request.getWaybillCache().getWaybillSign();
        String packageCode = request.getPackageCode();

        //判断waybillSign是否满足条件 判断 是否是经济网单号 20200824增加无需拦截经济网逆向运单
        boolean isEconomicNetNeedWeight = isEconomicNetValidateWeight(waybillCode, waybillSign,request);

        //判断纯配外单是否需要无重量拦截
        boolean isAllPureNeedWeight =  isAllPureValidateWeight(waybillSign,waybillCode,request);

        boolean isNeedWeight = StringUtils.isNotBlank(waybillSign) && BusinessHelper.isValidateWeightVolume(waybillSign,switchOn)
                && !WaybillUtil.isReturnCode(waybillCode);

        if( isEconomicNetNeedWeight || isAllPureNeedWeight){
            if(!packageWeightingService.weightVolumeValidate(waybillCode, packageCode)){
                throw new SortingCheckException(SortingResponse.CODE_29403, SortingResponse.MESSAGE_29403);
            }
        }else if ( isNeedWeight ) {
            logger.info("无重量体积校验：waybillSign=" + waybillSign + ",waybillCode=" + waybillCode + ",packageCode=" + packageCode);
            //查询重量体积信息
            if (!packageWeightingService.weightVolumeValidate(waybillCode, packageCode)) {
                logger.info("本地库未查到重量体积，调用运单接口检查,waybillCode=" + waybillCode + ",packageCode=" + waybillCode);
                //从运单接口查  数据没有下放的极端情况下 一般不会走
                WaybillCache waybillNoCache = waybillCacheService.getNoCache(waybillCode);
                if (waybillNoCache == null) {
                    throw new SortingCheckException(SortingResponse.CODE_39002, SortingResponse.MESSAGE_39002);
                }
                //判断运单上重量体积（复重：AGAIN_WEIGHT、复量方SPARE_COLUMN2）是否同时存在（非空，>0）
                if (waybillNoCache.getAgainWeight() == null || waybillNoCache.getAgainWeight() <= 0
                        || StringUtils.isEmpty(waybillNoCache.getSpareColumn2()) || Double.parseDouble(waybillNoCache.getSpareColumn2()) <= 0) {
                    logger.warn("未查询到重量体积信息,waybillCode=" + waybillCode + ",packageCode=" + packageCode);

                    /* C网提示，B网拦截 */
                    if(BusinessUtil.isSignChar(waybillSign, 40, '0')){
                        throw new SortingCheckException(SortingResponse.CODE_39128, SortingResponse.MESSAGE_39128);
                    }else {
                        throw new SortingCheckException(SortingResponse.CODE_29403, SortingResponse.MESSAGE_29403);
                    }
                }
            }
        }
        chain.doFilter(request, chain);
    }

    /**
     * 众邮运单是否拦截 -
     * @param waybillCode
     * @param waybillSign
     * @return
     */
    private boolean isEconomicNetValidateWeight(String waybillCode, String waybillSign,FilterContext request) {

        //首先满足经济网的单子----
        if(!BusinessUtil.isEconomicNetValidateWeightVolume(waybillCode, waybillSign)){
            return  false;
        }

        //如果是全国有效,直接返回不拦截
        if(!funcSwitchConfigService.getAllCountryFromCacheOrDb(FuncSwitchConfigEnum.FUNCTION_ALL_MAIL.getCode())){
            return false;
        }

        //不是全国-查询站点维度
        if(request.getCreateSiteCode()!=null){
            Integer  siteCode = request.getCreateSiteCode();
            //当缓存中存在时
            return funcSwitchConfigService.getSiteFlagFromCacheOrDb(FuncSwitchConfigEnum.FUNCTION_ALL_MAIL.getCode(),siteCode);
        }
        return true;
    }

    /**
     * 纯配运单拦截 (除信任商家、内部商家外的纯配外单)
     * @param waybillSign
     * @param request
     * @return
     */
    private boolean isAllPureValidateWeight(String waybillSign,String waybillCode,FilterContext request){
        //1.是否是纯配外单
        if(!BusinessHelper.isAllPureOutWaybill(waybillSign)){
            return  false;
        }

        //2.信任商家不拦截
        if(!BusinessHelper.isTrust(waybillSign)){
            return false;
        }

        //3.内部商家不拦截
        if(!funcSwitchConfigService.isInsideMode(waybillCode)){
            return false;
        }

        //4.如果是全国有效,直接返回不拦截
        if(!funcSwitchConfigService.getAllCountryFromCacheOrDb(FuncSwitchConfigEnum.FUNCTION_COMPLETE_DELIVERY.getCode())){
            return false;
        }

        //不是全国-查询站点维度
        if(request.getCreateSiteCode()!=null){
            Integer  siteCode = request.getCreateSiteCode();
            //当缓存中存在时
            return funcSwitchConfigService.getSiteFlagFromCacheOrDb(FuncSwitchConfigEnum.FUNCTION_COMPLETE_DELIVERY.getCode(),siteCode);
        }

        return  true;
    }
}
