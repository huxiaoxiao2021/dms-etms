package com.jd.bluedragon.distribution.rest.crosssorting;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.CrossSortingRequest;
import com.jd.bluedragon.distribution.cross.domain.CrossSorting;
import com.jd.bluedragon.distribution.cross.domain.CrossSortingResponse;
import com.jd.bluedragon.distribution.cross.service.CrossSortingService;
import com.jd.bluedragon.utils.JsonHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yanghongqiang on 2015/7/8.
 */

@Component
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class CrossSortingResource {

    private final Log logger = LogFactory.getLog(this.getClass());
    @Autowired
    private CrossSortingService crossSortingService;

    @POST
    @Path("/crosssorting/queryMixBoxSite")
    public CrossSortingResponse queryMixBoxSite(CrossSortingRequest request) {
        logger.info("查询建包发货规则"+JsonHelper.toJson(request));
        CrossSortingResponse response= new CrossSortingResponse();
        try {
            if (null == request || null == request.getCreateDmsCode()
                    || request.getCreateDmsCode() < 1
                    || null == request.getDestinationDmsCode()
                    || request.getDestinationDmsCode() < 1
                    || null == request.getType()
                    || request.getType() < 1) {
                logger.error(JsonHelper.toJson(request));
                response.setCode(JdResponse.CODE_PARAM_ERROR);
                response.setMessage(JdResponse.MESSAGE_PARAM_ERROR);
                return  response;
            }
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("createDmsCode", request.getCreateDmsCode());
            params.put("destinationDmsCode", request.getDestinationDmsCode());
            params.put("type", request.getType());
            List<CrossSorting> mixDmsList = crossSortingService.findMixDms(params);
            response.setCode(JdResponse.CODE_OK);
            response.setMessage(JdResponse.MESSAGE_OK);
            response.setData(mixDmsList);
        } catch (Exception e) {
            logger.error("查询建包规则"+JsonHelper.toJson(request)+e.toString());

            response.setCode(JdResponse.CODE_PARAM_ERROR);
            response.setMessage(JdResponse.MESSAGE_PARAM_ERROR);
        }
        return response;
    }
}
