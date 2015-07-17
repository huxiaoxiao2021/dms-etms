package com.jd.bluedragon.distribution.rest.order;

import java.math.BigDecimal;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.order.domain.OrderBankResponse;
import com.jd.bluedragon.distribution.order.service.OrderBankService;

@Controller
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class OrderBankResource {

	@Autowired
	private OrderBankService orderBankService;

	@GET
	@Path("/orderbank/discount")
	public BigDecimal getDiscount(@QueryParam("orderId") String orderId) {
		return this.orderBankService.getDiscount(orderId);
	}

	@GET
	@Path("/orderbank/shouldpay")
	public BigDecimal getShouldPay(@QueryParam("orderId") String orderId, @QueryParam("pin") String pin) {
		return this.orderBankService.getShouldPay(orderId, pin);
	}
	
	@GET
	@Path("/orderbank/order")
	public OrderBankResponse getOrderBankResponse(@QueryParam("orderId") String orderId) {
		return this.orderBankService.getOrderBankResponse(orderId);
	}

}
