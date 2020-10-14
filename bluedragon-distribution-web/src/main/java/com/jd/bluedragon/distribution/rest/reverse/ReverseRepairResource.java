package com.jd.bluedragon.distribution.rest.reverse;

import com.google.common.base.Strings;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.Md5Helper;
import com.jd.bluedragon.utils.StringHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import java.util.Date;

@Component
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class ReverseRepairResource {

	private static final String TASK_REVERSE_SEND_BUSINESS = "20";
	private static final String TASK_REVERSE_SEND_NODIFY = "4";

	@Autowired
	private TaskService taskService;

	@Autowired
	private DeliveryService deliveryService;

	@GET
	@Path("/reverse/repair/sendcode")
	public JdResponse repairSendCode(@QueryParam("sendCode") String sendCode) {
		if (!BusinessUtil.isSendCode(sendCode)) {
			return new JdResponse(JdResponse.CODE_PARAM_ERROR, "不是有效的批次号！");
		}
		Task task = this.taskService.findReverseSendTask(sendCode.trim());
		if (task != null) {
			task.setType(Task.TASK_TYPE_DEPARTURE);
			task.setStatus(Task.TASK_STATUS_UNHANDLED);
			task.setExecuteCount(Task.INITIAL_COUNT);
			task.setExecuteTime(new Date());
			this.taskService.updateBySelective(task);
		} else {
			Integer[] sites = BusinessUtil.getSiteCodeBySendCode(sendCode);
			if(sites[0]<=0 || sites[1]<=0){
				return new JdResponse(JdResponse.CODE_PARAM_ERROR, "不是有效的批次号！");
			}
			task = new Task();
			task.setKeyword1(ReverseRepairResource.TASK_REVERSE_SEND_NODIFY);
			task.setKeyword2(ReverseRepairResource.TASK_REVERSE_SEND_BUSINESS);
			task.setCreateSiteCode(sites[0]);
			task.setReceiveSiteCode(sites[1]);
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
	@Path("/reverse/repair/waybill")
	public JdResponse repairWaybill(@QueryParam("sendCode") String sendCode,
			@QueryParam("waybillCode") String waybillCode) {
		if (!Strings.isNullOrEmpty(sendCode) && !Strings.isNullOrEmpty(waybillCode)) {
			SendDetail sendDetail = new SendDetail();
			sendDetail.setSendCode(sendCode);
			sendDetail.setWaybillCode(waybillCode);
			
			if (this.deliveryService.cancelDelivery(sendDetail) > 0) {
				return new JdResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
			} 
		}
		
		return new JdResponse(JdResponse.CODE_OK, "未找到相关数据!");
	}

	private String getFingerprint(String sendCode) {
		return Md5Helper.encode(sendCode + Constants.SEPARATOR_HYPHEN + ReverseRepairResource.TASK_REVERSE_SEND_NODIFY);
	}
}
