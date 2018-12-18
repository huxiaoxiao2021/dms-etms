package com.jd.bluedragon.distribution.rest.clientLog;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.ClientRequest;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.dms.logger.aop.BusinessLogWriter;
import com.jd.dms.logger.external.BusinessLogProfiler;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Date;

/**
 * @author tangchunqing
 * @Description: 记录客户端操作日志
 * @date 2018年12月18日 16时:27分
 */
@Component
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class ClientLogResource {

    @POST
    @Path("/clientLog/save")
    public JdResponse save(ClientRequest request) {
        request.setOperateTime(DateHelper.formatDate(new Date(), Constants.DATE_TIME_FORMAT));
        JdResponse response = new JdResponse();
        if (StringUtils.isEmpty(request.getBusinessCode()) || StringUtils.isEmpty(request.getUserErp()) || request.getSiteCode() == null || request.getUserCode() == null) {
            response.setCode(JdResponse.CODE_PARAM_ERROR);
            response.setMessage(JdResponse.MESSAGE_PARAM_ERROR);
            return response;
        }
        //写入自定义日志
        BusinessLogProfiler businessLogProfiler = new BusinessLogProfiler();
        businessLogProfiler.setSourceSys(Constants.BUSINESS_LOG_SOURCE_SYS_DMSWEB);
        businessLogProfiler.setBizType(Constants.BUSINESS_LOG_BIZ_TYPE_CLIENTLOG);
        businessLogProfiler.setOperateType(Constants.BUSINESS_LOG_OPERATE_TYPE_CLIENTLOG);
        businessLogProfiler.setOperateRequest(JsonHelper.toJson(request));
        businessLogProfiler.setTimeStamp(System.currentTimeMillis());
        BusinessLogWriter.writeLog(businessLogProfiler);
        response.setCode(JdResponse.CODE_OK);
        response.setMessage(JdResponse.MESSAGE_OK);
        return response;
    }
}
