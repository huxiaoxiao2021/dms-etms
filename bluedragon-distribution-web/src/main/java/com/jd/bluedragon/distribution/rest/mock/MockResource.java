package com.jd.bluedragon.distribution.rest.mock;

import com.jd.bluedragon.Constants;
import com.jd.etms.api.common.dto.CommonDto;
import com.jd.etms.api.common.enums.RouteProductEnum;
import com.jd.etms.api.recommendroute.resp.RecommendRouteResp;
import com.jd.etms.sdk.compute.RouteComputeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Date;

@Component
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class MockResource {
    private static final Logger log = LoggerFactory.getLogger(MockResource.class);

    @Autowired
    private RouteComputeUtil routeComputeUtil;
    @GET
    @Path("/mock/queryRecommendRoute")
    public CommonDto<RecommendRouteResp> queryRecommendRoute(@QueryParam("startNode")String startNode,
                                                             @QueryParam("endNodeCode") String endNodeCode,
                                                             @QueryParam("routeProduct") String routeProduct){

        RouteProductEnum productEnum = RouteProductEnum.valueOf(routeProduct);
        return routeComputeUtil.queryRecommendRoute(startNode, endNodeCode, new Date(), productEnum);
    }
}
