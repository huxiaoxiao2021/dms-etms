package com.jd.bluedragon.distribution.rest.reverse;


import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.reverse.service.ReverseSendPopMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Component
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class ReversePopMessageResource {
    @Autowired
    private ReverseSendPopMessageService reverseSendPopMessageService;

    @GET
    @Path("/popMessage/sendMessage/{waybillCode}")
    @Produces("text/plain; charset=utf-8")
    public String sendMessage(@PathParam("waybillCode") String waybillCode) {
        return reverseSendPopMessageService.sendPopMessageForTest(waybillCode);
    }

    @GET
    @Path("/popMessage/getMessage/{waybillCode}")
    @Produces("text/plain; charset=utf-8")
    public String getMessage(@PathParam("waybillCode") String waybillCode) {
        return reverseSendPopMessageService.getPopMessageForTest(waybillCode);
    }

}
