package com.jd.bluedragon.distribution.rest.reverse;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.response.BaseResponse;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.reverse.domain.ReverseReject;
import com.jd.bluedragon.distribution.reverse.domain.ReverseSendWms;
import com.jd.bluedragon.distribution.reverse.service.ReverseRejectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Component
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class ReverseRejectResource {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private BaseService tBaseService;

	@Autowired
	private ReverseRejectService reverseRejectService;

	@Autowired
	private WaybillCommonService waybillCommonService;

	@GET
	@Path("/base/getWaybill/{code}")
	public BaseResponse getWaybill(@PathParam("code") String code) {
		this.log.info("getWaybill is {}", code);

		Waybill wb = waybillCommonService.getWaybillFromOrderService(code);
		if (null != wb) {
			BaseResponse response = new BaseResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
			response.setCarCode(wb.getType().toString());
			response.setCarId(wb.getSendPay());
			return response;
		} else {
			this.log.warn("{}获取订单中间件ooms订单数据失败",code);
			Waybill hwb = waybillCommonService.getHisWaybillFromOrderService(code);
			if (null != hwb) {
				BaseResponse response = new BaseResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
				response.setCarCode(hwb.getType().toString());
				response.setCarId(hwb.getSendPay());
				return response;
			} else {
				this.log.warn("{}获取订单中间件ooms历史表订单数据失败",code);
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
