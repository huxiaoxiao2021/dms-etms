package com.jd.bluedragon.distribution.rest.send;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.send.domain.SendQuery;
import com.jd.bluedragon.distribution.send.service.SendQueryService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by wangtingwei on 2014/12/8.
 */
@Path(Constants.REST_URL)
@Controller
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SendQueryResource {

    @Autowired
    private SendQueryService sendQueryService;
    private final Logger logger = Logger.getLogger(SendQueryResource.class);
    /**
     * 添加发货批次查询日志
     * @param domain 数据对象
     * @return
     */
    @POST
    @Path("/sendquery/put")
    public InvokeResult<Boolean> put(SendQuery domain,@Context HttpServletRequest servletRequest){
        InvokeResult<Boolean> result=new InvokeResult<Boolean>();

        result.success();

        this.logger.error("servletRequest.getHeader()" + servletRequest.getHeader("X-Forwarded-For"));
        String realIP = servletRequest.getHeader("X-Forwarded-For");
        domain.setIpAddress(realIP);

        try{
            result.setData(sendQueryService.insert(domain));
        }catch (Exception ex){
            result.error(ex);
        }
        return result;
    }

    /**
     * 查询发货批次查询日志
     * @param sendCode 发货批次号
     * @return
     */
    @GET
    @Path("/sendquery/get")
    public InvokeResult<List<SendQuery>> get(@QueryParam("sendCode") String sendCode){
        InvokeResult<List<SendQuery>> result=new InvokeResult<List<SendQuery>>();
        result.success();
        try{
            result.setData(sendQueryService.queryBySendCode(sendCode));
        }catch (Exception ex){
            result.error(ex);
        }
        return result;
    }
}
