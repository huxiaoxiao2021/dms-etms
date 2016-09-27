package com.jd.bluedragon.distribution.rest.send;

import com.google.common.base.Strings;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.rest.departure.DepartureResource;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.send.service.ReverseDeliveryService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.Md5Helper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class SendCodeResource {

	private static final String IS_AIR_TRANSPORT_CARRIER = "Y";
	private static final Integer TRANSPORT_TYPE_AIR = 1; // 航空

	public static final Integer CODE_SEND_CODE_IS_AIR_TRANSPORT = 30001;
	public static final String MESSAGE_SEND_CODE_IS_AIR_TRANSPORT = "航空批次与承运人类型不一致，确定发车?";

	public static final Integer CODE_CARRIER_IS_AIR_TRANSPORT = 30002;
	public static final String MESSAGE_CARRIER_IS_AIR_TRANSPORT = "批次与航空承运人类型不一致，确定发车?";

	public static final Integer CODE_CARRIER_NOT_FOUND = 409;
	public static final String MESSAGE_CARRIER_NOT_FOUND = "承运人不存在.";
	
	private static final String TASK_REVERSE_SEND_BUSINESS = "30";
	private static final String TASK_REVERSE_SEND_NODIFY = "3";

	@Autowired
	private SiteService siteService;

	@Autowired
	private DeliveryService deliveryService;

	@Autowired
	private DepartureResource departureResource;
	
	@Autowired
	private TaskService taskService;
	
	@Autowired
    ReverseDeliveryService reverseDelivery;

	@GET
	@Path("/departure/check")
	public JdResponse isAirTransportBatch(@QueryParam("siteCode") Integer siteCode,
			@QueryParam("carrierSiteCode") Integer carrierSiteCode, @QueryParam("sendCode") String sendCode) {
		if (siteCode == null || carrierSiteCode == null || Strings.isNullOrEmpty(sendCode)) {
			return new JdResponse(JdResponse.CODE_PARAM_ERROR, JdResponse.MESSAGE_PARAM_ERROR);
		}

		BaseStaffSiteOrgDto carrierSite = this.siteService.getSite(carrierSiteCode);
		if (carrierSite == null) {
			return new JdResponse(CODE_CARRIER_NOT_FOUND, MESSAGE_CARRIER_NOT_FOUND);
		}

		if (!isAirTransportCarrier(carrierSite) && isAirTransportBatch(sendCode)) {
			return new JdResponse(CODE_SEND_CODE_IS_AIR_TRANSPORT, MESSAGE_SEND_CODE_IS_AIR_TRANSPORT);
		} else if (isAirTransportCarrier(carrierSite) && !isAirTransportBatch(sendCode)) {
			return new JdResponse(CODE_CARRIER_IS_AIR_TRANSPORT, MESSAGE_CARRIER_IS_AIR_TRANSPORT);
		}

		return this.departureResource.checkSendStatus(siteCode, sendCode);
	}

	private Boolean isAirTransportCarrier(BaseStaffSiteOrgDto carrierSite) {
		return !Strings.isNullOrEmpty(carrierSite.getAirTransport())
				&& IS_AIR_TRANSPORT_CARRIER.equals(carrierSite.getAirTransport());
	}

	private Boolean isAirTransportBatch(String sendCode) {
		Boolean isAirTransportBatch = Boolean.FALSE;

		List<SendM> sends = this.deliveryService.queryCountByBox(newSend(sendCode));
		for (SendM send : sends) {
			if (send.getTransporttype() == null) {
				break;
			}

			if (TRANSPORT_TYPE_AIR.equals(send.getTransporttype())) {
				isAirTransportBatch = Boolean.TRUE;
			} else {
				isAirTransportBatch = Boolean.FALSE;
			}

			break;
		}

		return isAirTransportBatch;
	}

	private SendM newSend(String sendCode) {
		SendM send = new SendM();
		send.setSendCode(sendCode);
		return send;
	}
	
	@GET
	@Path("/send/repair/sendcode")
	public JdResponse repairSendCode(@QueryParam("sendCode") String sendCode) {
		if (StringHelper.isEmpty(sendCode)) {
			return new JdResponse(JdResponse.CODE_PARAM_ERROR,
					JdResponse.MESSAGE_PARAM_ERROR);
		}

		Task task = this.taskService.findWaybillSendTask(sendCode.trim());
		if (task != null) {
			task.setType(Task.TASK_TYPE_SEND_DELIVERY);
			task.setStatus(Task.TASK_STATUS_UNHANDLED);
			task.setExecuteCount(Task.INITIAL_COUNT);
			task.setExecuteTime(new Date());
			this.taskService.updateBySelective(task);
		} else {
			this.getCreateSiteCodeBysendCode(sendCode);

			task = new Task();
			task.setKeyword1(SendCodeResource.TASK_REVERSE_SEND_NODIFY);
			task.setKeyword2(SendCodeResource.TASK_REVERSE_SEND_BUSINESS);
			task.setCreateSiteCode(this.getCreateSiteCodeBysendCode(sendCode));
			task.setReceiveSiteCode(this.getReceiveSiteCodeBysendCode(sendCode));
			task.setBoxCode(sendCode);
			task.setBody(sendCode);
			task.setExecuteCount(Task.INITIAL_COUNT);
			task.setExecuteTime(new Date());
			task.setFingerprint(this.getFingerprint(sendCode));
			task.setType(Task.TASK_TYPE_SEND_DELIVERY);
			task.setTableName(Task.getTableName(Task.TASK_TYPE_SEND_DELIVERY));
			task.setSequenceName(Task.getSequenceName(Task.TABLE_NAME_SEND));
			task.setOwnSign(BusinessHelper.getOwnSign());

			this.taskService.add(task);
		}

		return new JdResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);

	}
	
	@GET
	@Path("/send/repair/waybillCode")
	public JdResponse repairWaybillCode(@QueryParam("waybillCode") String waybillCode) {
		if (StringHelper.isEmpty(waybillCode)) {
			return new JdResponse(JdResponse.CODE_PARAM_ERROR,
					JdResponse.MESSAGE_PARAM_ERROR);
		}
		String[] waybills = waybillCode.split("&");
if(waybills==null){
			return new JdResponse(JdResponse.CODE_PARAM_ERROR,
					JdResponse.MESSAGE_PARAM_ERROR);}
		if(waybills.length>20)
			return new JdResponse(JdResponse.CODE_PARAM_ERROR,
					JdResponse.MESSAGE_PARAM_ERROR);
		List<String> request = new ArrayList<String>();
		for(String code :waybills){
			request.add(code);
		}
		String message = reverseDelivery.toEmsServer(request);
		if(StringHelper.isEmpty(message))
			return new JdResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
		else
			return new JdResponse(JdResponse.CODE_OK, message);
	}

	private Integer getCreateSiteCodeBysendCode(String sendCode) {
		String[] sendCodeArray = sendCode.split(Constants.SEPARATOR_HYPHEN);

		if (sendCodeArray == null || sendCodeArray[0] == null) {
			return 0;
		}

		return new Integer(sendCodeArray[0]);
	}
	
	private Integer getReceiveSiteCodeBysendCode(String sendCode) {
		String[] sendCodeArray = sendCode.split(Constants.SEPARATOR_HYPHEN);

		if (sendCodeArray == null || sendCodeArray[1] == null) {
			return 0;
		}

		return new Integer(sendCodeArray[1]);
	}
	
	private String getFingerprint(String sendCode) {
		return Md5Helper.encode(sendCode + Constants.SEPARATOR_HYPHEN + SendCodeResource.TASK_REVERSE_SEND_NODIFY);
	}
}
