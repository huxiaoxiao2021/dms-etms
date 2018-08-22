package com.jd.bluedragon.distribution.rest.consumable;

import IceInternal.Ex;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.consumable.domain.DmsConsumableRelation;
import com.jd.bluedragon.distribution.consumable.domain.DmsConsumableRelationCondition;
import com.jd.bluedragon.distribution.consumable.domain.DmsConsumableRelationDetailInfo;
import com.jd.bluedragon.distribution.consumable.service.DmsConsumableRelationService;
import com.jd.bluedragon.distribution.consumable.service.PackingConsumableInfoService;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumableRecordService;
import com.jd.bluedragon.distribution.external.service.DmsPackingConsumableService;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by hanjiaxing1 on 2018/8/16.
 */
@Component
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class DmsPackingConsumableResource {

    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private DmsPackingConsumableService dmsPackingConsumableService;

    @Autowired
    private DmsConsumableRelationService dmsConsumableRelationService;

    @GET
    @Path("/packing/info/{dmsId}")
    public JdResponse getInfoByDmsId(@PathParam("dmsId") Integer dmsId) {
        JdResponse jdResponse = dmsPackingConsumableService.getPackingConsumableInfoByDmsId(dmsId);

        DmsConsumableRelationCondition dmsConsumableRelationCondition = new DmsConsumableRelationCondition();
        dmsConsumableRelationCondition.setDmsId(910);
        try {
            PagerResult<DmsConsumableRelationDetailInfo> pager = dmsConsumableRelationService.queryDetailInfoByPagerCondition(dmsConsumableRelationCondition);
        } catch (Exception e) {
            logger.error(e);
        }
        return jdResponse;
    }

    @GET
    @Path("/packing/detail/{code}")
    public JdResponse getInfoByCode(@PathParam("code") String code) {

        JdResponse jdResponse = dmsPackingConsumableService.getPackingConsumableInfoByCode(code);

//        DmsConsumableRelationCondition dmsConsumableRelationCondition = new DmsConsumableRelationCondition();
//        dmsConsumableRelationCondition.setDmsId(910);
//        try {
//            PagerResult<DmsConsumableRelationDetailInfo> pager = dmsConsumableRelationService.queryDetailInfoByPagerCondition(dmsConsumableRelationCondition);
//        } catch (Exception e) {
//            logger.error(e);
//        }
        return jdResponse;
    }
}
