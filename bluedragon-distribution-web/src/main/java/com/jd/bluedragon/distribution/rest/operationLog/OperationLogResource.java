package com.jd.bluedragon.distribution.rest.operationLog;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.operationLog.domain.OperationLog;
import com.jd.bluedragon.distribution.operationLog.service.OperationLogService;

@Controller
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class OperationLogResource {
	
	@Autowired
    private OperationLogService operationLogService;

	@POST
	@Path("/OperationLogResource/add")
	public JdResponse add(OperationLog operationLog) {
		try {
			if (operationLog == null || operationLog.getLogType() == null)
				return new JdResponse(JdResponse.CODE_PARAM_ERROR, JdResponse.MESSAGE_PARAM_ERROR);
			operationLog.setUrl("/OperationLogResource/add");
			operationLogService.add(operationLog);
		} catch (Exception e) {
			return new JdResponse(JdResponse.CODE_SERVICE_ERROR, JdResponse.MESSAGE_SERVICE_ERROR);
		}
		return new JdResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
	}
}
