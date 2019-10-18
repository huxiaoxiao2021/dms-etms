package com.jd.bluedragon.distribution.rest;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.external.pdd.DMSExternalInPDDService;
import com.jd.bluedragon.distribution.external.pdd.domain.PDDWaybillPrintInfoDto;
import com.jd.bluedragon.distribution.wss.dto.BaseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * <p>
 *
 * @author wuzuxiang
 * @since 2019/10/18
 **/
@Component
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class PDDResource {

    @Autowired
    private DMSExternalInPDDService dmsExternalInPDDService;
    @GET
    @Path("/get/{waybillCode}")
    public BaseEntity<PDDWaybillPrintInfoDto> get (@PathParam("waybillCode") String waybillCode) {

        return dmsExternalInPDDService.queryPDDWaybillByWaybillCode(waybillCode);

    }
}
