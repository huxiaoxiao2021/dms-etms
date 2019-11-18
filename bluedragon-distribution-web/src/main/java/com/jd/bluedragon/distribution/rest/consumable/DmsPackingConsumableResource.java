package com.jd.bluedragon.distribution.rest.consumable;

import IceInternal.Ex;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.Pager;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.distribution.consumable.domain.DmsConsumableRelation;
import com.jd.bluedragon.distribution.consumable.domain.DmsConsumableRelationCondition;
import com.jd.bluedragon.distribution.consumable.domain.DmsConsumableRelationDetailInfo;
import com.jd.bluedragon.distribution.consumable.service.DmsConsumableRelationService;
import com.jd.bluedragon.distribution.consumable.service.PackingConsumableInfoService;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumableRecordService;
import com.jd.bluedragon.distribution.external.service.DmsPackingConsumableService;
import com.jd.bluedragon.external.gateway.service.AbnormalReportingGatewayService;
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

    @Autowired
    private AbnormalReportingGatewayService abnormalReportingGatewayService;

    @GET
    @Path("/packing/info/{dmsId}")
    public JdCResponse getInfoByDmsId(@PathParam("dmsId") Integer dmsId) {
        JdCResponse jdResponse = abnormalReportingGatewayService.getDutyDepartment("JDVA00049183927-2-2-");
        return jdResponse;
    }

    @GET
    @Path("/packing/detail/{code}")
    public JdCResponse getInfoByCode(@PathParam("code") String code) {

        JdCResponse jdResponse = abnormalReportingGatewayService.getAllAbnormalReason("bjxings");
        return jdResponse;
    }
}
