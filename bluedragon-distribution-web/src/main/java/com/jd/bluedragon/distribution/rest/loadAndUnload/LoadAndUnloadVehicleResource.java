package com.jd.bluedragon.distribution.rest.loadAndUnload;

import com.jd.bluedragon.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * 装卸车REST接口
 *
 * @author: hujiping
 * @date: 2020/6/23 10:19
 */
@Component
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class LoadAndUnloadVehicleResource {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());



}
