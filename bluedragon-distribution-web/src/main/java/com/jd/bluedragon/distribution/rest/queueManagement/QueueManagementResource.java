package com.jd.bluedragon.distribution.rest.queueManagement;

import com.jd.bluedragon.Constants;
import org.springframework.stereotype.Controller;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * 智能园区 叫号系统
 * Created by biyubo on 2019/8/21
 */

@Controller
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class QueueManagementResource {

    @POST
    @Path("/qualitycontrol/redeliverycheck")

}
