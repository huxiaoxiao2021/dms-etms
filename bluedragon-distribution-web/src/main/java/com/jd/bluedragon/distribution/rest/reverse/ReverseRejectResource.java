package com.jd.bluedragon.distribution.rest.reverse;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.response.BaseResponse;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.reverse.domain.ReverseReject;
import com.jd.bluedragon.distribution.reverse.domain.ReverseSendSpwmsTest;
import com.jd.bluedragon.distribution.reverse.domain.ReverseSendWms;
import com.jd.bluedragon.distribution.reverse.service.ReverseRejectService;
import com.jd.bluedragon.distribution.reverse.service.ReverseSendService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.wss.service.ReverseWssService;
import com.jd.bluedragon.utils.XmlHelper;

@Component
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class ReverseRejectResource {

	private final Log logger = LogFactory.getLog(this.getClass());

	@Autowired
	private BaseService tBaseService;

	@Autowired
	private ReverseRejectService reverseRejectService;

	@Autowired
	private WaybillCommonService waybillCommonService;

	@GET
	@Path("/base/getWaybill/{code}")
	public BaseResponse getWaybill(@PathParam("code") String code) {
		this.logger.info("getWaybill is " + code);

		Waybill wb = waybillCommonService.getWaybillFromOrderService(code);
		if (null != wb) {
			BaseResponse response = new BaseResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
			response.setCarCode(wb.getType().toString());
			response.setCarId(wb.getSendPay());
			return response;
		} else {
			this.logger.error(code + "获取订单中间件ooms订单数据失败");
			Waybill hwb = waybillCommonService.getHisWaybillFromOrderService(code);
			if (null != hwb) {
				BaseResponse response = new BaseResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
				response.setCarCode(hwb.getType().toString());
				response.setCarId(hwb.getSendPay());
				return response;
			} else {
				this.logger.error(code + "获取订单中间件ooms历史表订单数据失败");
				BaseResponse response = new BaseResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
				response.setCarCode("none");
				response.setCarId("none");
				return response;
			}
		}
	}

	@POST
	@Path("/reverse/reject")
	public Boolean getOrderProducts(ReverseReject reverseReject) {
		this.reverseRejectService.reject(reverseReject);
		return Boolean.TRUE;
	}

	@GET
	@Path("/reverse/base/{orderid}")
	public Boolean sendbase(@PathParam("orderid") String orderid) {
		ReverseSendWms send = tBaseService.getWaybillByOrderCode(orderid);
		if (null == send) {
			System.out.println("调用运单WSS数据为空");
		} else {
			System.out.println(send.getProList());
			System.out.println(send.getSendCode());
			System.out.println(send.getOrderId());
		}
		return Boolean.TRUE;
	}

}
