package com.jd.bluedragon.distribution.ver.filter.filters;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.mixedPackageConfig.enums.MixedTypeEnum;
import com.jd.bluedragon.distribution.mixedPackageConfig.enums.RuleTypeEnum;
import com.jd.bluedragon.distribution.mixedPackageConfig.enums.YNEnum;
import com.jd.bluedragon.distribution.mixedPackageConfig.service.MixedPackageConfigService;
import com.jd.bluedragon.distribution.rule.domain.Rule;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.SiteHelper;
import com.jd.bluedragon.utils.WaybillCacheHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author dudong
 * @version 1.0
 * @date 2016/3/2
 */
public class CrossDistributionFilter implements Filter {
    private static final Integer transferStationSiteType = 256;
    private static final Logger logger = LoggerFactory.getLogger(CrossDistributionFilter.class);
    private static final String SWITCH_ON = "1";


    @Value("${useNewMixedConfig}")
    protected String useNewMixedConfig;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private MixedPackageConfigService mixedPackageConfigService;

    public static final String RULE_TYPE_MIX_BOX = "1125";

    @Override
    public void doFilter(FilterContext request, FilterChain chain) throws Exception {
        //大件订单 不进行预分拣相关校验 tangcq2018年11月2日10:28:42
        if (BusinessHelper.isLasOrder(request.getWaybillCache().getWaybillSign())){
            chain.doFilter(request, chain);
            return;
        }

        /* 填航空仓不进行目的地校验 */
        if (WaybillCacheHelper.isAirWaybill(request.getWaybillCache())) {
            chain.doFilter(request, chain);
            return;
        }

        Integer siteCode = request.getWaybillCache().getSiteCode();
        Integer preSiteDmsId = this.getTargetDmsCenter(request.getCreateSiteCode(), request.getWaybillCache().getSiteCode());


        //如果箱号目的地和预分拣站点相同 或者和预分拣站点所属分拣中心相同则通过
        if (request.getBox() != null && request.getBox().getReceiveSiteCode()!=null &&
                (request.getBox().getReceiveSiteCode().equals(siteCode) ||
                request.getBox().getReceiveSiteCode().equals(preSiteDmsId))) {
            chain.doFilter(request, chain);
            return ;
        }

        Rule ruleMixBox = null;
        try {
            ruleMixBox = request.getRuleMap().get(RULE_TYPE_MIX_BOX);
        } catch (Exception e) {
            logger.warn("站点 [" + request.getCreateSiteCode() + "] 类型 [" + RULE_TYPE_MIX_BOX + "] 没有匹配的规则");
        }


        //如果在规则配置表配置了使用新混装规则，则走新混装逻辑，否则走老逻辑
        if (request.getBox() != null && YNEnum.Y.getCode().equals(useNewMixedConfig)
                && MixedTypeEnum.MIXED.getCode().equals(request.getBox().getMixBoxType())
                && ruleMixBox != null && ruleMixBox.getContent().equals("1")) {
            if (!passMixedConfig(request)) {
                throw new SortingCheckException(SortingResponse.CODE_39001,
                        SortingResponse.MESSAGE_39011);
            }
        } else {
            //region 跨分拣中心判断，如果目的地是分拣中心，并且不是中转站，则弹确认框提示
//            Site waybillSite = waybill.getSiteCode()!=null&&waybill.getSiteCode().intValue()>0?siteService.get(waybill.getSiteCode()):null;
            //机构号判断用运单的预分拣站点的ORGID，如果预分拣站点为空，则默认去运单的ORGID
            Integer waybillOrgid = request.getWaybillSite() != null
                    && request.getWaybillSite().getOrgId() != null ? request.getWaybillSite().getOrgId()
                    : request.getWaybillCache().getOrgId();
            if (SiteHelper.isDistributionCenter(request.getReceiveSite())
                    && !transferStationSiteType.equals(request.getReceiveSite().getSubType()) ) {

                //区域不匹配，直接弹
                //新增逻辑，邹剑 - 取消苏州接货仓（ID：2531）、苏州外单分拣中心（ID 151678）跨分拣中心发货验证提示 2015年9月21日  邮件主题【跨区发货提示取消申请】
                //在 rules表中 如果有1121.content=1 就不做跨分拣中心提示，  如果没有1121 或者 1121.content!=1 就仍然提示
                Rule rule6 = null;
                try {
                    rule6 = request.getRuleMap().get("1121");
                } catch (Exception e) {

                    logger.warn("站点 [" + request.getCreateSiteCode() + "] 类型 [1121] 没有匹配的规则");
                }
                if (rule6 == null || !SWITCH_ON.equals(rule6.getContent())) {
                    //1121 跨分拣中心提示规则，=1 时 才会有提示逻辑，其他都不走提示逻辑
                    if (!waybillOrgid.equals(request.getReceiveSite().getOrgId()))
                        throw new SortingCheckException(SortingResponse.CODE_39001,
                                SortingResponse.MESSAGE_39021);

                    //扫描目的分拣中心与预分拣站点对应分拣中心进行比对,如不一 致则提示
                    Rule rule5 = null;
                    try {
                        rule5 = request.getRuleMap().get("1120");
                    } catch (Exception e) {

                        logger.warn("站点 [" + request.getCreateSiteCode() + "] 类型 [1120] 没有匹配的规则");
                    }

                    if (rule5 == null || Rule.IN.equals(rule5.getInOut())) {

                        //起始分拣中心不能与预分拣中心一样（如一样就是分拣到自己的分拣中心了） and 预分拣中心不能与目的分拣中心一样
                        if (!(request.getReceiveSite().getCode().equals(preSiteDmsId) || request.getReceiveSite().getCode().equals(request.getCreateSiteCode()))) {
                           CallerInfo info = Profiler.registerInfo("DMSVER.CrossDistributionFilter.ump1", Constants.UMP_APP_NAME_DMSVER,false, true);
                            Profiler.registerInfoEnd(info);
                            logger.info("跨分拣中心目的提示waybillCode:" + request.getWaybillCode() + " createSiteCode:" + request.getCreateSiteCode() + " preSiteCode" + request.getWaybillCache().getSiteCode() + " receiveSiteCode:" + request.getReceiveSite().getCode() + " preSiteDmsId:" + preSiteDmsId);
                            throw new SortingCheckException(SortingResponse.CODE_39001,
                                    SortingResponse.MESSAGE_39001);
                        }
                    }
                }
            }

        }


        chain.doFilter(request, chain);
    }

    /**
     * 根据新的混装规则配置表查询是否可以通过
     *
     * @param request 过滤对象
     * @return 是否通过
     */
    private boolean passMixedConfig(FilterContext request) {
        //如果箱子允许混装，则校验是否在混装规则内
        if (MixedTypeEnum.MIXED.getCode().equals(request.getBox().getMixBoxType())) {
            return mixedPackageConfigService.checkMixedPackageConfig(request.getCreateSiteCode(), request.getBox().getReceiveSiteCode(), request.getWaybillCache().getSiteCode(), request.getBox().getTransportType(), RuleTypeEnum.BUILD_PACKAGE.getCode());
        }
        return false;
    }

    public Integer getTargetDmsCenter(Integer startDmsCode, Integer siteCode) {
        try{
            BaseStaffSiteOrgDto  br = this.baseMajorManager.getBaseSiteBySiteId(siteCode);
            if (br != null)
                return br.getDmsId();
        }catch(Exception e){
            logger.error("根据运单号【{}-{}】 获取目的分拣中心信息接口",startDmsCode , siteCode, e);
        }
        return null;
    }
}
