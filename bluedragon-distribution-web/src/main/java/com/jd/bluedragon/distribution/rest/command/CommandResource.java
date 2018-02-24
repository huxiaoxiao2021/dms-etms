package com.jd.bluedragon.distribution.rest.command;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.resteasy.annotations.GZIP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.command.JdCommandService;

/**
 * 根据运单打印相关RESTful接口
 * Created by wangtingwei on 2016/4/8.
 */
@Component
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class CommandResource {

    private static final Log logger= LogFactory.getLog(CommandResource.class);

    @Autowired
    @Qualifier("jsonCommandService")
    private JdCommandService jdCommandService;
    /**
     * 
     * @param jsonReqest
     * @return
     */
    @POST
    @GZIP
    @Path("/command/execute")
    public String execute(String jsonCommand){
       return jdCommandService.execute(jsonCommand);
    }
}
