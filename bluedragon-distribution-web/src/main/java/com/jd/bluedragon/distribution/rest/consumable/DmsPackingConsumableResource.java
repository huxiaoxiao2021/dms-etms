package com.jd.bluedragon.distribution.rest.consumable;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.consumable.service.DmsConsumableRelationService;
import com.jd.bluedragon.distribution.consumable.service.PackingConsumableInfoService;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumableRecordService;
import com.jd.bluedragon.distribution.external.service.DmsPackingConsumableService;
import com.jd.ql.dms.common.domain.JdResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private DmsPackingConsumableService dmsPackingConsumableService;

    @GET
    @Path("/packing/info/{dmsId}")
    public JdResponse getDriver(@PathParam("dmsId") Integer dmsId) {
        JdResponse jdResponse = dmsPackingConsumableService.getPackingConsumableInfoByDmsId(dmsId);

        return jdResponse;
    }
}
