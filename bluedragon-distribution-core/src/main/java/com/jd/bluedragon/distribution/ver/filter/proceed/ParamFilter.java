package com.jd.bluedragon.distribution.ver.filter.proceed;

import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.ver.domain.FilterRequest;
import com.jd.bluedragon.distribution.ver.domain.Site;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.distribution.ver.service.CancelWaybillService;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author dudong
 * @date 2016/2/29
 */
public class ParamFilter implements Filter {
    private static final Log logger  = LogFactory.getLog(ParamFilter.class);
    @Autowired
    private BoxService boxService;

    @Autowired
    private SiteService siteService;

    @Autowired
    private WaybillService waybillService;

    @Autowired
    private CancelWaybillService cancelWaybillService;

    @Override
    public void doFilter(FilterRequest request, FilterChain chain) throws Exception {
        if(logger.isInfoEnabled()){
            logger.info("do filter process...");
        }

        String waybillCode = WaybillUtil.getWaybillCode(request.getPackageCode());
        request.setWaybillCode(waybillCode); //fixme 改变domain

        //region  参数判断
        if (StringHelper.isEmpty(request.getBoxCode())) {
            throw  new SortingCheckException(SortingResponse.CODE_29000, SortingResponse.MESSAGE_29000);
        }
        if (!NumberHelper.isPositiveNumber(request.getCreateSiteCode())) {
            throw  new SortingCheckException(SortingResponse.CODE_29200, SortingResponse.MESSAGE_29200);
        }
        if (!NumberHelper.isPositiveNumber(request.getReceiveSiteCode())) {
            throw  new SortingCheckException(SortingResponse.CODE_29201, SortingResponse.MESSAGE_29201);
        }
        if (StringHelper.isEmpty(waybillCode)) {
            throw  new SortingCheckException(SortingResponse.CODE_29100, SortingResponse.MESSAGE_29100);
        }


        // 箱子判断
        CallerInfo boxInfo = Profiler.registerInfo("DMS.BASE.VER.SortingResource.execSorting.box.get", false, true);
        Box box = this.boxService.findBoxByCode(request.getBoxCode());
        request.setBox(box); //fixme 改变domain
        Profiler.registerInfoEnd(boxInfo);
        if (BusinessUtil.isBoxcode(request.getBoxCode()) && (box == null || box.getBoxCode() == null)) {
            throw  new SortingCheckException(SortingResponse.CODE_29001, SortingResponse.MESSAGE_29001);
        }

        // 站点判断
        CallerInfo receiveSiteInfo = Profiler.registerInfo("DMSVER.SortingResource.execSorting.receiveSite.get", false, true);
        Site receiveSite = this.siteService.get(request.getReceiveSiteCode());
        request.setReceiveSite(receiveSite); //fixme 改变domain
        Profiler.registerInfoEnd(receiveSiteInfo);
        if (receiveSite == null) {
            throw  new SortingCheckException(SortingResponse.CODE_29202, request.getReceiveSiteCode()+SortingResponse.MESSAGE_29202);
        }

        String sReceiveSiteSubType = String.valueOf(receiveSite.getSubType());
        request.setsReceiveSiteSubType(sReceiveSiteSubType); //fixme 改变domain
        String sReceiveSiteCode = (BusinessUtil.isBoxcode(request.getBoxCode()) && null != box && null != box.getReceiveSiteCode()) ? String.valueOf(box
                .getReceiveSiteCode()) : String.valueOf(request.getReceiveSiteCode());
        request.setsReceiveSiteCode(sReceiveSiteCode); //fixme 改变domain
        // 箱子的收货站点和站点类型 (中转站和速递中心判断使用)
        CallerInfo receiveOrBoxSiteInfo = Profiler.registerInfo("DMSVER.SortingResource.execSorting.receiveOrBoxSite.get", false, true);
        Site sReceiveBoxSite = this.siteService.get(Integer.valueOf(sReceiveSiteCode));
        request.setsReceiveBoxSite(sReceiveBoxSite); // fixme 改变domain
        Profiler.registerInfoEnd(receiveOrBoxSiteInfo);

        //运单的校验
        CallerInfo waybillInfo = Profiler.registerInfo("DMSVER.SortingResource.execSorting.waybill.get", false, true);
        Waybill waybill = this.waybillService.getFromCache(request.getWaybillCode());
        request.setWaybill(waybill); //fixme 更改domain
        Profiler.registerInfoEnd(waybillInfo);
        if (waybill == null) {
            throw new SortingCheckException(SortingResponse.CODE_39002,
                    SortingResponse.MESSAGE_39002);
        }
        if(WaybillUtil.isWaybillCode(request.getPackageCode()) && waybill.getQuantity() != null){
        	request.setPackageNum(waybill.getQuantity());
        }else if (WaybillUtil.isPackageCode(request.getPackageCode())){
        	request.setPackageNum(1);
        }
        if (waybill.getOrgId() == null) {
            throw new SortingCheckException(JdResponse.CODE_PARAM_ERROR,
                    SortingResponse.WAYBILL_ERROR_ORGID);
        }

        if (waybill.getWaybillCode() == null) {
            throw new SortingCheckException(JdResponse.CODE_PARAM_ERROR,
                    SortingResponse.WAYBILL_ERROR_WAYBILLCODE);
        }

        chain.doFilter(request, chain);
    }
}
