package com.jd.bluedragon.distribution.ver.filter.filters;


import com.jd.bk.common.util.string.StringUtils;
import com.jd.bluedragon.common.domain.WaybillCache;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.domain.Site;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.ql.dispatch.api.DmsToVendorDispatchService;
import com.jd.ql.dispatch.dto.BaseResponse;
import com.jd.ql.dispatch.dto.DmsVendorRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * C网转B网建箱拦截
 * @author shipeilin
 * @Description: 类描述信息
 * @date 2019年01月04日 18时:05分
 */
public class DmsToVendorFilter implements Filter {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DmsToVendorDispatchService dmsToVendorDispatchService;;

    @Autowired
    private SiteService siteService;

    /* 基础资料subType:6420快运中心 */
    private static final Integer SUBTYPE_6420 = 6420;

    /* 基础资料siteType:96车队 */
    public static final Integer BASE_SITE_MOTORCADE = 96;

    @Override
    public void doFilter(FilterContext request, FilterChain chain) throws Exception {
        WaybillCache waybill = request.getWaybillCache();
        Site createSiteCode = siteService.get(request.getCreateSiteCode());
        //当前场地是快运中心 且 发货目的地是车队 且 非城配运单
        if(SUBTYPE_6420.equals(createSiteCode.getSubType())
                && BASE_SITE_MOTORCADE.equals(request.getReceiveSite().getType())
                && !BusinessHelper.isDmsToVendor(waybill.getWaybillSign(), waybill.getSendPay())){

            boolean result = this.dispatchToExpress(createSiteCode.getCode(), waybill.getBusiId(), waybill.getWaybillSign());
            //拦截校验住，给出提示信息
            if(result){
                throw new SortingCheckException(SortingResponse.CODE_39133, SortingResponse.MESSAGE_DMS_TO_VENDOR_ERROR);
            }
        }
        chain.doFilter(request, chain);
    }

    private boolean dispatchToExpress(Integer siteCode, Integer vendorId, String waybillSign) {
        if(siteCode == null || vendorId == null || StringUtils.isEmpty(waybillSign)){
            return false;
        }
        DmsVendorRequest request = new DmsVendorRequest();
        request.setDmsId(siteCode);
        request.setVendorId(vendorId);
        request.setWaybillSign(waybillSign);

        BaseResponse<Boolean> dmsToVendor =  dmsToVendorDispatchService.dispatchToExpress(request);
        if(dmsToVendor.getCode() != BaseResponse.OK_CODE || dmsToVendor.getData() ==null || !dmsToVendor.getData()) {
            logger.warn("C网转B网校验不通过，参数：{}；结果：{}", JsonHelper.toJson(request), JsonHelper.toJson(dmsToVendor));
        }
        return dmsToVendor.getData();
    }
}
