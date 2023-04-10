package com.jd.bluedragon.distribution.rest.command;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.PopPrintRequest;
import com.jd.bluedragon.distribution.api.response.PopPrintResponse;
import com.jd.bluedragon.distribution.base.service.AbstractBaseUserService;
import com.jd.bluedragon.distribution.command.JdCommand;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.popPrint.domain.ResidentTypeEnum;
import com.jd.bluedragon.distribution.print.request.PrintCompleteRequest;
import com.jd.bluedragon.distribution.print.service.PackagePrintInternalService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.avro.data.Json;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.resteasy.annotations.GZIP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(AbstractBaseUserService.class);

    @Autowired
    @Qualifier("packagePrintInternalService")
    private PackagePrintInternalService packagePrintInternalService;
    @Autowired
    private BaseMajorManager baseMajorManager;

    /**
     *
     * @param jsonCommand
     * @return
     */
    @POST
    @GZIP
    @Path("/command/execute")
    public String execute(String jsonCommand){
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
        log.info("补打回调-{}", JSON.toJSONString(request.getData()));
        //对siteCode =1 进行处理
        if(StringUtils.isNotBlank(request.getData().getOperatorErp()) && request.getData().getOperateSiteCode().equals(1)){
            log.warn("此运单-{}，当前操作erp-{},站点-{}",request.getData().getWaybillCode(),request.getData().getOperatorErp(),request.getData().getOperateSiteCode());
            BaseStaffSiteOrgDto baseStaff = baseMajorManager.getBaseStaffByErpNoCache(request.getData().getOperatorErp());
            if(baseStaff != null){
                request.getData().setOperateSiteCode(baseStaff.getSiteCode());
            }
        }
        return packagePrintInternalService.printComplete(request);
    }
}
