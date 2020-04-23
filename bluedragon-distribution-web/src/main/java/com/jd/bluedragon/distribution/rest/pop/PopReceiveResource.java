package com.jd.bluedragon.distribution.rest.pop;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.PopReceiveRequest;
import com.jd.bluedragon.distribution.api.response.PopReceiveResponse;
import com.jd.bluedragon.distribution.operationLog.domain.OperationLog;
import com.jd.bluedragon.distribution.operationLog.service.OperationLogService;
import com.jd.bluedragon.distribution.popReveice.domain.PopReceive;
import com.jd.bluedragon.distribution.popReveice.service.PopReceiveService;
import com.jd.bluedragon.distribution.popReveice.service.TaskPopRecieveCountService;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.Md5Helper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhaohc
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-10-19 下午03:54:11
 * 
 *             POP托寄收货
 */
@Component
@Path(Constants.REST_URL)
@Consumes( { MediaType.APPLICATION_JSON })
@Produces( { MediaType.APPLICATION_JSON })
public class PopReceiveResource {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PopReceiveService popReceiveService;
	
	@Autowired
	private TaskPopRecieveCountService taskPopRecieveCountService;
	
	@Autowired
    private OperationLogService operationLogService;

	@POST
	@Path("/popReceive/save")
	public PopReceiveResponse savePopReceive(PopReceiveRequest request) {
		if (request == null || StringUtils.isBlank(request.getWaybillCode())
				|| request.getCreateSiteCode() == null
				|| request.getCreateSiteCode() < 0
				|| request.getOperatorCode() == null
				|| request.getOperatorCode() < 0
				|| StringUtils.isBlank(request.getOperateTime())) {
			return new PopReceiveResponse(JdResponse.CODE_PARAM_ERROR,
					JdResponse.MESSAGE_PARAM_ERROR);
		}
		
		try {
			if (!PopReceive.EXPRESS_PASS_CHECK.equals(request.getIsPassCheck())) {
				Map<String, Object> paramMap = new HashMap<String, Object>();
				paramMap.put("waybillCode", request.getWaybillCode());
				paramMap.put("thirdWaybillCode", request.getThirdWaybillCode());
				List<PopReceive> popReceives = this.popReceiveService.findListByParamMap(paramMap);
				if (popReceives != null && !popReceives.isEmpty()) {
					return new PopReceiveResponse(PopReceiveResponse.CODE_IS_REPEAT,
							PopReceiveResponse.MESSAGE_IS_REPEAT);
				}
			}

			PopReceive popReceive = toPopReceive(request);
			PopReceive tempPopReceive = this.popReceiveService.findByFingerPrint(popReceive.getFingerPrint());
			if (tempPopReceive != null) {
				return new PopReceiveResponse(PopReceiveResponse.CODE_OK_REPEAT,
						PopReceiveResponse.MESSAGE_OK_REPEAT);
			}
			
			this.popReceiveService.addReceive(popReceive);
			
			try {
				if (!PopReceive.EXPRESS_THIRD_WAYBILLCODE.equals(request.getThirdWaybillCode())) {
					this.taskPopRecieveCountService.insert(popReceive);
				}
				
			} catch (Exception e) {
				this.log.error("PopReceiveResponse --> taskPopRecieveCountService insert ：", e);
			}
			
			this.operationLogService.add(parseOperationLog(request, null,"/popReceive/save"));

			return new PopReceiveResponse(PopReceiveResponse.CODE_OK,
					PopReceiveResponse.MESSAGE_OK);
		} catch (Exception e) {
			this.log.error("PopReceiveResponse --> savePopReceive：", e);
			return new PopReceiveResponse(PopReceiveResponse.CODE_SERVICE_ERROR,
					PopReceiveResponse.MESSAGE_SERVICE_ERROR);
		}
	}

	private PopReceive toPopReceive(PopReceiveRequest request) {
		if (request == null) {
			return null;
		}
		PopReceive popReceive = new PopReceive();
		popReceive.setWaybillCode(request.getWaybillCode());
		popReceive.setThirdWaybillCode(request.getThirdWaybillCode());
		popReceive.setOriginalNum(request.getOriginalNum());
		popReceive.setActualNum(request.getActualNum());
		popReceive.setCreateSiteCode(request.getCreateSiteCode());
		popReceive.setCreateSiteName(request.getCreateSiteName());
		popReceive.setOperatorCode(request.getOperatorCode());
		popReceive.setOperatorName(request.getOperatorName());
		popReceive.setOperateTime(DateHelper.getSeverTime(request
				.getOperateTime()));
		if (request.getActualNum().equals(request.getOriginalNum())) {
			popReceive.setIsReverse(PopReceive.POP_IS_REVERICE);
		} else {
			popReceive.setIsReverse(PopReceive.POP_NOT_REVERICE);
		}
		popReceive.setReceiveType(Constants.POP_QUEUE_EXPRESS);

		this.initFingerPrint(popReceive);

		return popReceive;
	}
	
	private OperationLog parseOperationLog(PopReceiveRequest request, String remark,String url) {
        OperationLog operationLog = new OperationLog();
        operationLog.setWaybillCode(request.getWaybillCode());
        operationLog.setCreateSiteCode(request.getCreateSiteCode());
        operationLog.setCreateSiteName(request.getCreateSiteName());
        operationLog.setCreateUserCode(request.getOperatorCode());
        operationLog.setCreateUser(request.getOperatorName());
		operationLog.setCreateTime(new Date());
        operationLog.setOperateTime(DateHelper.getSeverTime(request
				.getOperateTime()));
        operationLog.setLogType(OperationLog.LOG_TYPE_RECEIVE);
        operationLog.setRemark(remark);
        operationLog.setUrl(url);
        return operationLog;
    }

	private void initFingerPrint(PopReceive popReceive) {
		StringBuilder fingerprint = new StringBuilder("");

		fingerprint.append(popReceive.getWaybillCode()).append("_").append(
				popReceive.getThirdWaybillCode()).append("_").append(
				popReceive.getOriginalNum()).append("_").append(
				popReceive.getActualNum()).append("_").append(
				popReceive.getCreateSiteCode()).append("_").append(
				DateHelper.formatDateTimeMs(popReceive.getOperateTime()))
				.append("_").append(popReceive.getReceiveType()).append("_")
				.append(popReceive.getIsReverse());
		popReceive.setFingerPrint(Md5Helper.encode(fingerprint.toString()));
	}
}
