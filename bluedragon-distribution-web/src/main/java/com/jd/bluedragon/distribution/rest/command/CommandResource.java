package com.jd.bluedragon.distribution.rest.command;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.PopPrintRequest;
import com.jd.bluedragon.distribution.api.response.PopPrintResponse;
import com.jd.bluedragon.distribution.command.JdCommand;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.popPrint.domain.ResidentTypeEnum;
import com.jd.bluedragon.distribution.print.request.PrintCompleteRequest;
import com.jd.bluedragon.distribution.print.service.PackagePrintInternalService;
import com.jd.bluedragon.distribution.rest.IpUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
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

    private static final Log logger = LogFactory.getLog(CommandResource.class);

    @Autowired
    @Qualifier("packagePrintInternalService")
    private PackagePrintInternalService packagePrintInternalService;

    /**
     * 
     * @param jsonCommand
     * @return
     */
    @POST
    @GZIP
    @Path("/command/execute")
    public String execute(String jsonCommand, HttpServletRequest request){
        logger.info("remoteAddr :"+ JSON.toJSONString(request));
        String remoteAddr = request.getRemoteAddr();
        logger.info("remoteAddr :"+remoteAddr);
        String ipAddress = IpUtil.getIpAddress(request);
        logger.info("ipAddress :"+ipAddress);

        return packagePrintInternalService.getPrintInfo(jsonCommand);
    }

    @POST
    @Path("/command/printComplete")
    @JProfiler(jKey = "DMS.WEB.CommandResource.printComplete", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdResult<Boolean> printComplete(JdCommand<PrintCompleteRequest> request) {

        if (request == null
                || request.getData() == null
                || !WaybillUtil.isWaybillCode(request.getData().getWaybillCode())
                || request.getData().getOperateSiteCode() == null || request.getData().getOperateSiteCode() == 0
                || request.getData().getOperatorCode() == null) {

            JdResult<Boolean> jdResult = new JdResult<>();
            jdResult.setCode(JdResponse.CODE_PARAM_ERROR);
            jdResult.setMessage(JdResponse.MESSAGE_PARAM_ERROR);
            return jdResult;
        }

        // 分拣中心首次打印
        request.getData().setSortingFirstPrint(1);

        return packagePrintInternalService.printComplete(request);
    }
}
