package com.jd.bluedragon.distribution.rest.jyJobType;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.core.jsf.jyJobType.JyJobTypeManager;
import com.jdl.basic.api.domain.jyJobType.JyJobType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
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
    private JyJobTypeManager jyJobTypeManager;

    /**
     * 获取所有可用的JyJobType列表
     *
     * @return 返回包含JyJobType列表的InvokeResult对象
     */
    @GET
    @Path("/jyJobType/getAllAvailable")
    public InvokeResult<List<JyJobType>> getAllAvailable() {
        InvokeResult<List<JyJobType>> result = new InvokeResult<>();
        result.setData(jyJobTypeManager.getAllAvailable());
        return result;
    }
}
