package com.jd.bluedragon.distribution.ver.filter.filters;


import com.jd.bluedragon.configuration.DmsConfigManager;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.hint.constants.HintArgsConstants;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.jy.service.transfer.manager.JYTransferConfigProxy;
import com.jd.bluedragon.distribution.sorting.domain.SortingBizSourceEnum;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.domain.Site;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.SiteHelper;
import com.jdl.basic.api.domain.transferDp.ConfigTransferDpSite;
import com.jdl.basic.api.dto.transferDp.ConfigTransferDpSiteMatchQo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 三方-合作站点的包裹可能要通过所绑定的自营分拣中心执行配送，
 * 这里做目的地不一致的拦截验证
 */
public class PartnerSiteFilter implements Filter {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    public static final Integer siteType = 1024;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private JYTransferConfigProxy jyTransferConfigProxy;

    @Autowired
    private DmsConfigManager dmsConfigManager;

    @Override
    public void doFilter(FilterContext request, FilterChain chain) throws Exception {

        Site waybillSite = request.getWaybillSite();



        // 德邦判断提示
        if (dmsConfigManager.getPropertyConfig().isDpSpringSiteCode(request.getReceiveSiteCode())) {
            // 德邦春节项目的错发校验跳过
            final boolean dpSiteCode1Flag = BusinessHelper.isDPSiteCode1(request.getReceiveSite().getSubType());
            if (BusinessHelper.isDPWaybill1_2(request.getWaybillCache().getWaybillSign())) {
                ConfigTransferDpSite configTransferDpSite = jyTransferConfigProxy
                        .queryMatchConditionRecord(request.getCreateSiteCode(),request.getWaybillSite().getCode());
                if (jyTransferConfigProxy.isMatchConfig(configTransferDpSite, request.getWaybillCache().getWaybillSign())) {
                    if (dpSiteCode1Flag) {
                        chain.doFilter(request, chain);
                        return;
                    } else {
                        Map<String, String> hintParams = new HashMap<String, String>();
                        hintParams.put(HintArgsConstants.ARG_FIRST, request.getWaybillCode());
                        throw new SortingCheckException(Integer.valueOf(HintCodeConstants.JY_DP_TRANSFER_MESSAGE),
                                HintService.getHintWithFuncModule(HintCodeConstants.JY_DP_TRANSFER_MESSAGE, request.getFuncModule(), hintParams));
                    }
                } else {
                    if (dpSiteCode1Flag) {
                        Map<String, String> hintParams = new HashMap<String, String>();
                        hintParams.put(HintArgsConstants.ARG_FIRST, request.getWaybillCode());
                        throw new SortingCheckException(Integer.valueOf(HintCodeConstants.JY_DP_TRANSFER_MESSAGE_1),
                                HintService.getHintWithFuncModule(HintCodeConstants.JY_DP_TRANSFER_MESSAGE_1, request.getFuncModule(), hintParams));
                    }
                }
            } else if (dpSiteCode1Flag) {
                throw new SortingCheckException(Integer.valueOf(HintCodeConstants.JY_DP_TRANSFER_MESSAGE_2),
                        HintService.getHintWithFuncModule(HintCodeConstants.JY_DP_TRANSFER_MESSAGE_2, request.getFuncModule()));
            }
        }

        // 如果是是安卓笼车批量分拣，不进行预分拣校验
        if (Objects.nonNull(request.getPdaOperateRequest()) &&
        Objects.equals(request.getPdaOperateRequest().getBizSource(), SortingBizSourceEnum.ANDROID_SORTING_BATCH_TABLE_TROLLEY.getCode())){
            chain.doFilter(request, chain);
            return;
        }

        //是否是三方-合作站点订单分拣到与三方-合作站点绑定的自营站点
        Boolean isPartnerOrderDisToSelfOrderSite = Boolean.FALSE;

        //预分拣站点是三方-合作站点，分拣目的地不是分拣中心时，包裹可能发送到其所属的自营站点，由自营站点配送到三方-合作站点
        if (request.hasPreSite()
        		&& (SiteHelper.isMayBelongSiteExist(waybillSite) || isSmallSite(waybillSite))
                && !SiteHelper.isDistributionCenter(request.getReceiveSite()) ) {

            if (null == waybillSite || null == waybillSite.getCode() || waybillSite.getCode() <= 0) {
                throw new SortingCheckException(SortingResponse.CODE_WAYBILL_SITE_NULL,
                        HintService.getHintWithFuncModule(HintCodeConstants.PRE_SITE_CLOSED_WHEN_SORTING, request.getFuncModule()));
            }

            //预分拣站点和目的站点不符时，从基础资料的站点-合作站点绑定关系中找出三方-合作站点所属站点
            if(!SiteHelper.isMatchOfBoxBelongSiteAndReceivedSite(waybillSite.getCode(), request.getsReceiveSiteCode())){
                Integer belongSiteCode = baseMajorManager.getPartnerSiteBySiteId(waybillSite.getCode());
                if (!SiteHelper.isMatchOfBoxBelongSiteAndReceivedSite(belongSiteCode, request.getsReceiveSiteCode())) {
                    throw new SortingCheckException(SortingResponse.CODE_39000,
                            HintService.getHintWithFuncModule(HintCodeConstants.SITE_NOT_EQUAL_RECEIVE_SITE, request.getFuncModule()));
                } else {
                    isPartnerOrderDisToSelfOrderSite = Boolean.TRUE;
                }
            }
        }

        request.setPartnerOrderDisToSelfOrderSite(isPartnerOrderDisToSelfOrderSite);

        chain.doFilter(request, chain);

    }

    /**
     * 是否是小站；true 是小站
     * @param preSite
     * @return
     */
    private boolean isSmallSite(Site preSite){
        if(preSite == null || preSite.getCode() == null || preSite.getCode() <= 0){
            return false;
        }
        Integer belongSiteCode = baseMajorManager.getPartnerSiteBySiteId(preSite.getCode());

        return belongSiteCode != null && belongSiteCode > 0;
    }
}
