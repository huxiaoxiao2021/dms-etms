package com.jd.bluedragon.distribution.rest.consumable;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.consumable.domain.DmsConsumableRelationCondition;
import com.jd.bluedragon.distribution.consumable.domain.DmsConsumableRelationDetailInfo;
import com.jd.bluedragon.distribution.consumable.service.DmsConsumableRelationService;
import com.jd.bluedragon.distribution.external.service.DmsPackingConsumableService;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by hanjiaxing1 on 2018/8/16.
 */
@Component
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class DmsPackingConsumableResource {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DmsPackingConsumableService dmsPackingConsumableService;

    @Autowired
    private DmsConsumableRelationService dmsConsumableRelationService;

    @GET
    @Path("/packing/info/{dmsId}")
    @JProfiler(jKey = "DMS.WEB.DmsPackingConsumableResource.getInfoByDmsId", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdResponse getInfoByDmsId(@PathParam("dmsId") Integer dmsId) {
        JdResponse jdResponse = dmsPackingConsumableService.getPackingConsumableInfoByDmsId(dmsId);

        DmsConsumableRelationCondition dmsConsumableRelationCondition = new DmsConsumableRelationCondition();
        dmsConsumableRelationCondition.setDmsId(910);
        try {
            PagerResult<DmsConsumableRelationDetailInfo> pager = dmsConsumableRelationService.queryDetailInfoByPagerCondition(dmsConsumableRelationCondition);
        } catch (Exception e) {
            log.error("getInfoByDmsId:{}",dmsId,e);
        }
        return jdResponse;
    }

    @GET
    @Path("/packing/detail/{code}")
    @JProfiler(jKey = "DMS.WEB.DmsPackingConsumableResource.getInfoByCode", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdResponse getInfoByCode(@PathParam("code") String code) {

        JdResponse jdResponse = dmsPackingConsumableService.getPackingConsumableInfoByCode(code);

//        DmsConsumableRelationCondition dmsConsumableRelationCondition = new DmsConsumableRelationCondition();
//        dmsConsumableRelationCondition.setDmsId(910);
//        try {
//            PagerResult<DmsConsumableRelationDetailInfo> pager = dmsConsumableRelationService.queryDetailInfoByPagerCondition(dmsConsumableRelationCondition);
//        } catch (Exception e) {
//            log.error(e);
//        }
        return jdResponse;
    }

    @GET
    @Path("/packing/confirmStatus/{waybillCode}")
    @JProfiler(jKey = "DMS.WEB.DmsPackingConsumableResource.getPackingConsumableConfirmStatus", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdResponse<Boolean> getPackingConsumableConfirmStatus(@PathParam("waybillCode") String waybillCode) {

        JdResponse jdResponse = dmsPackingConsumableService.getConfirmStatusByWaybillCode(waybillCode);

        return jdResponse;
    }
}
