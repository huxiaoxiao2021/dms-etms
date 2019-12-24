package com.jd.bluedragon.distribution.rest.send;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.send.domain.SendQuery;
import com.jd.bluedragon.distribution.send.service.SendQueryService;
import com.jd.bluedragon.distribution.sendprint.domain.ExpressInfo;
import com.jd.bluedragon.distribution.sendprint.service.ThirdExpressPrintService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final Logger log = LoggerFactory.getLogger(SendQueryResource.class);

    @Autowired
    private SendQueryService sendQueryService;

    @Autowired
    private ThirdExpressPrintService thirdExpressPrintService;

    /**
     * 添加发货批次查询日志
     * @param domain 数据对象
     * @return
     */
    @POST
    @Path("/sendquery/put")
    public InvokeResult<Boolean> put(SendQuery domain,@Context HttpServletRequest servletRequest){
        InvokeResult<Boolean> result=new InvokeResult<Boolean>();
        String realIP = servletRequest.getHeader("X-Forwarded-For");
        if(log.isInfoEnabled()) {
            this.log.info("SendQueryResource.put()：{}", realIP);
        }
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

    /**
     * 获取三方面单
     * @param packageCode
     * @return
     */
    @GET
    @Path("/sendquery/getthirdwaybillpaper/{packagecode}")
    public InvokeResult<ExpressInfo> getThirdWaybillPaper(@PathParam("packagecode") String packageCode){
        InvokeResult<ExpressInfo> result=null;
        try {
            result=thirdExpressPrintService.getThirdExpress(packageCode);
        }catch (Throwable ex){
            log.error("获取三方面单异常",ex);
            result=new InvokeResult<ExpressInfo>();
            result.error(ex);
        }
        return result;
    }
}
