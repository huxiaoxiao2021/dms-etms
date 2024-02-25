package com.jd.bluedragon.distribution.rest.jyJobType;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.jyJobType.JyJobTypeService;
import com.jdl.basic.api.domain.jyJobType.JyJobType;
import com.jdl.basic.api.domain.jyJobType.JyJobTypeQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * @author pengchong28
 * @description 工种查询接口
 * @date 2024/2/25
 */
@Controller
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class JyJobTypeResource {
    @Autowired
    private JyJobTypeService jyJobTypeService;

    @GET
    @Path("/jyJobType/getAll")
    public List<JyJobType> getALlJyJobType() {
        return jyJobTypeService.getAll();
    }
}
