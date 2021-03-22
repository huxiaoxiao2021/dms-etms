package com.jd.bluedragon.distribution.ver.filter.filters;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.WaybillCache;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.BaseMinorManager;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.funcSwitchConfig.FuncSwitchConfigEnum;
import com.jd.bluedragon.distribution.funcSwitchConfig.TraderMoldTypeEnum;
import com.jd.bluedragon.distribution.funcSwitchConfig.YnEnum;
import com.jd.bluedragon.distribution.funcSwitchConfig.dao.FuncSwitchConfigDao;
import com.jd.bluedragon.distribution.funcSwitchConfig.domain.FuncSwitchConfigAllPureDto;
import com.jd.bluedragon.distribution.funcSwitchConfig.domain.FuncSwitchConfigCondition;
import com.jd.bluedragon.distribution.funcSwitchConfig.service.FuncSwitchConfigService;
import com.jd.bluedragon.distribution.loadAndUnload.service.UnloadCarService;
import com.jd.bluedragon.distribution.packageWeighting.PackageWeightingService;
import com.jd.bluedragon.distribution.rule.domain.Rule;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.distribution.waybill.service.WaybillCacheService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.coo.ucc.common.utils.JsonUtils;
import com.jd.etms.waybill.constant.WaybillCodePattern;
import com.jd.etms.waybill.util.UniformValidateUtil;
import com.jd.jddl.executor.function.scalar.filter.In;
import com.jd.ldop.basic.dto.BasicTraderNeccesaryInfoDTO;
import com.jd.ql.dms.common.domain.JdResponse;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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

    @Autowired
    private BaseMinorManager baseMinorManager;

    @Autowired
    private UnloadCarService unloadCarService;

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
        FuncSwitchConfigAllPureDto funcSwitchConfigAllPureDto = packFuncSwitchConfigAllPureDto(request);
        boolean isAllPureNeedWeight =  funcSwitchConfigService.isAllPureValidateWeight(funcSwitchConfigAllPureDto);

        boolean isNeedWeight = StringUtils.isNotBlank(waybillSign) && BusinessHelper.isValidateWeightVolume(waybillSign,switchOn)
                && !WaybillUtil.isReturnCode(waybillCode);

        if(logger.isInfoEnabled()){
            logger.info("运单{}无重量判断标识 isEconomicNetNeedWeight:{},isAllPureNeedWeight:{},isNeedWeight:{}",waybillCode,isEconomicNetNeedWeight,isAllPureNeedWeight,isNeedWeight);
        }
        if(logger.isInfoEnabled()) {
            logger.info("无重量体积校验：waybillSign=" + waybillSign + ",waybillCode=" + waybillCode + ",packageCode=" + packageCode);
        }
        //众邮无重量拦截
        if( isEconomicNetNeedWeight){
            if(!packageWeightingService.weightVolumeValidate(waybillCode, packageCode)){
                throw new SortingCheckException(SortingResponse.CODE_29403, SortingResponse.MESSAGE_29403);
            }
         //纯配外单无重量拦截-不校验体积
        }else if(isAllPureNeedWeight){
            //是否是KA的重量逻辑校验 66->3
            boolean isNewWeightLogic = BusinessUtil.isKaPackageOrNo(waybillSign);
            if(isNewWeightLogic){
                logger.info("waybillsign66为3增加新的拦截校验,package:{},waybillSin:{}",packageCode,waybillSign);
                InvokeResult<String> result = new InvokeResult<String>();
                result.setCode(InvokeResult.RESULT_SUCCESS_CODE);
                result = unloadCarService.kaWaybillCheck(packageCode,waybillSign,result);
                //如果reusltcode不为200 说明已经被上面方法改变 校验不通过
                if(!Objects.equals(InvokeResult.RESULT_SUCCESS_CODE,result.getCode())){
                    //return result;
                    logger.info("WeighVolumeFilter-doFilter,判断是否KA校验,66位为3,但校验不通过,package:{},waybillSin:{}",packageCode,waybillSign);
                    throw  new SortingCheckException(result.getCode(),result.getMessage());
                }
            }else {//原来逻辑
                JdResponse<Void> jdResponse = funcSwitchConfigService.checkAllPureWeight(request.getWaybillCache(), waybillCode, packageCode);
                if(jdResponse.getCode().equals(SortingResponse.CODE_39002)){
                    throw  new SortingCheckException(jdResponse.getCode() ,SortingResponse.MESSAGE_39002);
                }else if(jdResponse.getCode().equals(SortingResponse.CODE_29419)){
                    throw  new SortingCheckException(jdResponse.getCode() ,SortingResponse.MESSAGE_29419);
                }
            }

        }else if (isNeedWeight) {
            //查询重量体积信息
            if (!packageWeightingService.weightVolumeValidate(waybillCode, packageCode)) {
                if(logger.isInfoEnabled()) {
                    logger.info("本地库未查到重量体积，调用运单接口检查,waybillCode=" + waybillCode + ",packageCode=" + waybillCode);
                }
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


    //封装参数
    private  FuncSwitchConfigAllPureDto packFuncSwitchConfigAllPureDto(FilterContext context){
        FuncSwitchConfigAllPureDto funcSwitchConfigAllPureDto = new FuncSwitchConfigAllPureDto();
        funcSwitchConfigAllPureDto.setWaybillSign(context.getWaybillCache().getWaybillSign());
        funcSwitchConfigAllPureDto.setCreateSiteCode(context.getCreateSiteCode());
        funcSwitchConfigAllPureDto.setCustomerCode(context.getWaybillCache().getCustomerCode());
        funcSwitchConfigAllPureDto.setWaybillCode(context.getWaybillCode());
        return  funcSwitchConfigAllPureDto;
    }
}
