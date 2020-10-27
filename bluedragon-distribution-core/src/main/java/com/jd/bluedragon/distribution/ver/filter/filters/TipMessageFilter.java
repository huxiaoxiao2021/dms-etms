package com.jd.bluedragon.distribution.ver.filter.filters;

import com.jd.bluedragon.core.base.ColdChainQuarantineManager;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.domain.Site;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class TipMessageFilter implements Filter {

    @Autowired
    private SiteService siteService;

    @Autowired
    private ColdChainQuarantineManager coldChainQuarantineManager;

    //快运中心子类型
    private static final Integer SUBTYPE_6420 = 6420;

    private static final String TIP_MESSAGE_NEED_ADD_QUARANTINE = "此运单有检疫证，若更换票号请录入";

    private static final Log logger = LogFactory.getLog(WaybillConsumableFilter.class);

    @Override
    public void doFilter(FilterContext request, FilterChain chain) throws Exception {
        logger.info("建包提示语获取...");
        //1.检疫证票号提示语
        Site createSite = siteService.get(request.getCreateSiteCode());
        //如果操作站点是快运中心（siteType=6420）则校验，否则不处理
        if(createSite != null && createSite.getSubType() != null && createSite.getSubType().equals(SUBTYPE_6420)){
            List<String> tipMessageList = new ArrayList<String>();
            request.setTipMessages(tipMessageList);
            if(isWaybillNeedAddQuarantine(request.getWaybillCode(),request.getCreateSiteCode())){
                tipMessageList.add(TIP_MESSAGE_NEED_ADD_QUARANTINE);
            }

        }

        chain.doFilter(request, chain);
    }

    /**
     * 快运发货判断是否需要提示录入检疫证票号
     * @param waybillCode
     * @param siteCode
     * @return
     */
    private Boolean isWaybillNeedAddQuarantine(String waybillCode,Integer siteCode) {
        logger.info("查询是否需要录入检疫证票号...");

        if (StringUtils.isBlank(waybillCode) || siteCode == null) {
            return false;
        }

        return coldChainQuarantineManager.isWaybillNeedAddQuarantine(waybillCode,siteCode);
    }
}
