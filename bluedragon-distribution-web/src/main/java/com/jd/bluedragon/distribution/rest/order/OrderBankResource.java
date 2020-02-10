package com.jd.bluedragon.distribution.rest.order;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.order.domain.OrderBankResponse;
import com.jd.bluedragon.distribution.order.service.OrderBankService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.math.BigDecimal;

@Controller
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class OrderBankResource {

	@Autowired
	private OrderBankService orderBankService;

	@GET
	@Path("/orderbank/shouldpay")
    @JProfiler(jKey = "DMS.WEB.OrderBankResource.getShouldPay", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public BigDecimal getShouldPay(@QueryParam("orderId") String orderId, @QueryParam("pin") String pin) {
		return this.orderBankService.getShouldPay(orderId, pin);
	}
	
	@GET
	@Path("/orderbank/order")
    @JProfiler(jKey = "DMS.WEB.OrderBankResource.getOrderBankResponse", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public OrderBankResponse getOrderBankResponse(@QueryParam("orderId") String orderId) {
		return this.orderBankService.getOrderBankResponse(orderId);
	}

}
