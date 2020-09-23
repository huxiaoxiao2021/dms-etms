package com.jd.bluedragon.distribution.ver.filter.filters;

import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.domain.Site;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.SiteHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author dudong
 * @date 2016/3/1
 */
public class ZiTiGuiFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    public static final Integer siteType = 1024;
    private static final Integer DISTRIBUTE_CENTER_TYPE = 64;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private BaseService baseService;

    @Override
    public void doFilter(FilterContext request, FilterChain chain) throws Exception {

        Site waybillSite = request.getWaybillSite();

        // 如果获取的本地缓存站点为B商家，直接调用基础资料站点接口获取站点1
        // 跨分拣中心分拣时由于预分拣站点和B商家ID重复问题
        if (null != waybillSite && waybillSite.getType().equals(siteType)) {
            logger.info("预分拣站点ID重复，获取基础资料站点");
            waybillSite = this.getSite(request.getReceiveSiteCode());
        }
        //endregion

        //region 是否是自提柜订单分拣到与自提柜绑定的站点
        Boolean isSelfOrderDisToSelfOrderSite = Boolean.FALSE;
        if (BusinessUtil.isZiTiGui(request.getWaybillCache().getSendPay())) {
            if (SiteHelper.isPickup(request.getReceiveSite())) {
                throw new SortingCheckException(SortingResponse.CODE_29116,
                        SortingResponse.MESSAGE_29116);
            }
            // 自提柜跨分拣取消提示
            if (! "28".equals(request.getsReceiveSiteSubType()) && !DISTRIBUTE_CENTER_TYPE.equals(request.getReceiveSite().getType())) {
                Integer selfhelpBoxBelongSiteCode = null;
                if(waybillSite != null){
                    selfhelpBoxBelongSiteCode = baseService.getSiteSelfDBySiteCode(waybillSite.getCode());
                }
                if (!SiteHelper.isMatchOfBoxBelongSiteAndReceivedSite(selfhelpBoxBelongSiteCode, request.getsReceiveSiteCode())) {
                    throw new SortingCheckException(SortingResponse.CODE_39116, SortingResponse.MESSAGE_39116);
                } else {
                    isSelfOrderDisToSelfOrderSite = Boolean.TRUE;
                }
            }
        } else if (! BusinessUtil.isZiTiGui(request.getWaybillCache().getSendPay()) && "28".equals(request.getsReceiveSiteSubType())) {
            throw new SortingCheckException(SortingResponse.CODE_29209,
                    SortingResponse.MESSAGE_29209);
        }

        request.setSelfOrderDisToSelfOrderSite(isSelfOrderDisToSelfOrderSite); //fixme 更改domain
        
        chain.doFilter(request, chain);
    }

    private Site getSite(Integer code) {

        BaseStaffSiteOrgDto dto = null;
        try {
            dto = baseMajorManager.getBaseSiteBySiteId(code);
        } catch (Exception e) {
            logger.error("获取站点名称失败:code={}" , code, e);
            return null;
        }
        if (null == dto) {
            logger.warn("没有对应站点:code={}" , code);
            return null;
        }
        Site site = new Site();
        site.setCode(dto.getSiteCode());
        site.setName(dto.getSiteName());
        String dmsSiteCode = dto.getDmsSiteCode() != null ? dto.getDmsSiteCode() : "";
        site.setDmsCode(dmsSiteCode);
        site.setType(dto.getSiteType());
        site.setSubType(dto.getSubType());
        site.setOrgId(dto.getOrgId());
        site.setSiteBusinessType(dto.getSiteBusinessType());

        return site;
    }
}
