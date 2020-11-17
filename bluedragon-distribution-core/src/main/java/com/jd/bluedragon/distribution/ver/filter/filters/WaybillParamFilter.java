package com.jd.bluedragon.distribution.ver.filter.filters;

import com.jd.bluedragon.common.domain.WaybillCache;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.base.service.BasicInfoPackService;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.domain.Site;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.distribution.waybill.service.WaybillCacheService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.ldop.basic.dto.BasicTraderNeccesaryInfoDTO;
import com.jd.ldop.basic.dto.ResponseDTO;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by xumei3 on 2018/5/2.
 */
public class WaybillParamFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private WaybillCacheService waybillCacheService;

    @Autowired
    private SiteService siteService;

    @Autowired
    private BasicInfoPackService basicInfoPackService;

    @Override
    public void doFilter(FilterContext request, FilterChain chain) throws Exception {

        String waybillCode = WaybillUtil.getWaybillCode(request.getPackageCode());
        request.setWaybillCode(waybillCode);

        //运单的校验
        WaybillCache waybillCache = this.waybillCacheService.getFromCache(request.getWaybillCode());
        request.setWaybillCache(waybillCache);

        if (waybillCache == null) {
            throw new SortingCheckException(SortingResponse.CODE_39002,
                    SortingResponse.MESSAGE_39002);
        }

        //封装商家基础资料
        if(StringUtils.isNotEmpty(waybillCache.getWaybillSign())){
            //是纯配外单且是信任商家走如下逻辑
            if(BusinessHelper.isAllPureOutWayBillAndIsTrust(waybillCache.getWaybillSign())){
                Waybill waybill =  basicInfoPackService.packBasicInfo(waybillCode);
                request.setWaybill(waybill);
                BasicTraderNeccesaryInfoDTO basicTraderNeccesaryInfoDTO =   basicInfoPackService.getBaseTraderNeccesaryInfo(waybill.getCustomerCode());
                request.setBasicTraderNeccesaryInfoDTO(basicTraderNeccesaryInfoDTO);
            }
        }

        if (waybillCache.getOrgId() == null) {
            throw new SortingCheckException(JdResponse.CODE_PARAM_ERROR,
                    SortingResponse.WAYBILL_ERROR_ORGID);
        }

        if (waybillCache.getWaybillCode() == null) {
            throw new SortingCheckException(JdResponse.CODE_PARAM_ERROR,
                    SortingResponse.WAYBILL_ERROR_WAYBILLCODE);
        }

        if(request.getReceiveSiteCode() != null && request.getReceiveSiteCode() > 0){
            Site receiveSite = this.siteService.get(request.getReceiveSiteCode());
            request.setReceiveSite(receiveSite);
            if (receiveSite == null) {
                throw  new SortingCheckException(SortingResponse.CODE_29202, request.getReceiveSiteCode()+SortingResponse.MESSAGE_29202);
            }
        }
        chain.doFilter(request, chain);
    }
}
