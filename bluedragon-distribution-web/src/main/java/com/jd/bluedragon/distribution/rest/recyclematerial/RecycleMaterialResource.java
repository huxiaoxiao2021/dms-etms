package com.jd.bluedragon.distribution.rest.recyclematerial;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.utils.PropertiesHelper;
import com.jd.bluedragon.utils.RestHelper;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * 循环物资实操装态更新
 * @author shipeilin
 * @Description: 类描述信息
 * @date 2018年12月10日 11时:16分
 */
@Controller
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class RecycleMaterialResource {

    private final static String url = "/services/recycle/material/updateStatus";

    private final static String PREFIX_VER_URL = "DMS_BUSINESS_ADDRESS";

    /**
     * PDA实操节点更新循环物资状态信息
     *
     * @param vo
     * @return
     */
    @POST
    @Path("/recycle/material/updateStatus")
    @JProfiler(jKey = "DMSWEB.RecycleMaterialResource.updateStatus", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdResponse<String> updateStatus(@RequestBody JSONObject vo){

        return RestHelper.jsonPostForEntity(PropertiesHelper.newInstance().getValue(PREFIX_VER_URL) + url, vo, new TypeReference<JdResponse>(){});
    }
}
